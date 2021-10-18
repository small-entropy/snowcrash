(ns routes.authorization
  (:require [io.pedestal.http.body-params :as body-params]
            [interceptors.common.attach-db :refer :all]
            [interceptors.common.attach-guid :refer :all]
            [interceptors.users.authorization-interceptors :refer :all]
            [interceptors.common.error-interceptor :refer :all]
            [interceptors.users.attach-user :refer :all]
            [utils.constants :refer :all]))

(defn get-routes-v1
  "Function for get config for authorization routes"
  [database]
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
     :route-name :change-user-password]})