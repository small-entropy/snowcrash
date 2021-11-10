(ns routes.countries
  (:require [io.pedestal.http.body-params :as body-params]
            [interceptors.common.attach-db :refer :all]
            [interceptors.common.attach-guid :refer :all]
            [interceptors.common.error-interceptor :refer :all]
            [interceptors.books.check-access :refer :all]
            [interceptors.books.countries-interceptors :refer :all]
            [interceptors.books.attach-doc-guid :refer :all]
            [utils.constants :refer :all]))

(defn get-routes-v1
  "Function for get configs routes for countries book"
  [database]
  #{["/api/v1/countries"
     :get [errors
           attach-guid
           attach-doc-guid
           (attach-db database)
           list-countries-interceptor]
     :route-name :list-countries]
    ["/api/v1/countries"
     :post [errors
            attach-guid
            (attach-db database)
            (body-params/body-params)
            (books-access countries-collection-name)
            create-country-interceptor]
     :route-name :create-country]
    ["/api/v1/countries/:document-id"
     :get [errors
           attach-guid
           attach-doc-guid
           (attach-db database)
           get-country-interceptor]
     :route-name :get-country]
    ["/api/v1/countries/:document-id"
     :put [errors
           attach-guid
           (attach-db database)
           (body-params/body-params)
           (books-access countries-collection-name)
           update-country-interceptor]
     :route-name :update-country]
    ["/api/v1/countries/:document-id"
     :delete [errors
              attach-guid
              (attach-db database)
              (books-access countries-collection-name)
              deactivate-country-interceptor]
     :route-name :deactivate-country]})
