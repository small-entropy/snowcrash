(ns snowcrash.routes
  (:require [io.pedestal.http.route :as r]
            [io.pedestal.http.body-params :as body-params]
            [interceptors.common.attach-db :refer :all]
            [interceptors.common.attach-guid :refer :all]
            [interceptors.users.users-interceptors :refer :all]
            [interceptors.common.error-interceptor :refer :all]
            [interceptors.users.attach-user :refer :all]
            [utils.constants :refer :all]))


(defn- response-hello [request]
  {:status 200 :body "Hello, world"})

(def get-expanded-routes
   (fn
     [database-component]
     (let [database (:database database-component)]
       (r/expand-routes
         #{["/api/v1/user/register"
            :post [errors
                   attach-guid
                   (attach-db database)
                   (body-params/body-params)
                   register-user-interceptor]
            :route-name :register-user]
           ["/api/v1/user/login"
            :post [errors
                   attach-guid
                   (attach-db database)
                   (body-params/body-params)
                   login-user-interceptor]
            :route-name :login-user]
           ["/api/v1/user/autologin"
            :get [errors
                  attach-guid
                  (attach-db database)
                  autologin-user-interceptor]
            :route-name :autologin-user]
           ["/api/v1/user/logout"
            :post [errors
                   attach-guid
                   logout-user-interceptor]
            :route-name :logout-user]
           ["/api/v1/user/change-password"
            :post [errors
                   attach-guid
                   (attach-db database)
                   (body-params/body-params)
                   change-user-password-interceptor]
            :route-name :change-user-password]
           ["/api/v1/users"
            :get [errors
                  attach-guid
                  (attach-db database)
                  list-users-interceptor]
            :route-name :list-users]
           ["/api/v1/users/:user-id"
           :get [errors
                 attach-guid
                 attach-user-data
                 (attach-db database)
                 entity-users-interceptor]
           :route-name :entity-user]
           ["/api/v1/users/:user-id/properties"
            :get [errors
                  attach-guid
                  (attach-db database)
                  attach-user-data
                  properties-user-interceptor]
            :route-name :properties-user]
           ["/api/v1/users/:user-id/properties/:property-id"
            :get [errors
                  attach-guid
                  (attach-db database)
                  attach-user-data
                  properties-user-property-interceptor]
            :route-name :properties-user-property]
           ["/api/v1/users/:user-id/profile"
            :get [errors
                  attach-guid
                  (attach-db database)
                  profile-user-interceptor]
            :route-name :profile-user]
           ["/api/v1/users/:user-id/profile/:property-id"
            :get [errors
                  attach-guid
                  (attach-db database)
                  profile-user-property-interceptor]
            :route-name :profile-user-property]
           ["/api/v1/users/:user-id/profile"
            :post [errors
                   attach-guid
                   (attach-db database)
                   (body-params/body-params)
                   attach-user-data
                   create-profile-user-property-interceptor]
            :route-name :create-profile-user-property]
           ["/api/v1/users/:user-id/profile/:property-id"
            :put [errors
                   attach-guid
                   (attach-db database)
                   (body-params/body-params)
                   attach-user-data
                   update-profile-user-property-interceptor]
            :route-name :update-profile-user-property]
           ["/api/v1/users/:user-id/profile/:property-id"
            :delete [errors
                     attach-guid
                     (attach-db database)
                     (body-params/body-params)
                     attach-user-data
                     delete-profile-user-property-interceptor]
            :route-name :delete-profile-user-property]}))))