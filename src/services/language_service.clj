(ns services.language-service
  (:require
    [database.single.languages-repository :as r]
    [utils.constants :refer :all]))

(defn create-language
  "Function for create language document"
  [connection]
  {:document nil})

(defn get-languages
  "Function for get language documents list"
  [connection limit skip]
  {:documents []})

(defn get-language
  "Function for get language document"
  [connection document-id]
  {:document nil})

(defn update-language
  "Function for update language document"
  [connection document-id]
  {:document nil})

(defn deactivate-language
  "Function for deactivate language document"
  [connection document-id]
  {:document nil})