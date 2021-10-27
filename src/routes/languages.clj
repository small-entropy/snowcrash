(ns routes.languages
  (:require [io.pedestal.http.body-params :as body-params]
            [interceptors.common.attach-db :refer :all]
            [interceptors.common.attach-guid :refer :all]
            [interceptors.books.languages :refer :all]
            [interceptors.common.error-interceptor :refer :all]
            [interceptors.users.attach-user :refer :all]
            [utils.constants :refer :all]))

(defn get-routes-v1
  "Function for get configs for languages routes"
  [database]
  #{["/api/v1/languages"
     :get [errors
           attach-guid
           (attach-db database)
           list-languages-interceptor]
     :route-name :list-languages]
    ["/api/v1/languages"
     :post [errors
           attach-guid
           (attach-db database)
           (body-params/body-params)
           attach-user-data
           create-language-interceptor]
     :route-name :create-language]
    ["/api/v1/languages/:document-id"
     :get [errors
           attach-guid
           (attach-db database)
           get-language-interceptor]
     :route-name :get-language]
    ["/api/v1/languages/:document-id"
     :put [errors
           attach-guid
           (attach-db database)
           (body-params/body-params)
           attach-user-data
           update-language-interceptor]
     :route-name :update-language]
    ["/api/v1/languages/:document-id"
     :delete [errors
              attach-guid
              (attach-db database)
              attach-user-data
              deactivate-language-interceptor]
     :route-name :deactivate-language]})
