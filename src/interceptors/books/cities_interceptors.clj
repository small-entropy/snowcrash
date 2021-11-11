(ns interceptors.books.cities-interceptors
  (:require
    [services.cities-service :as service]
    [utils.helpers :as h]
    [utils.answers :refer :all]))

;; Interceptor for get cities list
(def list-cities-interceptor
  {:name ::list-cities-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             guid :guid
             accept-language :accept-language} context
            query-params (get request :query-params nil)
            limit (h/get-limit query-params)
            skip (h/get-skip query-params)
            {documents :documents
             total :total} (if (nil? accept-language)
                                         (service/get-cities connection limit skip)
                                         (service/get-cities connection limit skip accept-language))]
        (assoc context :response (ok guid documents {:request guid
                                                     :total total
                                                     :limit limit
                                                     :skip skip}))))})

;; Interceptor for create city document
(def create-city-interceptor
  {:name ::create-city-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             token :token
             user :user
             guid :guid} context
            {title :title
             time-zone :time-zone
             post-code :post-code
             values :values} (get request :json-params nil)
            {document :document} (service/create-city connection title time-zone post-code values user)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})
;; Interceptor for get city document
(def get-city-interceptor
  {:name ::get-city-interceptor
   :enter
    (fn [context]
      (let [{guid :guid
             connection :connection
             document-id :document-id
             accept-language :accept-language} context
            {document :document} (if (nil? accept-language)
                                   (service/get-city connection document-id)
                                   (service/get-city connection document-id accept-language))]
        (assoc context :response (ok guid document {:_id document-id
                                                    :request guid
                                                    :accept-language accept-language}))))})

;; Interceptor for update city document
(def update-city-interceptor
  {:name ::update-city-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             token :token
             user :user
             guid :guid
             document-id :document-id} context
            {title :title
             time-zone :time-zone
             post-code :post-code
             values :values} (get request :json-params nil)
            {document :document} (service/update-city
                                   connection
                                   document-id
                                   title
                                   time-zone
                                   post-code
                                   values)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})

;; Interceptor for deactivate city document
(def deactivate-city-interceptor
  {:name ::deactivate-city-interceptor
   :enter
    (fn [context]
      (let [{connection :connection
             user :user
             token :token
             guid :guid
             document-id :document-id} context
            {document :document} (service/deactivate-city connection document-id)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})