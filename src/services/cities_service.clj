(ns services.cities-service
  (:require
    [database.single.cities-repository :as r]
    [utils.constants :refer :all]
    [database.nested.translate-value :as tv]
    [utils.books-service-helpers :as bsh]))

(defn- get-fields
  [with-owner]
  (if with-owner
    ["title" "time-zone" "post-code" "values" "owner"]
    ["title" "time-zone" "post-code" "values"]))

(defn- build-value
  [doc accept-language]
  {:_id (get doc :_id nil)
   :title (get doc :title nil)
   :time-zone (get doc :time-zone nil)
   :post-code (get doc :post-code nil)
   :value (tv/get-value-by-title doc accept-language)})

(defn create-city-entity
  "Function for create pure city entity"
  [title time-zone post-code values user]
  {:title title
   :time-zone time-zone
   :post-code post-code
   :values (map tv/create-translate-value values)
   :status default-status
   :owner user})

(defn get-cities
  "Function for get cities list"
  ([connection limit skip]
   (bsh/get-documents
     connection
     limit
     skip
     r/get-cities-list
     r/get-total
     get-fields))
  ([connection limit skip accept-language]
   (bsh/get-documents
     connection
     limit
     skip
     accept-language
     r/get-cities-list
     r/get-total
     get-fields
     build-value)))

(defn create-city
  "Function for create city document"
  [connection title time-zone post-code values user]
  (bsh/create-document
    connection
    (create-city-entity title time-zone post-code values user)
    user
    r/create-city))

(defn get-city
  "Function for get city document"
  ([connection document-id]
   (bsh/get-document
     connection
     document-id
     r/find-city-by-id
     get-fields))
  ([connection document-id accept-language]
   (bsh/get-document
     connection
     document-id
     accept-language
     r/find-city-by-id
     get-fields
     build-value)))

(defn- get-to-update
  [connection document-id title time-zone post-code values]
  (let [document (r/find-city-by-id connection document-id [])]
    (merge document {:title title
                     :time-zone time-zone
                     :post-code post-code
                     :values (map (fn [v]
                                    (tv/create-translate-value
                                      (get v :_id nil)
                                      (get v :title nil)
                                      (get v :value))) values)})))

(defn update-city
  "Function for update city document"
  [connection document-id title time-zone post-code values]
  (bsh/update-document
    connection
    document-id
    (get-to-update connection document-id title time-zone post-code values)
    r/update-city
    get-fields))

(defn deactivate-city
  "Function for deactivate city document"
  [connection document-id]
  (bsh/deactivate-document
    connection
    document-id
    r/find-city-by-id
    r/update-city
    get-fields))