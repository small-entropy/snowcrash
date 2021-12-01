(ns database.single.companies-repository
  (:require
    [utils.repository-helpers :as rh]
    [utils.constants :refer :all]
    [utils.helpers :as h]))

(defn create-company
  "Function for create company in database"
  [connection data]
  (rh/create-document connection companies-collection-name data))

(defn get-companies-list
  "Function for get companies"
  [connection limit skip fields]
  (rh/list-documents
    connection
    companies-collection-name
    limit
    skip
    {:status default-status}
    {}
    fields
    "Companies list is empty"
    "not-found"
    {:limit limit :skip skip}))

(defn find-company-by-id
  "Function for get company document by id"
  [connection id fields]
  (rh/find-document
    connection
    companies-collection-name
    {:_id (h/value->object-id id) :status default-status}
    fields
    "Can not find company by id"
    "not-found"
    {:_id id :fields fields}))

(defn get-total
  "Function for get total count companies"
  [connection]
  (rh/get-total-documents connection companies-collection-name))