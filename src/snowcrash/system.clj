(ns snowcrash.system
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [components.server.pedestal :as p]
            [snowcrash.routes :as r]
            [utils.constants :refer :all]))

(defn new-system
  [env]
  (component/system-map
    :service-map {:env env
                  ::http/port   (-> env :http :port)
                  ::http/join?  false
                  ::http/type   :jetty
                  ::http/routes (r/get-expanded-routes)
                  ::http/allowed-origins {:creds true
                                          :allowed-origins (constantly true)
                                          :max-age 350}}
    :pedestal
    (component/using
      (p/new-pedestal)
      [:service-map])))

