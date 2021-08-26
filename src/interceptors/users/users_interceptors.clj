(ns interceptors.users.users-interceptors
  (:require
    [services.users-service :as service]
    [utils.answers :refer :all]))

(def register-user-interceptor
  {:name ::register-user-interceptor
   :enter
    (fn
      [context]
      (let [request (get context :request nil)
            connection (get request :connection nil)
            {login :login password :password} (get request :json-params nil)
            {token :token document :document} (service/register-user connection login password)]
        (assoc context :response (ok document {:token token }))))})

(def login-user-interceptor
  {:name ::login-user-interceptor
   :enter
    (fn
      [context]
      (let [request (get context :request nil)
            connection (get request :connection nil)
            {login :login password :password} (get request :json-params nil)
            {document :document token :token} (service/login-user connection login password)]
        (assoc context :response (ok document {:token token}))))})