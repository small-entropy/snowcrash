(ns database.nested.right
  (:import (org.bson.types ObjectId)))

(defn get-default-user-right
  "Function for get default user right"
  []
  [
   {:_id (ObjectId.) :name "users" :create "00100" :read "111001" :update "001000"}
   {:_id (ObjectId.) :name "catalogs" :create "00100" :read "111001" :update "001000"}
   {:_id (ObjectId.) :name "categories" :create "00100" :read "111001" :update "001000"}
   {:_id (ObjectId.) :name "companies" :create "00100" :read "111001" :update "001000"}
   {:_id (ObjectId.) :name "products" :create "00100" :read "111001" :update "001000"}
   {:_id (ObjectId.) :name "tags" :create "00100" :read "111001" :update "001000"}
   ])
