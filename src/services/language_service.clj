(ns services.language-service
  (:require
    [database.single.languages-repository :as r]
    [utils.constants :refer :all]
    [database.nested.language :as lang]))

(defn create-language-doc
  "Function for create language document"
  [title values user]
  {:title title
   :values (map lang/create-language-value values)
   :status default-status
   :owner user})

(defn create-language
  "Function for create language document"
  [connection title values user]
  (let [new-language (create-language-doc title values user)
        document (r/create-language connection new-language)]
    {:document document :user user}))

(defn get-languages
  "Function for get language documents list"
  ([connection limit skip]
   (let [documents (r/get-language-list connection limit skip [])
         total (r/get-total connection)]
     {:documents documents :total total}))
  ([connection limit skip accept-language]
   (let [documents (r/get-language-list connection limit skip [])
         total (r/get-total connection)]
     {:documents (map (fn[doc]
                        {:_id (get doc :_id nil)
                         :title (get doc :title nil)
                         :value (lang/get-value-by-title doc accept-language)}) documents)
      :total total})))

(defn get-language
  "Function for get language document"
  ([connection document-id]
   {:document (r/find-language-by-id connection document-id ["title" "values"])})
  ([connection document-id accept-language]
   (let [document (r/find-language-by-id connection document-id ["title" "values"])]
     {:document {:_id (get document :_id)
                 :title (get document :title nil)
                 :value (lang/get-value-by-title document accept-language)}})))

(defn- get-updated-language-document
  [document title values]
  (merge document {:title title
                   :values (map (fn [v]
                                  (lang/create-language-value
                                    (get v :_id nil)
                                    (get v :title nil)
                                    (get v :value nil))) values)}))

(defn update-language
  "Function for update language document"
  [connection document-id title values]
  (let [document (r/find-language-by-id connection document-id [])
        to-update (get-updated-language-document document title values)
        language (r/update-language connection document-id to-update [])]
    {:document language}))

(defn deactivate-language
  "Function for deactivate language document"
  [connection document-id ]
  (let [document (r/find-language-by-id connection document-id [])
        to-update (merge document {:status inactive-status})
        language (r/update-language connection document-id to-update [])]
    {:document language}))