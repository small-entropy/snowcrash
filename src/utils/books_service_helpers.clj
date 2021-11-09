(ns utils.books-service-helpers
  (:require
    [utils.constants :refer :all]))

(defn create-document
  "Common function for create document"
  [connection new-doc user fn-create]
  {:document (fn-create connection new-doc) :user user})

(defn get-documents
  "Common function for get list of documents for books"
  ([connection limit skip fn-list fn-total fn-fields]
   (let [documents (fn-list connection limit skip (fn-fields true))
         total (fn-total connection)]
     {:documents documents :total total}))
  ([connection limit skip accept-language fn-list fn-total fn-fields fn-value-builder]
   (let [documents (fn-list connection limit skip (fn-fields false))
         total (fn-total connection)]
     {:documents (map (fn [doc]
                        (fn-value-builder doc accept-language)) documents) :total total})))

(defn get-documents-with-owner
  "Common function for get list of documents with owner for books"
  ([connection limit skip fn-list fn-total fn-fields]
   (let [documents (fn-list connection limit skip (fn-fields true))
         total (fn-total connection)]
     {:documents documents :total total}))
  ([connection limit skip with-owner fn-list fn-total fn-fields]
   (let [documents (fn-list connection limit skip (fn-fields with-owner))
         total (fn-total connection)]
     {:documents documents :total total})))


(defn get-document
  "Common function for get document by id"
  ([connection document-id fn-document fn-fields]
   {:documents (fn-document connection document-id (fn-fields true))})
  ([connection document-id accept-language fn-document fn-fields fn-value-builder]
   (let [document (fn-document connection document-id (fn-fields false))]
     {:document (fn-value-builder document accept-language)})))

(defn get-document-with-owner
  "Common function for get document by id"
  ([connection document-id fn-document fn-fields]
   {:documents (fn-document connection document-id (fn-fields true))})
  ([connection document-id with-owner fn-document fn-fields]
   (let [document (fn-document connection document-id (fn-fields with-owner))]
     {:document document})))

(defn deactivate-document
  "Common function for deactivate document"
  [connection document-id fn-document fn-update fn-fields]
  (let [document (fn-document connection document-id [])
        to-update (merge document {:status inactive-status})
        updated (fn-update connection document-id to-update (fn-fields true))]
    {:document updated}))

(defn update-document
  [connection document-id to-update fn-update fn-fields]
  {:document (fn-update connection document-id to-update (fn-fields true))})