(ns database.common.indexes
  (:require
    [utils.connection :as con]
    [monger.collection :as mc]))

(defn check-index-on-exist
  "Function for check index on exist"
  [connection collection]
  (let [db (con/get-db-from-connection connection)
        size (count (mc/indexes-on db collection))]
    (if (= size 0) false true)))

(defn create-unique-index
  "Function for create unique index"
  [connection collection field]
  (if (or (nil? collection) (nil? field))
    (throw (Exception. "Not send some data"))
    (let [db (con/get-db-from-connection connection)
          exist (check-index-on-exist connection collection)]
      (if (nil? db)
        (throw (Exception. "Can't get database"))
        (if exist
          nil
          (mc/ensure-index db collection field {:unique true }))))))

(defn create-index
  "Function for create index (full featured)"
  [connection collection name opts]
  (if (or (nil? collection) (nil? name) (nil? opts))
    (throw (Exception. "Not send some data"))
    (let [db (con/get-db-from-connection connection)]
      (mc/ensure-index db collection name opts))))

(defn drop-index
  "Function for drop index"
  [connection collection name]
  (if (or (nil? connection) (nil? name))
    (throw (Exception. "Not send some data"))
    (let [db (con/get-db-from-connection connection)]
      (mc/drop-index db collection name))))