(ns routes.cities
  (:require [io.pedestal.http.body-params :as body-params]
            [interceptors.common.attach-db :refer :all]
            [interceptors.common.attach-guid :refer :all]
            [interceptors.common.error-interceptor :refer :all]
            [interceptors.books.check-access :refer :all]
            [interceptors.books.cities-interceptors :refer :all]
            [interceptors.books.attach-doc-guid :refer :all]
            [utils.constants :refer :all]))

(defn get-routes-v1
  "Function for get configs for cities book"
  [database]
  #{["/api/v1/cities"
     :get [errors
           attach-guid
           attach-doc-guid
           (attach-db database)
           list-cities-interceptor]
     :route-name :list-cities]
    ["/api/v1/cities"
     :post [errors
            attach-guid
            (attach-db database)
            (body-params/body-params)
            (books-access cities-collection-name)
            create-city-interceptor]
     :route-name :create-city]
    ["/api/v1/cities/:document-id"
     :get [errors
           attach-guid
           attach-doc-guid
           (attach-db database)
           get-city-interceptor]
     :route-name :get-city]
    ["/api/v1/cities/:document-id"
     :put [errors
           attach-guid
           (attach-db database)
           (body-params/body-params)
           (books-access cities-collection-name)
           update-city-interceptor]
     :route-name :update-city]
    ["/api/v1/cities/:document-id"
     :delete [errors
              attach-guid
              (attach-db database)
              (books-access cities-collection-name)
              deactivate-city-interceptor]
     :route-name :deactivate-city]})