(ns snowcrash.service
  (:require [io.pedestal.interceptor :as i]))

(def say-hello
  {:name ::say-hello
   :enter (fn [context]
            (assoc context :response {:body "Success"
                                      :status 200}))})