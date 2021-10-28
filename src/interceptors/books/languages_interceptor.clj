(ns interceptors.books.languages-interceptor
  (:require
    [services.language-service :as service]
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
            limit (if (nil? query-params) 10 (Integer/parseInt (get query-params :limit "10")))
            skip (if (nil? query-params) 0 (Integer/parseInt (get query-params :skip "0")))
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
      (let [{connection :connection
             token :token
             guid :guid
             user :user} context
            {document :document} (service/create-language connection)]
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
             document-id :document-id} context
            {document :document} (service/get-language connection document-id)]
        (assoc context :response (ok guid document {:request guid}))))})

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