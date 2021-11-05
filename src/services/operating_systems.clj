(ns services.operating-systems
  (:require
    [database.single.os-repository :as r]
    [utils.constants :refer :all]))

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
  (let [new-os (create-os-doc title is-mobile is-pc is-console requirements user)
        document (r/create-operating-system connection new-os)]
    {:document document :user user}))

(defn get-operating-systems
  "Function for get list of OS documents"
  ([connection limit skip]
   (get-operating-systems connection limit skip false))
  ([connection limit skip with-owner]
   (let [documents (r/get-operating-systems-list connection limit skip (get-fields with-owner))
         total (r/get-total connection)]
     {:documents documents :total total})))

(defn get-operating-system
  "Function for get operating system document"
  ([connection document-id]
   (get-operating-system connection document-id false))
  ([connection document-id with-owner]
   {:document (r/find-operating-system-by-id connection document-id (get-fields with-owner))}))

(defn- get-updated-document
  [document title is-mobile is-pc is-console requirements]
  (merge document {:title title
                   :is-mobile is-mobile
                   :is-pc is-pc
                   :is-console is-console
                   :requirements requirements}))

(defn update-operating-system
  "Function for update OS document"
  [connection document-id title is-mobile is-pc is-console requirements]
  (let [document (r/find-operating-system-by-id connection document-id [])
        to-update (get-updated-document document title is-mobile is-pc is-console requirements)
        os (r/update-operating-system connection document-id to-update (get-fields true))]
    {:document os}))

(defn deactivate-operating-system
  "Function for deactivate OS document"
  [connection document-id]
  (let [document (r/find-operating-system-by-id connection document-id [])
        to-update (merge document {:status inactive-status})
        os (r/update-operating-system connection document-id to-update (get-fields true))]
    {:document os}))