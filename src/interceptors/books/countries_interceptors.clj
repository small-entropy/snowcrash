(ns interceptors.books.countries-interceptors
  (:require
    [services.countries-service :as service]
    [utils.helpers :as h]
    [utils.answers :refer :all]))

;; Interceptor for get countries list
(def list-countries-interceptor
  {:name ::list-countries-interceptor
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
                                         (service/get-countries connection limit skip)
                                         (service/get-countries connection limit skip accept-language))]
        (assoc context :response (ok guid documents {:request guid
                                                     :total total
                                                     :limit limit
                                                     :skip skip}))))})

;; Interceptor for create country in book
(def create-country-interceptor
  {:name ::create-country-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             token :token
             guid :guid
             user :user} context
            {title :title
             time-zone :time-zone
             values :values} (get request :json-params nil)
            {document :document} (service/create-country connection title time-zone values user)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})

;; Interceptor for get country document
(def get-country-interceptor
  {:name ::get-language-interceptor
   :enter
    (fn [context]
      (let [{guid :guid
             connection :connection
             document-id :document-id
             accept-language :accept-language} context
            {document :document} (if (nil? accept-language)
                                   (service/get-country connection document-id)
                                   (service/get-country connection document-id accept-language))]
        (assoc context :response (ok guid document {:_id document-id
                                                    :request guid
                                                    :accept-language accept-language}))))})

;; Interceptor for update country document
(def update-country-interceptor
  {:name ::update-country-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             user :user
             token :token
             guid :guid
             document-id :document-id} context
            {title :title
             time-zone :time-zone
             values :values} (get request :json-params nil)
            {document :document} (service/update-country
                                   connection
                                   document-id
                                   title
                                   time-zone
                                   values)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})

;; Interceptor for deactivate country document
(def deactivate-country-interceptor
  {:name ::deactivate-country-interceptor
   :enter
    (fn [context]
      (let [{connection :connection
             user :user
             token :token
             guid :guid
             document-id :document-id} context
            {document :document} (service/deactivate-country connection document-id)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})
