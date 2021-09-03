(ns interceptors.common.error-interceptor
  (:require [utils.answers :refer :all]))

(def errors
  {:name ::error-interceptor
   :error
   (fn
     [context exception]
     (let [guid (get context :guid nil)
           meta {:request guid}]
       (if (= :java.lang.Exception (:exception-type (ex-data exception)))
         (assoc context :response (fail guid (get exception :message "Internal server error") meta))
         (assoc context :io.pedestal.interceptor.chain/error exception))))})
