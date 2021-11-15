(ns routes.genres
  (:require [io.pedestal.http.body-params :as body-params]
            [interceptors.common.attach-db :refer :all]
            [interceptors.common.attach-guid :refer :all]
            [interceptors.common.error-interceptor :refer :all]
            [interceptors.books.check-access :refer :all]
            [interceptors.books.genres-interceptors :refer :all]
            [interceptors.books.attach-doc-guid :refer :all]
            [utils.constants :refer :all]))

(defn get-routes-v1
  "Function for get configs for genres book"
  [database]
  #{["/api/v1/genres"
     :get [errors
           attach-guid
           attach-doc-guid
           (attach-db database)
           list-genres-interceptors]
     :route-name :list-genres]
    ["/api/v1/genres"
     :post [errors
            attach-guid
            (attach-db database)
            (body-params/body-params)
            (books-access genres-collection-name)
            create-genre-interceptor]
     :route-name :create-genre]
    ["/api/v1/genres/:document-id"
     :get [errors
           attach-guid
           attach-doc-guid
           (attach-db database)
           get-genre-interceptor]
     :route-name :get-genre]
    ["/api/v1/genres/:document-id"
     :put [errors
           attach-guid
           (attach-db database)
           (body-params/body-params)
           (books-access genres-collection-name)
           update-genre-interceptor]
     :route-name :update-genre]
    ["/api/v1/genres/:document-id"
     :delete [errors
              attach-guid
              (attach-db database)
              (books-access genres-collection-name)
              deactivate-genre-interceptor]
     :route-name :deactivate-genre]})