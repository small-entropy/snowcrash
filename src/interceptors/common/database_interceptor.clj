(ns interceptors.common.database-interceptor)

(defn db-interceptor
  [connection]
  {:name ::database-interceptor
   :enter
         (fn [context]
           (assoc context :connection connection))
   :leave
         (fn [context]
           (if-let [[op & args] (:tx-data context)]
             (do
               (apply swap! connection op args)
               (assoc-in context [:connection] connection))
             context))})