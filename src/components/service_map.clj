(ns components.service-map
  (:require [com.stuartsierra.component :as component]
            [snowcrash.routes :as r]
            [io.pedestal.http :as http]))

(defrecord Service-Map
  [env database service-map]
  component/Lifecycle
  (start [this]
    (if service-map
      this
      (assoc
        this
        :service-map
        {:env env
         ::http/port   (-> env :http :port)
         ::http/join?  false
         ::http/type   :jetty
         ::http/routes (r/get-expanded-routes database)
         ::http/allowed-origins {:creds true
                                 :allowed-origins (constantly true)
                                 :max-age 350}})))
  (stop [this]
    (assoc this :service-map nil)))

(defn new-service-map
  []
  (map->Service-Map {}))
