(ns database.single.countries-repository
  (:require
    [utils.constants :refer :all]
    [utils.helpers :as h]
    [utils.repository-helpers :as rh]))

(defn create-country
  "Function for create country document in database"
  [connection data]
  (rh/create-document connection countries-collection-name data))

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

(defn get-total
  "Function for get count of language documents"
  [connection]
  (rh/get-total-documents connection languages-collection-name))