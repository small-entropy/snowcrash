(ns database.single.companies-repository
  (:require
    [database.common.repository :as repository]
    [database.nested.profile :as prof]
    [database.nested.property :as prop]
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

(defn create-profile-property
  "Function for create company profile property by key & value"
  [connection company key value fields]
  (let [company-id (get company :_id nil)
        new-property (prop/create key value)
        new-profile {:profile (conj (get company :profile []) new-property)}
        to-update (merge company new-profile)]
    (repository/update-document connection companies-collection-name company-id to-update)
    (find-company-by-id connection company-id fields)))

(defn create-compamy-property
  "Function for create company property by key & value"
  [connection company key value fields]
  (let [company-id (get company :_id nil)
        new-property (prop/create key value)
        new-properties {:properties (conj (get company :properties []) new-property)}
        to-update (merge company new-properties)]
    (repository/update-document connection companies-collection-name company-id to-update)
    (find-company-by-id connection company-id fields)))

(defn update-document
  "Function for update company document"
  [connection company-id to-update fields]
  (rh/update-document
    connection
    companies-collection-name
    company-id
    to-update
    fields
    "Can not find company"
    "not-found"))

(defn get-total
  "Function for get total count companies"
  [connection]
  (rh/get-total-documents connection companies-collection-name))