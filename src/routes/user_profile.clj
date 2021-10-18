(ns routes.user-profile
  (:require [io.pedestal.http.body-params :as body-params]
            [interceptors.common.attach-db :refer :all]
            [interceptors.common.attach-guid :refer :all]
            [interceptors.users.profile-interceptors :refer :all]
            [interceptors.common.error-interceptor :refer :all]
            [interceptors.users.attach-user :refer :all]
            [utils.constants :refer :all]))

(defn get-routes-v1
  "Function for get configs for user profile routes"
  [database]
  #{["/api/v1/users/:user-id/profile"
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
     :route-name :delete-profile-user-property]})