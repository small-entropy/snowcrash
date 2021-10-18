(ns snowcrash.config
  (:require [utils.constants :refer :all]))

(defn get-config
  "Function for get config for system"
  []
  {:http {:port port}})