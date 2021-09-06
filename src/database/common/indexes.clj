(ns database.common.indexes
  (:require
    [utils.connection :as con]
    [monger.collection :as mc]
    [utils.helpers :refer :all]))

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
    (throw (ex-info
             "Not send some data"
             {:alias "can-not-create-unique-index"
              :info {:collection (not-send collection)
                     :field (not-send field)}}))
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
    (throw (ex-info
             "Not send some data"
             {:alias "can-not-create-index"
              :info {:collection (not-send collection)
                     :name (not-send name)
                     :opts (not-send opts)}}))
    (let [db (con/get-db-from-connection connection)]
      (mc/ensure-index db collection name opts))))

(defn drop-index
  "Function for drop index"
  [connection collection name]
  (if (or (nil? connection) (nil? name))
    (throw (ex-info
             "Not send some data"
             {:alias "can-not-drop-index"
              :info {:collection (not-send collection)
                     :name (not-send name)}}))
    (let [db (con/get-db-from-connection connection)]
      (mc/drop-index db collection name))))