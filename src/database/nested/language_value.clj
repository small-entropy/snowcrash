(ns database.nested.language-value
  (:import (org.bson.types ObjectId)))

(defn create-language-value
  "Function for create nested language document"
  ([] (create-language-value "unknown-language"))
  ([value]{:_id (ObjectId.) :value value}))

(defn get-value-by-title
  "Function for get language translate from document by title"
  [document title]
  (let [values (get document :values [])
        value (filter (fn [current-value]
                        (= title (get current-value :value nil))) values)]
    (if (nil? value)
      (throw (ex-info
               "Can not find language"
               {:alias "not-found"
                :info {:title title
                       :values values}}))
      value)))

(defn get-value-by-id
  "Function for get language translate from document by id"
  [document id]
  (let [values (get document :values [])
        value (filter (fn [current-value]
                        (= id (get current-value :_id nil))) values)]
    (if (nil? value)
      (throw (ex-info
               "Can not find language"
               {:alias "not-found"
                :info {:_id id
                       :values values}}))
      value)))