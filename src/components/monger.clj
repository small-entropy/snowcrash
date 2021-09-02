(ns components.monger
  (:require
    [com.stuartsierra.component :as component]
    [utils.connection :as connection]))

(defrecord Monger
  [host name database]
  component/Lifecycle
  (start [this]
    (if database
      this
      (let [connection
            (atom (connection/get-connection-by-uri host name))]
        (assoc this :database @connection))))
  (stop [this]
    (when database
      (connection/disconnect database))
    (assoc this :database nil)))

(defn new-monger
  [host name]
  (map->Monger {:host host :name name}))

