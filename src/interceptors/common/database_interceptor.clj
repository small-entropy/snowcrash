(ns interceptors.common.database_interceptor)

(defn db-interceptor
  [connection]
  {:name :database-interceptor
   :enter
         (fn [context]
           (update context :request assoc :connection connection))
   :leave
         (fn [context]
           (if-let [[op & args] (:tx-data context)]
             (do
               (apply swap! connection op args)
               (assoc-in context [:request :connection] connection))
             context))})