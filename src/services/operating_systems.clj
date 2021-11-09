(ns services.operating-systems
  (:require
    [database.single.os-repository :as r]
    [utils.constants :refer :all]
    [utils.books-service-helpers :as bsh]))

(defn create-os-doc
  "Function for create os document"
  [title is-mobile is-pc is-console requirements user]
  {:title title
   :is-mobile is-mobile
   :is-pc is-pc
   :is-console is-console
   :requirements requirements
   :status default-status
   :owner user})

(defn- get-fields
  [with-owner]
  (if with-owner
    ["title" "is-mobile" "is-pc" "is-console" "requirements" "owner"]
    ["title" "is-mobile" "is-pc" "is-console" "requirements"]))

(defn create-operating-system
  "Function for create OS document in database"
  [connection title is-mobile is-pc is-console requirements user]
  (bsh/create-document
    connection
    (create-os-doc title is-mobile is-pc is-console requirements user)
    user
    r/create-operating-system))

(defn get-operating-systems
  "Function for get list of OS documents"
  ([connection limit skip]
   (bsh/get-documents-with-owner
     connection
     limit
     skip
     r/get-operating-systems-list
     r/get-total
     get-fields))
  ([connection limit skip with-owner]
   (bsh/get-documents-with-owner
     connection
     limit
     skip
     with-owner
     r/get-operating-systems-list
     r/get-total
     get-fields)))

(defn get-operating-system
  "Function for get operating system document"
  ([connection document-id]
   (bsh/get-document-with-owner
     connection
     document-id
     r/find-operating-system-by-id
     get-fields))
  ([connection document-id with-owner]
   (bsh/get-document-with-owner
     connection
     document-id
     with-owner
     r/find-operating-system-by-id
     get-fields)))

(defn- get-to-update
  [connection document-id title is-mobile is-pc is-console requirements]
  (let [document (r/find-operating-system-by-id connection document-id [])]
    (merge document {:title title
                     :is-mobile is-mobile
                     :is-pc is-pc
                     :is-console is-console
                     :requirements requirements})))

(defn update-operating-system
  "Function for update OS document"
  [connection document-id title is-mobile is-pc is-console requirements]
  (bsh/update-document
    connection
    document-id
    (get-to-update connection document-id title is-mobile is-pc is-console requirements)
    r/update-operating-system
    get-fields))

(defn deactivate-operating-system
  "Function for deactivate OS document"
  [connection document-id]
  (bsh/deactivate-document
    connection
    document-id
    r/find-operating-system-by-id
    r/update-operating-system
    get-fields))