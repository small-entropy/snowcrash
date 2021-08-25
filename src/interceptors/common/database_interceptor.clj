(ns interceptors.common.database_interceptor
  (:require
    [utils.connection :as connection]))


(defonce host "127.0.0.1")
(defonce dbname "Snowcrash")
(defonce connection (atom (connection/get-connection-by-uri host dbname)))

(def db-interceptor
  {:name :database-interceptor
   :enter
         (fn [context]
           (update context :request assoc :connection @connection))
   :leave
         (fn [context]
           (if-let [[op & args] (:tx-data context)]
             (do
               (apply swap! connection op args)
               (assoc-in context [:request :connection] @connection))
             context))})