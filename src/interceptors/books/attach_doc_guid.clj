(ns interceptors.books.attach-doc-guid
  (:require [utils.constants :refer :all])
  (:import (org.bson.types ObjectId)))

;; Interceptor to attach document id to context
(def attach-doc-guid
  {:name ::attach-doc-guid
   :enter
    (fn [context]
      (let [request (get context :request nil)
            path-params (get request :path-params nil)
            document-id (get path-params :document-id nil)
            headers (get request :headers nil)
            accept-language (get headers accept-language nil)]
        (assoc context :document-id (if (nil? document-id)
                                      nil
                                      (ObjectId. ^String document-id))
                       :accept-language (if (= "all" accept-language)
                                          nil
                                          accept-language))))})