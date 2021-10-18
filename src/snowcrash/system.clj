(ns snowcrash.system
  (:require [com.stuartsierra.component :as component]
            [components.pedestal :as p]
            [components.monger :as m]
            [components.service-map :as sm]
            [utils.constants :refer :all]))

(defn new-system
  "Function for create new system"
  [env get-expanded-routes]
  (component/system-map
    :env env
    :get-expanded-routes get-expanded-routes
    :database (m/new-monger "127.0.0.1" "Snowcrash")
    :service-map (component/using
                   (sm/new-service-map)
                   [:env :get-expanded-routes :database])
    :pedestal
    (component/using
      (p/new-pedestal)
      [:service-map])))

