(ns snowcrash.main
  (:gen-class)
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [snowcrash.service :as service]
            [database.common.indexes :as i]
            [utils.connection :as con]
            [interceptors.common.database_interceptor :as dbi]
            [interceptors.users.users-interceptors :as users-interceptors]
            [utils.constants :refer :all]))

;; Default port
(defonce port 8822)

(defn get-expanded-routes
  "Function for get expanded routes"
  []
    (route/expand-routes
      #{["/api/v1/users/register"
         :post [dbi/db-interceptor (body-params/body-params) users-interceptors/register-user-interceptor]
         :route-name
         :register-user]
        ["/api/v1/users/login"
         :post [dbi/db-interceptor (body-params/body-params) users-interceptors/login-user-interceptor]
         :route-name :login-user]
        ["/api/v1/users/auto-login"
         :get [dbi/db-interceptor service/say-hello]
         :route-name :auto-login]
        ["/api/v1/users/logout"
         :get [dbi/db-interceptor service/say-hello]
         :route-name :logout-user]
        ["/api/v1/users/change-password"
         :post service/say-hello
         :route-name :change--user-password]}))

(defn start
  []
  (-> {::http/port   port
       ::http/join?  false
       ::http/type   :jetty
       ::http/routes (get-expanded-routes)
       ::http/allowed-origins {:creds true
                               :allowed-origins (constantly true)
                               :max-age 350}}
      http/default-interceptors
      http/dev-interceptors
      http/create-server
      http/start))

(defn ensure-indexes
  "Function for ensure indexes for server"
  [host dbname]
  (let [connection (con/get-connection-by-uri host dbname)]
    (i/create-unique-index connection users-collection-name (array-map :login 1))
    (con/disconnect connection)))

(defn -main
  []
  (ensure-indexes database-host database-name)
  (start))