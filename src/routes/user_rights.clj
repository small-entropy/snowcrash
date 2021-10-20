(ns routes.user-rights
  (:require [io.pedestal.http.body-params :as body-params]
            [interceptors.common.attach-db :refer :all]
            [interceptors.common.attach-guid :refer :all]
            [interceptors.users.rights-interceptors :refer :all]
            [interceptors.common.error-interceptor :refer :all]
            [interceptors.users.attach-user :refer :all]
            [utils.constants :refer :all]))

(defn get-routes-v1
  "Function for get configs routes for user rights"
  [database]
  #{["/api/v1/users/:user-id/rights"
     :get [errors
           attach-guid
           (attach-db database)
           attach-user-data
           rights-user-interceptor]
     :route-name :rights-user]
    ["/api/v1/users/:user-id/rights"
     :post [errors
            attach-guid
            (attach-db database)
            (body-params/body-params)
            attach-user-data
            create-user-right-interceptor]
     :route-name :create-user-rights]
    ["/api/v1/users/:user-id/rights/:property-id"
     :get [errors
           attach-guid
           (attach-db database)
           attach-user-data
           right-user-interceptor]
     :route-name :right-user]
    ["/api/v1/users/:user-id/rights/:property-id"
     :put [errors
           attach-guid
           (attach-db database)
           (body-params/body-params)
           attach-user-data
           update-user-right-interceptor]
     :route-name :update-user-right]
    ["/api/v1/users/:user-id/rights/:property-id"
     :delete [errors
              attach-guid
              (attach-db database)
              (body-params/body-params)
              attach-user-data
              delete-user-right-interceptor]
     :route-name :delete-user-right]})
