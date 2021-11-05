(ns interceptors.books.os-interceptors
  (:require
    [services.operating-systems :as s]
    [utils.helpers :as h]
    [utils.answers :refer :all]))

;; Interceptor for get list of operating systems
(def list-os-interceptor
  {:name ::list-os-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             guid :guid
             accept-language :accept-language} context
            query-params (get request :query-params nil)
            limit (h/get-limit query-params)
            skip (h/get-skip query-params)
            with-owner (nil? accept-language)
            {documents :documents
             total :total} (s/get-operating-systems connection limit skip with-owner)]
        (assoc context :response (ok guid documents {:request guid
                                                     :total total
                                                     :limit limit
                                                     :skip skip}))))})

;; Interceptor for create operating system document
(def create-os-interceptor
  {:name ::create-os-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             token :token
             guid :guid
             user :user} context
            {title :title
             is-mobile :is-mobile
             is-pc :is-pc
             is-console :is-console
             requirements :requirements} (get request :json-params nil)
            {document :document}
            (s/create-operating-system connection title is-mobile is-pc is-console requirements user)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})

;; Interceptor for get operating system document
(def get-os-interceptor
  {:name ::get-os-interceptor
   :enter
    (fn [context]
      (let [{connection :connection
             guid :guid
             accept-language :accept-language
             document-id :document-id} context
            with-owner (nil? accept-language)
            {document :document} (s/get-operating-system connection document-id with-owner)]
        (assoc context :response (ok guid document {:_id document-id
                                                    :request guid}))))})

;; Interceptor fot update operation system document
(def update-os-interceptor
  {:name ::update-os-interceptor
   :enter
    (fn [context]
      (let [{request :request
             connection :connection
             user :user
             token :token
             guid :guid
             document-id :document-id} context
            {title :title
             is-mobile :is-mobile
             is-pc :is-pc
             is-console :is-console
             requirements :requirements} (get request :json-params nil)
            {document :document}
            (s/update-operating-system connection document-id title is-mobile is-pc is-console requirements)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})

;; Interceptor for deactivate operating system document
(def deactivate-os-interceptor
  {:name ::deactivate-os-interceptor
   :enter
    (fn [context]
      (let [{connection :connection
             user :user
             token :token
             guid :guid
             document-id :document-id} context
            {document :document} (s/deactivate-operating-system connection document-id)]
        (assoc context :response (ok guid document {:request guid
                                                    :user user
                                                    :token token}))))})