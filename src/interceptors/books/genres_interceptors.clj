(ns interceptors.books.genres-interceptors
  (:require
    [services.genres-service :as service]
    [utils.helpers :as h]
    [utils.answers :refer :all]))

;; Interceptor for get list of genres
(def list-genres-interceptors
  {:name ::list-genres-interceptors
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
                                         (service/get-genres connection limit skip)
                                         (service/get-genres connection limit skip accept-language))]
        (assoc context :response (ok guid documents {:request guid
                                                     :total total
                                                     :limit limit
                                                     :skip skip}))))})

;; Interceptor for create genre document
(def create-genre-interceptor
  {:name ::create-genre-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             token :token
             guid :guid
             user :user} context
            {title :title
             values :values} (get request :json-params nil)
            {document :document} (service/create-genre connection title values user)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})

;; Interceptor for get genre document
(def get-genre-interceptor
  {:name ::get-genre-interceptor
   :enter
    (fn [context]
      (let [{guid :guid
             connection :connection
             document-id :document-id
             accept-language :accept-language} context
            {document :document} (if (nil? accept-language)
                                   (service/get-genre connection document-id)
                                   (service/get-genre connection document-id accept-language))]
        (assoc context :response (ok guid document {:_id document-id
                                                    :request guid
                                                    :accept-language accept-language}))))})

;; Interceptor for update genre document
(def update-genre-interceptor
  {:name ::update-genre-interceptor
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
            {document :document} (service/update-genre
                                   connection
                                   document-id
                                   title
                                   values)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})

;; Interceptor for deactivate genre document
(def deactivate-genre-interceptor
  {:name ::deactivate-genre-interceptor
   :enter
    (fn [context]
      (let [{connection :connection
             user :user
             token :token
             guid :guid
             document-id :document-id} context
            {document :document} (service/deactivate-genre connection document-id)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})
