(ns services.tags-service
  (:require
    [database.single.tags-repository :as r]
    [utils.constants :refer :all]
    [database.nested.translate-value :as tv]
    [utils.books-service-helpers :as bsh]))

(defn- get-fields
  [with-owner]
  (if with-owner
    ["title" "values" "owner"]
    ["title" "values"]))

(defn- build-value
  [doc accept-language]
  {:_id (get doc :_id nil)
   :title (get doc :title nil)
   :value (tv/get-value-by-title doc accept-language)})

(defn create-tag-entity
  "Function for create pure tag entity"
  [title values user]
  {:title title
   :values (map tv/create-translate-value values)
   :status default-status
   :owner user})

(defn get-tags
  "Function for get tags list"
  ([connection limit skip]
   (bsh/get-documents
     connection
     limit
     skip
     r/get-tags-list
     r/get-total
     get-fields))
  ([connection limit skip accept-language]
   (bsh/get-documents
     connection
     limit
     skip
     accept-language
     r/get-tags-list
     r/get-total
     get-fields
     build-value)))

(defn create-tag
  "Function for create tag document"
  [connection title values user]
  (bsh/create-document
    connection
    (create-tag-entity title values user)
    user
    r/create-tag))

(defn get-tag
  "Function for get tag document"
  ([connection document-id]
   (bsh/get-document
     connection
     document-id
     r/find-tag-by-id
     get-fields))
  ([connection document-id accept-language]
   (bsh/get-document
     connection
     document-id
     accept-language
     r/find-tag-by-id
     get-fields
     build-value)))

(defn- get-to-update
  [connection document-id title values]
  (let [document (r/find-tag-by-id connection document-id [])]
    (merge document {:title title
                     :values (map (fn [v]
                                 (tv/create-translate-value
                                   (get v :_id nil)
                                   (get v :title nil)
                                   (get v :value nil))) values)})))

(defn update-tag
  "Function for update tag document"
  [connection document-id title values]
  (bsh/update-document
    connection
    document-id
    (get-to-update connection document-id title values)
    r/update-tag
    get-fields))

(defn deactivate-tag
  "Function for deactivate tag document"
  [connection document-id]
  (bsh/deactivate-document
    connection
    document-id
    r/find-tag-by-id
    r/update-tag
    get-fields))