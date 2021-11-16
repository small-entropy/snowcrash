(ns interceptors.books.tags-interceptors
  (:require
    [services.tags-service :as service]
    [utils.helpers :as h]
    [utils.answers :refer :all]))

;; Interceptor for get tags list
(def list-tags-interceptors
  {:name ::list-tags-interceptors
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
                                         (service/get-tags connection limit skip)
                                         (service/get-tags connection limit skip accept-language))]
        (assoc context :response (ok guid documents {:request guid
                                                     :total total
                                                     :limit limit
                                                     :skip skip}))))})

;; Interceptor for create tag document
(def create-tag-interceptor
  {:name ::create-tag-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             token :token
             user :user
             guid :guid} context
            {title :title
             values :values} (get request :json-params nil)
            {document :document} (service/create-tag connection title values user)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})

;; Interceptor for get tag document
(def get-tag-interceptor
  {:name ::get-tag-interceptor
   :enter
    (fn [context]
      (let [{guid :guid
             connection :connection
             document-id :document-id
             accept-language :accept-language} context
            {document :document} (if (nil? accept-language)
                                   (service/get-tag connection document-id)
                                   (service/get-tag connection document-id accept-language))]
        (assoc context :response (ok guid document {:_id document-id
                                                    :request guid
                                                    :accept-language accept-language}))))})

;; Interceptor for update tag document
(def update-tag-interceptor
  {:name ::update-tag-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             token :token
             user :user
             guid :guid
             document-id :document-id} context
            {title :title
             values :values} (get request :json-params nil)
            {document :document} (service/update-tag connection document-id title values)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})

;; Interceptor for deactivate tag document
(def deactivate-tag-interceptor
  {:name ::deactivate-tag-interceptor
   :enter
    (fn [context]
      (let [{connection :connection
             user :user
             token :token
             guid :guid
             document-id :document-id} context
            {document :document} (service/deactivate-tag connection document-id)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})