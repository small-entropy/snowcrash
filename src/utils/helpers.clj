(ns utils.helpers
  (:require [utils.constants :refer :all])
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

(defn get-limit
  "Function for get limit from query params"
  [query-params]
  (if (nil? query-params)
    default-limit
    (Integer/parseInt (get query-params :limit default-limit-str))))

(defn get-skip
  "function for get limit from query params"
  [query-params]
  (if (nil? query-params)
    default-skip
    (Integer/parseInt (get query-params :skip default-skip-str))))