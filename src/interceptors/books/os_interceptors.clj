(ns interceptors.books.os-interceptors)

;; Interceptor for get list of operating systems
(def list-os-interceptor
  {:name ::list-os-interceptor
   :enter
    (fn [context] nil)})

;; Interceptor for create operating system document
(def create-os-interceptor
  {:name ::create-os-interceptor
   :enter
    (fn [context] nil)})

;; Interceptor for get operating system document
(def get-os-interceptor
  {:name ::get-os-interceptor
   :enter
    (fn [context] nil)})

;; Interceptor fot update operation system document
(def update-os-interceptor
  {:name ::update-os-interceptor
   :enter
    (fn [context] nil)})

;; Interceptor for deactivate operating system document
(def deactivate-os-interceptor
  {:name ::deactivate-os-interceptor
   :enter
    (fn [context] nil)})