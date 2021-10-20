(ns routes.user-properties
  (:require [io.pedestal.http.body-params :as body-params]
            [interceptors.common.attach-db :refer :all]
            [interceptors.common.attach-guid :refer :all]
            [interceptors.users.properties-interceptors :refer :all]
            [interceptors.common.error-interceptor :refer :all]
            [interceptors.users.attach-user :refer :all]
            [utils.constants :refer :all]))

(defn get-routes-v1
  "Function for get configs for user properties"
  [database]
  #{["/api/v1/users/:user-id/properties"
     :get [errors
           attach-guid
           (attach-db database)
           attach-user-data
           properties-user-interceptor]
     :route-name :properties-user]
    ["/api/v1/users/:user-id/properties"
     :post [errors
            attach-guid
            (attach-db database)
            (body-params/body-params)
            attach-user-data
            create-user-property-interceptor]
     :route-name :create-user-property]
    ["/api/v1/users/:user-id/properties/:property-id"
     :get [errors
           attach-guid
           (attach-db database)
           attach-user-data
           properties-user-property-interceptor]
     :route-name :properties-user-property]
    ["/api/v1/users/:user-id/properties/:property-id"
     :put [errors
           attach-guid
           (attach-db database)
           (body-params/body-params)
           attach-user-data
           update-user-property-interceptor]
     :route-name :update-user-property]
    ["/api/v1/users/:user-id/properties/:property-id"
     :delete [errors
              attach-guid
              (attach-db database)
              (body-params/body-params)
              attach-user-data
              delete-user-property-interceptor]
     :route-name :delete-user-property]})
