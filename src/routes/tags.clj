(ns routes.tags
  (:require [io.pedestal.http.body-params :as body-params]
            [interceptors.common.attach-db :refer :all]
            [interceptors.common.attach-guid :refer :all]
            [interceptors.common.error-interceptor :refer :all]
            [interceptors.books.check-access :refer :all]
            [interceptors.books.tags-interceptors :refer :all]
            [interceptors.books.attach-doc-guid :refer :all]
            [utils.constants :refer :all]))

(defn get-routes-v1
  "Function for get configs for tags book"
  [database]
  #{["/api/v1/tags"
     :get [errors
           attach-guid
           attach-doc-guid
           (attach-db database)
           list-tags-interceptors]
     :route-name :list-tags]
    ["/api/v1/tags"
     :post [errors
           attach-guid
           (attach-db database)
           (body-params/body-params)
           (books-access tags-collection-name)
           create-tag-interceptor]
     :route-name :create-tag]
    ["/api/v1/tags/:document-id"
     :get [errors
           attach-guid
           attach-doc-guid
           (attach-db database)
           get-tag-interceptor]
     :route-name :get-tag]
    ["/api/v1/tags/:document-id"
     :put [errors
           attach-guid
           (attach-db database)
           (body-params/body-params)
           (books-access tags-collection-name)
           update-tag-interceptor]
     :route-name :update-tag]
    ["/api/v1/tags/:document-id"
     :delete [errors
              attach-guid
              (attach-db database)
              (books-access tags-collection-name)
              deactivate-tag-interceptor]
     :route-name :deactivate-tag]})