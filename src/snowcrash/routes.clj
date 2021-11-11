(ns snowcrash.routes
  (:require [io.pedestal.http.route :as r]
            [routes.authorization :as auth-routes]
            [routes.user-profile :as prof-routes]
            [routes.users :as users-routes]
            [routes.user-properties :as prop-routes]
            [routes.user-rights :as rights-routes]
            [routes.languages :as lang-routes]
            [routes.operating-systems :as os-routes]
            [routes.countries :as countries-route]
            [routes.cities :as cities-routes]
            [clojure.set :as set]))

(def get-expanded-routes
  "Function for get expanded pedestal routes"
   (fn
     [database-component]
     (let [database (:database database-component)]
       (r/expand-routes (set/union (auth-routes/get-routes-v1 database)
                                   (users-routes/get-routes-v1 database)
                                   (prof-routes/get-routes-v1 database)
                                   (prop-routes/get-routes-v1 database)
                                   (rights-routes/get-routes-v1 database)
                                   (lang-routes/get-routes-v1 database)
                                   (os-routes/get-routes-v1 database)
                                   (countries-route/get-routes-v1 database)
                                   (cities-routes/get-routes-v1 database))))))