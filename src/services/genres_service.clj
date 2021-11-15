(ns services.genres-service
  (:require
    [database.single.genres-repository :as r]
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

(defn create-genre-entity
  "Function for create pure genre entity"
  [title values user]
  {:title title
   :values (map tv/create-translate-value values)
   :status default-status
   :owner user})

(defn get-genres
  "Function for get genres list"
  ([connection limit skip]
   (bsh/get-documents
     connection
     limit
     skip
     r/get-genres-list
     r/get-total
     get-fields))
  ([connection limit skip accept-language]
   (bsh/get-documents
     connection
     limit
     skip
     accept-language
     r/get-genres-list
     r/get-total
     get-fields
     build-value)))

(defn create-genre
  "Function for create genre document"
  [connection title values user]
  (bsh/create-document
    connection
    (create-genre-entity title values user)
    user
    r/create-genre))

(defn get-genre
  "Function for get genre document"
  ([connection document-id]
   (bsh/get-document
     connection
     document-id
     r/find-genre-by-id
     get-fields))
  ([connection document-id accept-language]
   (bsh/get-document
     connection
     document-id
     accept-language
     r/find-genre-by-id
     get-fields
     build-value)))

(defn- get-to-update
  [connection document-id title values]
  (let [document (r/find-genre-by-id connection document-id [])]
    (merge document {:title title
                     :values (map (fn [v]
                                    (tv/create-translate-value
                                      (get v :_id nil)
                                      (get v :title nil)
                                      (get v :value nil))) values)})))

(defn update-genre
  "Function for update genre document"
  [connection document-id title values]
  (bsh/update-document
    connection
    document-id
    (get-to-update connection document-id title values)
    r/update-genre
    get-fields))

(defn deactivate-genre
  "Function for deactivate genre document"
  [connection document-id]
  (bsh/deactivate-document
    connection
    document-id
    r/find-genre-by-id
    r/update-genre
    get-fields))