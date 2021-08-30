(ns database.nested.right
  (:import (org.bson.types ObjectId))
  (:require [utils.constants :refer :all]))

(defn get-default-user-right
  "Function for get default user right"
  []
  [
   {:_id (ObjectId.) :name users-collection-name :create "00100" :read "011001" :update "001000"}
   {:_id (ObjectId.) :name catalogs-collection-name :create "00100" :read "111001" :update "001000"}
   {:_id (ObjectId.) :name categories-collection-name :create "00100" :read "111001" :update "001000"}
   {:_id (ObjectId.) :name companies-collection-name :create "00100" :read "111001" :update "001000"}
   {:_id (ObjectId.) :name products-collection-name :create "00100" :read "111001" :update "001000"}
   {:_id (ObjectId.) :name tags-collection-name :create "00100" :read "111001" :update "001000"}
   ])
