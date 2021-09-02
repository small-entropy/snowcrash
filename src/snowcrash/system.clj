(ns snowcrash.system
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [components.pedestal :as p]
            [components.monger :as m]
            [components.service-map :as sm]
            [utils.constants :refer :all]))

(defn new-system
  [env]
  (component/system-map
    :env env
    :database (m/new-monger "127.0.0.1" "Snowcrash")
    :service-map (component/using
                   (sm/new-service-map)
                   [:env :database])
    :pedestal
    (component/using
      (p/new-pedestal)
      [:service-map])))

