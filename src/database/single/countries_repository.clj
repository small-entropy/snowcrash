(ns database.single.countries-repository
  (:require
    [utils.constants :refer :all]
    [utils.helpers :as h]
    [utils.repository-helpers :as rh]))

(defn create-country
  "Function for create country document in database"
  [connection data]
  (rh/create-document connection countries-collection-name data))

(defn get-countries-list
  "Function for get countries list from books"
  [connection limit skip fields]
  (rh/list-documents
    connection
    countries-collection-name
    limit
    skip
    {:status default-status}
    {}
    fields
    "Countries not found"
    "not-found"
    {:limit limit :skip skip}))

(defn find-country-by-id
  "Function for find country by id"
  [connection id fields]
  (rh/find-document
    connection
    countries-collection-name
    {:_id (h/value->object-id id) :status default-status}
    fields
    "Can not find countries"
    "not-found"
    {:_id id :fields fields}))

(defn update-country
  "Function for update document"
  [connection document-id to-update fields]
  (rh/update-document
    connection
    countries-collection-name
    document-id
    to-update
    fields
    "Can not find country document"
    "not-found"))

(defn get-total
  "Function for get count of language documents"
  [connection]
  (rh/get-total-documents connection languages-collection-name))