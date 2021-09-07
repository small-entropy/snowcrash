(ns interceptors.users.users-interceptors
  (:require
    [services.users-service :as service]
    [utils.answers :refer :all]))

(def register-user-interceptor
  {:name ::register-user-interceptor
   :enter
    (fn
      [context]
      (let [{request :request guid :guid connection :connection} context
            {login :login password :password} (get request :json-params nil)
            {token :token document :document} (service/register-user connection login password)]
        (assoc context :response (created guid document {:token token
                                                         :request guid}))))})

(def login-user-interceptor
  {:name ::login-user-interceptor
   :enter
    (fn
      [context]
      (let [{request :request guid :guid connection :connection} context
            {login :login password :password} (get request :json-params nil)
            {document :document token :token} (service/login-user connection login password)]
        (assoc context :response (ok guid document {:token token
                                                    :request guid}))))})

(def autologin-user-interceptor
  {:name ::login-user-interceptor
   :enter
         (fn
           [context]
           (let [{request :request guid :guid connection :connection} context
                 {token "authorization" } (get request :headers nil)
                 {document :document token :token} (service/login-user connection token)]
             (assoc context :response (ok guid document {:token token
                                                         :request guid}))))})