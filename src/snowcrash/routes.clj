(ns snowcrash.routes
  (:require [io.pedestal.http.route :as r]
            [io.pedestal.http.body-params :as body-params]
            [interceptors.common.database-interceptor :as dbi]
            [interceptors.common.attach-guid :as aguidi]
            [interceptors.users.users-interceptors :as ui]
            [interceptors.common.error-interceptor :as ei]
            [interceptors.users.attach-user :as au]
            [utils.constants :refer :all]))


(defn- response-hello [request]
  {:status 200 :body "Hello, world"})

(def get-expanded-routes
   (fn
     [database-component]
     (let [database (:database database-component)]
       (r/expand-routes
         #{["/api/v1/user/register"
            :post [ei/errors
                   aguidi/attach-guid
                   (dbi/db-interceptor database)
                   (body-params/body-params)
                   ui/register-user-interceptor]
            :route-name
            :register-user]
           ["/api/v1/user/login"
            :post [ei/errors
                   aguidi/attach-guid
                   (dbi/db-interceptor database)
                   (body-params/body-params)
                   ui/login-user-interceptor]
            :route-name :login-user]
           ["/api/v1/user/autologin"
            :get [ei/errors
                  aguidi/attach-guid
                  (dbi/db-interceptor database)
                  ui/autologin-user-interceptor]
            :route-name :autologin-user]
           ["/api/v1/user/logout"
            :post [ei/errors
                   aguidi/attach-guid
                   ui/logout-user-interceptor]
            :route-name :logout-user]
           ["/api/v1/user/change-password"
            :post [ei/errors
                   aguidi/attach-guid
                   (dbi/db-interceptor database)
                   (body-params/body-params)
                   ui/change-user-password-interceptor]
            :route-name :change-user-password]
           ["/api/v1/users"
            :get [ei/errors
                  aguidi/attach-guid
                  (dbi/db-interceptor database)
                  ui/list-users-interceptor]
            :route-name :list-users]
           ["/api/v1/users/:user-id"
           :get [ei/errors
                 aguidi/attach-guid
                 au/attach-user-data
                 (dbi/db-interceptor database)
                 ui/entity-users-interceptor]
           :route-name :entity-user]
           ["/api/v1/users/:user-id/profile"
            :get [ei/errors
                  aguidi/attach-guid
                  (dbi/db-interceptor database)
                  ui/profile-user-interceptor]
            :route-name :profile-user-interceptor]
           ["/api/v1/users/:user-id/profile/:property-id"
            :get [ei/errors
                  aguidi/attach-guid
                  (dbi/db-interceptor database)
                  ui/profile-user-property-interceptor]
            :route-name :profile-user-property]
           ["/api/v1/users/:user-id/profile"
            :post [ei/errors
                   aguidi/attach-guid
                   (dbi/db-interceptor database)
                   (body-params/body-params)
                   au/attach-user-data
                   ui/create-profile-user-property-interceptor]
            :route-name :create-profile-user-property]
           ["/api/v1/users/:user-id/profile/:property-id"
            :put [ei/errors
                   aguidi/attach-guid
                   (dbi/db-interceptor database)
                   (body-params/body-params)
                   au/attach-user-data
                   ui/update-profile-user-property-interceptor]
            :route-name :update-profile-user-property]}))))