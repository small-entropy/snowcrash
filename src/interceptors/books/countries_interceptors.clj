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