(ns services.language-service
  (:require
    [database.single.languages-repository :as r]
    [utils.constants :refer :all]
    [database.nested.language-value :as lv]))

(defn create-language
  "Function for create language document"
  [connection title values user]
  (let [new-language {:title title
                      :values (map lv/create-language-value values)
                      :status default-status
                      :owner user}
        document (r/create-language connection new-language)]
    {:document document :user user}))

(defn get-languages
  "Function for get language documents list"
  [connection limit skip]
  {:documents (r/get-language-list connection limit skip [])})

(defn get-language
  "Function for get language document"
  [connection document-id accept-language]
  (let [document (r/find-language-by-id connection document-id ["values"])]
    {:document {:_id (get document :_id)
                :value (lv/get-value-by-title document accept-language)}}))

(defn update-language
  "Function for update language document"
  [connection document-id]
  {:document nil})

(defn deactivate-language
  "Function for deactivate language document"
  [connection document-id]
  {:document nil})