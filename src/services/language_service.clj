(ns services.language-service
  (:require
    [database.single.languages-repository :as r]
    [utils.constants :refer :all]
    [utils.books-service-helpers :as bsh]
    [database.nested.translate-value :as tv]))

(defn create-language-doc
  "Function for create language document"
  [title values user]
  {:title title
   :values (map tv/create-translate-value values)
   :status default-status
   :owner user})

(defn- get-fields [with-owner]
  (if with-owner
    ["title" "values" "owner"]
    ["title" "values"]))

(defn create-language
  "Function for create language document"
  [connection title values user]
  (bsh/create-document
    connection
    (create-language-doc title values user)
    user
    r/create-language))

(defn- build-value
  [doc accept-language]
  {:id (get doc :_id nil)
   :title (get doc :title nil)
   :value (tv/get-value-by-title doc accept-language)})

(defn get-languages
  "Function for get language documents list"
  ([connection limit skip]
   (bsh/get-documents
     connection
     limit
     skip
     r/get-language-list
     r/get-total
     get-fields))
  ([connection limit skip accept-language]
   (bsh/get-documents
     connection
     limit skip accept-language
     r/get-language-list
     r/get-total
     get-fields
     build-value)))


(defn get-language
  "Function for get language document"
  ([connection document-id]
   (bsh/get-document connection document-id r/find-language-by-id get-fields))
  ([connection document-id accept-language]
   (bsh/get-document
     connection
     document-id
     accept-language
     r/find-language-by-id
     get-fields
     build-value)))

(defn- get-to-update
  [connection document-id title values]
  (let [document (r/find-language-by-id connection document-id [])]
    (merge document {:title title
                     :values (map (fn [v]
                                    (tv/create-translate-value
                                      (get v :_id nil)
                                      (get v :title nil)
                                      (get v :value nil))) values)})))

(defn update-language
  "Function for update language document"
  [connection document-id title values]
  (bsh/update-document
    connection
    document-id
    (get-to-update connection document-id title values)
    r/update-language
    get-fields))

(defn deactivate-language
  "Function for deactivate language document"
  [connection document-id]
  (bsh/deactivate-document
    connection
    document-id
    r/find-language-by-id
    r/update-language
    get-fields))
