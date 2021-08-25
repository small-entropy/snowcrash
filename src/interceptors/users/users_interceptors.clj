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
            json-params (get request :json-params nil)
            login (get json-params :login nil)
            password (get json-params :password nil)
            data (service/register-user connection login password)
            token (get data :token nil)
            document (get data :document nil)]
        (assoc context :response (ok document {:token token }))))})