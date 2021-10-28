(ns interceptors.books.attach-doc-guid
  (:import (org.bson.types ObjectId)))

;; Interceptor to attach document id to context
(defn attach-doc-guid
  {:name ::attach-doc-guid
   :enter
    (fn [context]
      (let [request (get context :request nil)
            path-params (get request :path-params)
            document-id (get path-params :document-id)]
        (assoc context :document-id (if (nil? document-id)
                                      nil
                                      (ObjectId. ^String document-id)))))})