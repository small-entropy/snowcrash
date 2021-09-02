(ns snowcrash.routes
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [database.common.indexes :as i]
            [utils.connection :as con]
            [interceptors.common.database_interceptor :as dbi]
            [interceptors.users.users-interceptors :as users-interceptors]
            [utils.constants :refer :all]))

(defn- response-hello [request]
  {:status 200 :body "Hello, world"})

(defn get-expanded-routes
  "Function for get expanded routes"
  [database-component]
  (let [database (:database database-component)]
    (route/expand-routes
      #{["/api/v1/users/register"
         :post [(dbi/db-interceptor database) (body-params/body-params) users-interceptors/register-user-interceptor]
         :route-name
         :register-user]
        ["/api/v1/users/login"
         :post [(dbi/db-interceptor database) (body-params/body-params) users-interceptors/login-user-interceptor]
         :route-name :login-user]})))