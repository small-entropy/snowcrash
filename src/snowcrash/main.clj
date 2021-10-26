(ns snowcrash.main
  (:require [snowcrash.system :as system]
            [snowcrash.config :as config]
            [com.stuartsierra.component :as component]
            [database.common.indexes :as indexes]
            [utils.connection :as connection]
            [utils.constants :refer :all]
            [snowcrash.routes :as routes]))

(defn- ensure-indexes
  "Function for ensure indexes for server"
  [host dbname]
  (let [connection (connection/get-connection-by-uri host dbname)]
    (indexes/create-unique-index connection users-collection-name (array-map :login 1))
    (indexes/create-unique-index connection languages-collection-name (array-map :key 1))
    (connection/disconnect connection)))

(defn -main
  "Main entry point"
  []
  (let [system (system/new-system
                 (config/get-config) routes/get-expanded-routes)]
    (ensure-indexes database-host database-name)
    (component/start system)))
