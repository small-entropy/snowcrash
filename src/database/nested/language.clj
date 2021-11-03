(ns database.nested.language
  (:import (org.bson.types ObjectId)))

(defn create-language-value
  "Function for create nested language document"
  ([] (create-language-value {:title "unknown" :value nil}))
  ([value] (merge {:_id (ObjectId.)} value)))

(defn get-value-by-title
  "Function for get language translate from document by title"
  [document title]
  (let [values (get document :values [])
        value (filter (fn [current-value]
                        (= title (get current-value :title nil))) values)]
    (if (nil? value)
      (throw (ex-info
               "Can not find language"
               {:alias "not-found"
                :info {:title title
                       :values values}}))
      value)))