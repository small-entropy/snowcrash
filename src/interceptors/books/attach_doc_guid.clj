(ns interceptors.books.attach-doc-guid
  (:require [utils.constants :refer :all])
  (:import (org.bson.types ObjectId)))

;; Interceptor to attach document id to context
(defn attach-doc-guid
  {:name ::attach-doc-guid
   :enter
    (fn [context]
      (let [request (get context :request nil)
            path-params (get request :path-params)
            document-id (get path-params :document-id)
            headers (get request :headers nil)
            accept-language (get headers accept-language default-accept-language)]
        (assoc context :document-id (if (nil? document-id)
                                      nil
                                      (ObjectId. ^String document-id))
                       :accept-language accept-language)))})