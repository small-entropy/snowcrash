(ns snowcrash.routes
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [database.common.indexes :as i]
            [utils.connection :as con]
            [interceptors.common.database-interceptor :as dbi]
            [interceptors.common.attach-guid :as aguidi]
            [interceptors.users.users-interceptors :as users-interceptors]
            [interceptors.common.error-interceptor :as errors]
            [utils.constants :refer :all]))

(defn- response-hello [request]
  {:status 200 :body "Hello, world"})

(def get-expanded-routes
   (fn
     [database-component]
     (let [database (:database database-component)]
       (route/expand-routes
         #{["/api/v1/users/register"
            :post [errors/errors
                   aguidi/attach-guid
                   (dbi/db-interceptor database)
                   (body-params/body-params)
                   users-interceptors/register-user-interceptor]
            :route-name
            :register-user]
           ["/api/v1/users/login"
            :post [errors/errors
                   aguidi/attach-guid
                   (dbi/db-interceptor database)
                   (body-params/body-params)
                   users-interceptors/login-user-interceptor]
            :route-name :login-user]
           ["/api/v1/users/autologin"
            :get [errors/errors
                  aguidi/attach-guid
                  (dbi/db-interceptor database)
                  users-interceptors/autologin-user-interceptor]]}))))