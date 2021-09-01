(ns snowcrash.config
  (:require [utils.constants :refer :all]))

(defn get-config
  []
  {:http {:port port}})