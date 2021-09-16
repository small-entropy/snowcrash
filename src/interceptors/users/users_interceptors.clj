(ns interceptors.users.users-interceptors
  (:require
    [services.users-service :as service]
    [utils.answers :refer :all]
    [utils.helpers :refer :all]
    [utils.constants :refer :all]))

;; Interceptor for register user
(def register-user-interceptor
  {:name ::register-user-interceptor
   :enter
    (fn [context]
      (let [{request :request guid :guid connection :connection} context
            {login :login password :password} (get request :json-params nil)
            {token :token document :document} (service/register-user connection login password)]
        (assoc context :response (created guid document {:token token
                                                         :request guid}))))})
;; Interceptor for login user
(def login-user-interceptor
  {:name ::login-user-interceptor
   :enter
    (fn [context]
      (let [{request :request guid :guid connection :connection} context
            {login :login password :password} (get request :json-params nil)
            {document :document token :token} (service/login-user connection login password)]
        (assoc context :response (ok guid document {:token token
                                                    :request guid}))))})

;; Interceptor for autologin user (call login by token)
(def autologin-user-interceptor
  {:name ::autologin-user-interceptor
   :enter
   (fn [context]
     (let [{request :request guid :guid connection :connection} context
           {header-token auth-header} (get request :headers nil)
           {document :document token :token} (service/login-user connection header-token)]
       (assoc context :response (ok guid document {:token token
                                                   :request guid}))))})

;; Interceptor for change user password
(def change-user-password-interceptor
  {:name ::change-user-password-interceptor
   :enter
   (fn [context]
     (let [{request :request guid :guid connection :connection} context
           {header-token auth-header} (get request :headers nil)
           {password :password} (get request :json-params nil)
           {document :document} (service/change-user-password connection header-token password)]
       (assoc context :response (ok guid document {:token header-token
                                                   :request guid}))))})

;; Interceptor for logout user
(def logout-user-interceptor
  {:name ::logout-user-interceptor
   :enter
   (fn [context]
     (let [{request :request guid :guid} context
           {header-token auth-header} (get request :headers nil)
           {document :document token :token} (service/logout-user header-token)]
       (assoc context :response (ok guid document {:token token
                                                   :request guid}))))})

;; Interceptor for get paginate list users list
(def list-users-interceptor
  {:name ::list-users-interceptor
   :enter
    (fn [context]
      (let [{request :request guid :guid connection :connection} context
            query-params (get request :query-params nil)
            limit (Integer/parseInt (get query-params :limit 10))
            skip (Integer/parseInt (get query-params :skip 0))
            {header-token auth-header} (get request :headers nil)
            {documents :documents token :token total :total} (service/get-users-list connection header-token limit skip)]
        (assoc context :response (ok guid documents {:token token
                                                     :request guid
                                                     :total total
                                                     :limit limit
                                                     :skip skip}))))})
;; Interceptor for get user document
(def entity-users-interceptor
  {:name ::entity-users-interceptor
   :enter
   (fn [context]
     (let [{is-owner :is-owner
            user-id :user-id
            guid :guid
            token :token
            connection :connection
            decoded-id :decoded-id} context
           {document :document} (if (nil? token)
                                  (service/get-user connection user-id)
                                  (service/get-user connection user-id decoded-id is-owner))]
       (assoc context :response (ok guid document {:token (not-send token)
                                                   :request guid}))))})