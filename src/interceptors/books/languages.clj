(ns interceptors.books.languages)

;; Interceptor for get languages list
(def list-languages-interceptor
  {:name ::languages-interceptor
   :enter
    (fn [context] nil)})

;; Interceptor for get create language document
(defn create-language-interceptor
  {:enter ::create-language-interceptor
   :enter
    (fn [context] nil)})

;; Interceptor for get language document
(defn get-language-interceptor
  {:name ::get-language-interceptor
   :enter
    (fn [context] nil)})

;; Interceptor for update language document
(defn update-language-interceptor
  {:name ::update-language-interceptor
   :enter
    (fn [context] nil)})

;; Interceptor for deactivate language document
(defn deactivate-language-interceptor
  {:name ::deactivate-language-interceptor
   :enter
    (fn [context] nil)})