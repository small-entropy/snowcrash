(ns interceptors.books.languages-interceptor
  (:require
    [services.language-service :as service]
    [utils.helpers :as h]
    [utils.answers :refer :all]))

;; Interceptor for get languages list
(def list-languages-interceptor
  {:name ::languages-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             guid :guid} context
            query-params (get request :query-params nil)
            limit (h/get-limit query-params)
            skip (h/get-skip query-params)
            {documents :documents
             total :total} (service/get-languages connection limit skip)]
        (assoc context :response (ok guid documents {:request guid
                                                     :total total
                                                     :limit limit
                                                     :skip skip}))))})

;; Interceptor for get create language document
(defn create-language-interceptor
  {:enter ::create-language-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             token :token
             guid :guid
             user :user} context
            {title :title values :values} (get request :json-params nil)
            {document :document} (service/create-language connection title values user)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})

;; Interceptor for get language document
(defn get-language-interceptor
  {:name ::get-language-interceptor
   :enter
    (fn [context]
      (let [{connection :connection
             guid :guid
             document-id :document-id
             accept-language :accept-language} context
            {document :document} (service/get-language connection document-id accept-language)]
        (assoc context :response (ok guid document {:_id document-id
                                                    :request guid
                                                    :accept-language accept-language}))))})

;; Interceptor for update language document
(defn update-language-interceptor
  {:name ::update-language-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             user :user
             token :token
             guid :guid
             document-id :document-id} context
            {document :document} (service/update-language connection document-id)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})

;; Interceptor for deactivate language document
(defn deactivate-language-interceptor
  {:name ::deactivate-language-interceptor
   :enter
    (fn [context]
      (let [{connection :connection
             user :user
             token :token
             guid :guid
             document-id :document-id} context
            {document :document} (service/deactivate-language connection document-id)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})