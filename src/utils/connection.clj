(ns utils.connection
  (:require [monger.core :as mg]))

(defn get-connection-by-uri
  "Function for connect to database by URI"
  [host database]
  (if (or (nil? host) (nil? database))
    (throw (Exception. "Not send host or database name"))
    (let [uri (str "mongodb://" host "/" database)]
      (mg/connect-via-uri uri))))