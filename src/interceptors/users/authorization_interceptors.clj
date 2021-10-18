(ns interceptors.users.authorization-interceptors
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
