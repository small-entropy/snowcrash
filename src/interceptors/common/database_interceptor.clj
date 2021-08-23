(ns interceptors.common.database_interceptor
  (:require [utils.connection :as connection]))


(defonce host "127.0.0.1")
(defonce dbname "Snowcrash")
(defonce database (atom (connection/get-connection-by-uri host dbname)))


(def db-interceptor
  {:name :database-interceptor
   :enter
         (fn [context]
           (update context :request assoc :database @database))
   :leave
         (fn [context]
           (if-let [[op & args] (:tx-data context)]
             (do
               (apply swap! database op args)
               (assoc-in context [:request :database] @database))
             context))})