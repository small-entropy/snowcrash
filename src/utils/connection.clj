(ns utils.connection
  (:require
    [monger.core :as mg]))

(defn get-connection-by-uri
  "Function for connect to database by URI"
  [host database]
  (if (or (nil? host) (nil? database))
    (throw (Exception. "Not send host or database name"))
    (let [uri (str "mongodb://" host "/" database)]
      (mg/connect-via-uri uri))))

(defn get-db-from-connection
  "Function for get database from connection"
  [connection]
  (if (nil? connection)
    (throw (Exception. "Not sent connection"))
    (let [db (get connection :db nil)]
      (if (nil? db) (Exception. "Can not get database") db))))

(defn disconnect
  "Function for disconnect"
  [connection]
  (let [con (get connection :conn nil)]
    (if (nil? con)
      (throw (Exception. "Can not get MongoClient"))
      (mg/disconnect con))))