(ns routes.operating-systems
  (:require [io.pedestal.http.body-params :as body-params]
            [interceptors.common.attach-db :refer :all]
            [interceptors.common.attach-guid :refer :all]
            [interceptors.common.error-interceptor :refer :all]
            [interceptors.books.check-access :refer :all]
            [interceptors.books.os-interceptors :refer :all]
            [interceptors.books.attach-doc-guid :refer :all]
            [utils.constants :refer :all]))

(defn get-routes-v1
  "Function for get configs for OS routes"
  [database]
  #{["/api/v1/operating-systems"
     :get [errors
           attach-guid
           attach-doc-guid
           (attach-db database)
           list-os-interceptor]
     :route-name :list-os]
    ["/api/v1/operating-systems"
     :post [errors
            attach-guid
            (attach-db database)
            (body-params/body-params)
            (books-access os-collection-name)
            create-os-interceptor]
     :route-name :create-os]
    ["/api/v1/operating-systems/:document-id"
     :get [errors
           attach-guid
           attach-doc-guid
           (attach-db database)
           get-os-interceptor]
     :route-name ::get-os]
    ["/api/v1/operating-systems/:document-id"
     :put [errors
           attach-guid
           (attach-db database)
           (body-params/body-params)
           (books-access os-collection-name)
           update-os-interceptor]
     :route-name :update-os]
    ["/api/v1/operating-systems/:document-id"
     :delete [errors
              attach-guid
              (attach-db database)
              (books-access os-collection-name)
              deactivate-os-interceptor]
     :route-name :deactivate-os]})