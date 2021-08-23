(ns interceptors.users.users-interceptors
  (:require
    [services.users-service :as service]
    [clojure.data.json :as json]))

(def register-user-interceptor
  {:name ::register-user-interceptor
   :enter
    (fn
      [context]
      (let [request (get context :request nil)
            database (get request :database nil)
            json-params (get request :json-params nil)
            login (get json-params :login nil)
            password (get json-params :password nil)
            data (service/register-user database login password)
            token (get data :token nil)
            document (get data :document nil)]
        (assoc context :response {:body (json/write-str {:id (str (get document :_id nil))
                                                         :login (get document :login)
                                                         :token token})
                                  :status 200
                                  :headers {"Content-Type" "application/json"}})))})