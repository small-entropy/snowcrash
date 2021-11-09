(ns services.countries-service
  (:require
    [database.single.countries-repository :as r]
    [utils.constants :refer :all]
    [database.nested.translate-value]))

(defn get-countries
  "Function for get countries documents list"
  [connection limit skip]
  (let [documents]))