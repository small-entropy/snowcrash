(ns services.countries-service
  (:require
    [database.single.countries-repository :as r]
    [utils.constants :refer :all]
    [utils.books-service-helpers :as bsh]
    [database.nested.translate-value :as tv]))

(defn- get-fields
  [with-owner]
  (if with-owner
    ["title" "time-zones" "values" "owner"]
    ["title" "time-zones" "values"]))

(defn- build-value
  [doc accept-language]
  {:id (get doc :_id nil)
   :title (get doc :title nil)
   :time-zones (get doc :time-zones nil)
   :value (tv/get-value-by-title doc accept-language)})

(defn create-country-entity
  "Function for create pure country entity"
  [title time-zones values user]
  {:title title
   :time-zones time-zones
   :values (map tv/create-translate-value values)
   :status default-status
   :owner user})

(defn create-country
  "Function for create country document"
  [connection title time-zones values user]
  (bsh/create-document
    connection
    (create-country-entity title time-zones values user)
    user
    r/create-country))

(defn get-countries
  "Function for get countries documents list"
  ([connection limit skip]
   (bsh/get-documents
     connection
     limit
     skip
     r/get-countries-list
     r/get-total
     get-fields))
  ([connection limit skip accept-language]
   (bsh/get-documents
     connection
     limit
     skip
     accept-language
     r/get-countries-list
     r/get-total
     get-fields
     build-value)))

(defn get-country
  "Function for get country from book"
  ([connection document-id]
   (bsh/get-document
     connection
     document-id
     r/find-country-by-id
     get-fields))
  ([connection document-id accept-language]
   (bsh/get-document
     connection
     document-id
     accept-language
     r/find-country-by-id
     get-fields
     build-value)))

(defn- get-to-update
  [connection document-id title time-zones values]
  (let [document (r/find-country-by-id connection document-id [])]
    (merge document {:title title
                      :time-zones time-zones
                      :values (map (fn [v]
                                     (tv/create-translate-value
                                       (get v :_id nil)
                                       (get v :title nil)
                                       (get v :value))) values)})))

(defn update-country
  "Function for update country document"
  [connection document-id title time-zones values]
  (bsh/update-document
    connection
    document-id
    (get-to-update connection document-id title time-zones values)
    r/update-country
    get-fields))

(defn deactivate-country
  "Function for deactivate country document"
  [connection document-id]
  (bsh/deactivate-document
    connection
    document-id
    r/find-country-by-id
    r/update-country
    get-fields))