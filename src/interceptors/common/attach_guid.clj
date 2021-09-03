(ns interceptors.common.attach-guid
  (:require [io.pedestal.interceptor :as interceptor])
  (:import (org.bson.types ObjectId)))

(def attach-guid
  {:name ::attach-guid
   :enter
         (fn [context]
           (let [guid (str (ObjectId.))]
             (assoc context :guid guid)))})