(ns database.single.genres-repository
  (:require
    [utils.constants :refer :all]
    [utils.helpers :as h]
    [utils.repository-helpers :as rh]))

(defn create-genre
  "Function for create genre document in database"
  [connection data]
  (rh/create-document connection genres-collection-name data))

(defn get-genres-list
  "Function for get list of genres document"
  [connection limit skip fields]
  (rh/list-documents
    connection
    genres-collection-name
    limit
    skip
    {:status default-status}
    {}
    fields
    "Genres list is empty"
    "not-found"
    {:limit limit :skip skip}))

(defn find-genre-by-id
  "Function for find genre document by id"
  [connection id fields]
  (rh/find-document
    connection
    genres-collection-name
    {:_id (h/value->object-id id) :status default-status}
    fields
    "Can not find genre"
    "not-found"
    {:_id id :fields fields}))

(defn update-genre
  "Function for update genre document"
  [connection document-id to-update fields]
  (rh/update-document
    connection
    genres-collection-name
    document-id
    to-update
    fields
    "Can not find genre document"
    "not-found"))

(defn get-total
  "Function for get count of genre documents"
  [connection]
  (rh/get-total-documents connection genres-collection-name))
