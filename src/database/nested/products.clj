(ns database.nested.products
  (:import (org.bson.types ObjectId)))

(defn create
  "Function for create company product"
  [id title image uri]
  {:_id (ObjectId ^String id) :title title :image image :uri uri})