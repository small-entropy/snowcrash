(ns database.nested.property
  (:import (org.bson.types ObjectId)))

(defn get-default-user-properties
  "Function for get default user properties"
  []
  [{:_id (ObjectId.) :key "banned" :value false}])

(defn get-default-company-properties
  "Function for get default company properties"
  []
  [])

(defn create
  "Function for create user property by key & value"
  [key value]
  {:_id (ObjectId.) :key key :value value})