(ns interceptors.common.error-interceptor
  (:require [utils.answers :refer :all]))

(defn- error-answer
  ([context message meta]
   (assoc context :response (fail (get meta :request nil) message meta)))
  ([context message meta code alias]
    (assoc context :response (fail (get meta :request nil) message meta code alias))))

(defn- get-code
  [alias]
  (case alias
    "can-not-create-unique-index" 500
    "can-not-create-index" 500
    "can-not-drop-index" 500
    "can-not-get-collection-document" 500
    "can-not-find-user" 404
    "can-not-login" 401
    "not-send-token" 401
    "not-send-password" 400
    :else 502))

(def errors
  {:name ::error-interceptor
   :error
   (fn
     [context exception]
     (let [guid (get context :guid nil)
           ex-type (:exception-type (ex-data exception))
           default-meta {:request guid}
           message (ex-message (ex-cause exception))]
       (case ex-type
         :java.lang.Exception
         (error-answer context message default-meta)
         :com.mongodb.DuplicateKeyException
         (error-answer context
                       "Failed with duplicate key error"
                       (merge default-meta {:description message}))
         :clojure.lang.ExceptionInfo
         (let [meta (merge default-meta (:info (ex-data exception)))
               alias (:alias (ex-data exception))
               code (get-code alias)]
           (error-answer context message meta code alias))
         :else
         (assoc context :io.pedestal.interceptor.chain/error exception))))})
