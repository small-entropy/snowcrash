(ns routes.users
  (:require [interceptors.common.attach-db :refer :all]
            [interceptors.common.attach-guid :refer :all]
            [interceptors.users.users-interceptors :refer :all]
            [interceptors.common.error-interceptor :refer :all]
            [interceptors.users.attach-user :refer :all]
            [utils.constants :refer :all]))

(defn get-routes-v1
  "Function for get configs for users routes"
  [database]
  #{["/api/v1/users"
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
     :route-name :entity-user]})
