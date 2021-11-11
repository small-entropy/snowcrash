(ns database.single.cities-repository
  (:require
    [utils.constants :refer :all]
    [utils.helpers :as h]
    [utils.repository-helpers :as rh]))

(defn create-city
  "Function for create city document in database"
  [connection data]
  (rh/create-document connection cities-collection-name data))

(defn get-cities-list
  "Function for get list of cities document"
  [connection limit skip fields]
  (rh/list-documents
    connection
    cities-collection-name
    limit
    skip
    {:status default-status}
    {}
    fields
    "Cities list is empty"
    "not-found"
    {:limit limit :skip skip}))

(defn find-city-by-id
  "Function for find city document by id"
  [connection id fields]
  (rh/find-document
    connection
    cities-collection-name
    {:_id (h/value->object-id id) :status default-status}
    fields
    "Can not find city"
    "not-found"
    {:_id id :fields fields}))

(defn update-city
  "Function for update city document"
  [connection document-id to-update fields]
  (rh/update-document
    connection
    cities-collection-name
    document-id
    to-update
    fields
    "Can not find city document"
    "not-found"))

(defn get-total
  "Function for get count of city documents"
  [connection]
  (rh/get-total-documents connection cities-collection-name))
