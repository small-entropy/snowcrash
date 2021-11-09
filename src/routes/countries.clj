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
           list-countries-interceptor]]})
