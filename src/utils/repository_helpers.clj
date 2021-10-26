(ns utils.repository-helpers
  (:require
    [database.common.repository :as r]
    [utils.helpers :as h]
    [utils.constants :refer :all]))

(defn create-document
  "Default function for create document"
  [connection collection data]
  (r/create-document connection collection data))

(defn find-document
  "Default function for find document by id"
  [connection collection filter fields message alias info]
  (let [document (r/get-collection-document
                   connection
                   collection
                   filter
                   true
                   (h/values->get-collection fields))]
    (if (nil? document)
      (throw (ex-info
               message
               {:alias alias
                :info info}))
      document)))

(defn list-documents
  "Function for get documents list"
  [connection collection limit skip filter sort fields message alias info]
  (let [opts {:collection collection
              :limit limit
              :skip skip}
        documents (r/get-list-by-query connection opts filter sort fields)]
    (if (= (count documents) 0)
      (throw (ex-info
               message
               {:alias alias
                :info info}))
      documents)))

(defn update-document
  "Default function for update document"
  [connection collection document-id to-update fields message alias]
  (r/update-document connection collection document-id to-update)
  (find-document
    connection
    collection
    {:_id (h/value->object-id document-id) :status default-status}
    fields
    message
    alias
    {:_id document-id :fields fields}))

(defn get-total-documents
  ([connection collection]
   (get-total-documents connection collection {:status default-status}))
  ([connection collection filter]
   (r/get-collection-count connection collection filter)))