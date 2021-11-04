(ns database.single.os-repository
  (:require
    [utils.constants :refer :all]
    [utils.helpers :as h]
    [utils.repository-helpers :as rh]))

(defn create-operating-system
  "Function for create entry for operating system"
  [connection data]
  (rh/create-document connection os-collection-name data))

(defn find-operating-system-by-id
  "Function for find OS document by id"
  [connection id fields]
  (rh/find-document
    connection
    os-collection-name
    {:_id (h/value->object-id id) :status default-status}
    fields
    "Can not find OS document"
    "not-found"
    {:_id id :fields fields}))

(defn find-operating-system-by-name
  "Function for find OS by name"
  [connection name fields]
  (rh/find-document
    connection
    os-collection-name
    {:name name :status default-status}
    fields
    "Can not find OS document"
    "not-found"
    {:name name :fields fields}))

(defn get-operating-systems-list
  "Function for get list of OS documents"
  [connection limit skip fields]
  (rh/list-documents
    connection
    os-collection-name
    limit
    skip
    {:status default-status}
    {}
    fields
    "OS documents not found"
    "not-found"
    {:limit limit :skip skip}))

(defn update-operating-system
  "Function for update OS document"
  [connection os-id to-update fields]
  (rh/update-document
    connection
    os-collection-name
    os-id
    to-update
    fields
    "Can not find OS document"
    "not-found"))

(defn deactivate-operating-system
  "Function for deactivate OS document"
  [connection os-id fields]
  (rh/deactivate-document
    connection
    os-collection-name
    os-id
    fields
    "Can not find OS document"
    "not-found"))

(defn get-total
  "Function for get count of OS documents"
  [connection]
  (rh/get-total-documents connection os-collection-name))
