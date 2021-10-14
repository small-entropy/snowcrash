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

;; Interceptor for get user profile
(def profile-user-interceptor
  {:name ::profile-user-interceptor
   :enter
   (fn [context]
     (let [{connection :connection
            request :request
            guid :guid} context
           {header-token auth-header} (get request :headers nil)
           path-params (get request :path-params nil)
           user-id (get path-params :user-id nil)
           {documents :documents user :user} (service/get-user-profile connection user-id)]
       (assoc context :response (ok guid documents {:token (not-send header-token)
                                                   :request guid
                                                   :user user}))))})

;; Interceptor for get user profile property
(def profile-user-property-interceptor
  {:name ::profile-user-property-interceptor
   :enter
   (fn [context]
     (let [{connection :connection
            request :request
            guid :guid} context
           {header-token auth-header} (get request :headers nil)
           path-params (get request :path-params nil)
           {user-id :user-id property-id :property-id} path-params
           {document :document user :user} (service/get-user-profile-property connection user-id property-id)]
       (assoc context :response (ok guid document {:token (not-send header-token)
                                                   :request guid
                                                   :user user}))))})

;; Interceptor for create user profile property
(def create-profile-user-property-interceptor
  {:name ::create-profile-user-property-interceptor
   :enter
   (fn [context]
     (let [{connection :connection
            request :request
            guid :guid
            token :token
            is-owner :is-owner
            user-id :user-id
            decoded-id :decoded-id} context
           {key :key value :value} (get request :json-params nil)
           {documents :documents
            user :user} (if (true? is-owner)
                       (service/create-user-profile-property connection user-id key value)
                       (service/create-user-profile-property connection user-id decoded-id key value))]
       (assoc context :response (ok guid documents {:token token
                                                    :request guid
                                                    :user user}))))})

;; Interceptor for update user profile property
(def update-profile-user-property-interceptor
  {:name ::update-profile-user-property-interceptor
   :enter
   (fn [context]
     (let [{connection :connection
            request :request
            guid :guid
            token :token
            is-owner :is-owner
            user-id :user-id
            decoded-id :decoded-id} context
           path-params (get request :path-params nil)
           {property-id :property-id} path-params
           {key :key value :value} (get request :json-params nil)
           {documents :documents
            user :user} (if (true? is-owner)
                                     (service/update-user-profile-property connection property-id user-id key value)
                                     (service/update-user-profile-property connection property-id user-id decoded-id key value))]
       (assoc context :response (ok guid documents {:token token
                                                    :request guid
                                                    :user user}))))})

;; Interceptor for delete user profile property
(def delete-profile-user-property-interceptor
  {:name ::delete-profile-user-property-interceptor
   :enter
   (fn [context]
     (let [{connection :connection
            request :request
            guid :guid
            token :token
            is-owner :is-owner
            user-id :user-id
            decoded-id :decoded-id} context
           {property-id :property-id} (get request :path-params nil)
           {documents :documents
            user :user} (if (true? is-owner)
                                     (service/delete-user-profile-property connection user-id property-id)
                                     (service/delete-user-profile-property connection user-id decoded-id property-id))]
       (assoc context :response (ok guid documents {:token token
                                                    :request guid
                                                    :user user}))))})

;; Interceptor for get user properties
(def properties-user-interceptor
  {:name ::properties-user-interceptor
   :enter
   (fn [context]
     (let [{connection :connection
            guid :guid
            token :token
            is-owner :is-owner
            user-id :user-id
            decoded-id :decoded-id} context
           {documents :documents
            user :user} (if (true? is-owner)
                                     (service/get-user-properties connection user-id)
                                     (service/get-user-properties connection user-id decoded-id))]
       (assoc context :response (ok guid documents {:token token
                                                    :request guid
                                                    :user user}))))})

;; Interceptor for get user property
(def properties-user-property-interceptor
  {:name ::properties-user-property-interceptor
   :enter
   (fn [context]
     (let [{connection :connection
            request :request
            guid :guid
            token :token
            is-owner :is-owner
            user-id :user-id
            decoded-id :decoded-id} context
           {property-id :property-id} (get request :path-params)
           {document :document
            user :user} (if (true? is-owner)
                                     (service/get-user-property connection user-id property-id)
                                     (service/get-user-property connection user-id decoded-id property-id))]
       (assoc context :response (ok guid document {:token token
                                                   :request guid
                                                   :user user}))))})

;; Interceptor for create user property
(def create-user-property-interceptor
  {:name ::create-user-property-interceptor
   :enter
   (fn [context]
     (let [{connection :connection
            request :request
            guid :guid
            token :token
            is-owner :is-owner
            user-id :user-id
            decoded-id :decoded-id} context
           {key :key value :value} (get request :json-params nil)
           {documents :documents
            user :user} (if (true? is-owner)
                                     (service/create-user-property connection user-id key value)
                                     (service/create-user-property connection user-id decoded-id key value))]
       (assoc context :response (ok guid documents {:token token
                                                    :request guid
                                                    :user user}))))})

;; Interceptor for delete user property
(def delete-user-property-interceptor
  {:name ::delete-user-property-interceptor
   :enter
   (fn [context]
     (let [{connection :connection
            request :request
            guid :guid
            token :token
            is-owner :is-owner
            user-id :user-id
            decoded-id :decoded-id} context
           {property-id :property-id} (get request :path-params nil)
           {documents :documents
            user :user} (if (true? is-owner)
                                     (service/delete-user-property connection user-id property-id)
                                     (service/delete-user-property connection user-id decoded-id property-id))]
       (assoc context :response (ok guid documents {:token token
                                                    :request guid
                                                    :user user}))))})