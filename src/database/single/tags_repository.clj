(ns database.single.tags-repository
  (:require
    [utils.constants :refer :all]
    [utils.helpers :as h]
    [utils.repository-helpers :as rh]))

(defn create-tag
  "Function for create tag document in database"
  [connection data]
  (rh/create-document connection tags-collection-name data))

(defn get-tags-list
  "Function for get list of tags documents"
  [connection limit skip fields]
  (rh/list-documents
    connection
    tags-collection-name
    limit
    skip
    {:status default-status}
    {}
    fields
    "Tags list is empty"
    "not-found"
    {:limit limit :skip skip}))

(defn find-tag-by-id
  "Function for find tag document by id"
  [connection id fields]
  (rh/find-document
    connection
    tags-collection-name
    {:_id (h/value->object-id id) :status default-status}
    fields
    "Can not find tag"
    "not-found"
    {:_id id :fields fields}))

(defn update-tag
  "Function for update tag document"
  [connection document-id to-update fields]
  (rh/update-document
    connection
    tags-collection-name
    document-id
    to-update
    fields
    "Can not find tag document"
    "not-found"))

(defn get-total
  "Function for get count for tags documents"
  [connection]
  (rh/get-total-documents connection tags-collection-name))