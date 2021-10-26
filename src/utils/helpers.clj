(ns utils.helpers
  (:import (org.bson.types ObjectId)))

(defn not-send
  "Helper function for check value on exist &
  return value or string (Not send)"
  [value]
  (if (nil? value) "Not send" value))

(defn value->object-id
  "Function get correct ID on ObjectID.
  If send String - create ObjectId from String.
  If send ObjectId - return it"
  [value]
  (if (string? value) (ObjectId. ^String value) value))

(defn values->get-collection
  "Function for get correct fields collection.
  If send fields - return it.
  If send nil - return empty collection"
  [values]
  (if (nil? values) [] values))

