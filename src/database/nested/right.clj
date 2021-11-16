(ns database.nested.right
  (:import (org.bson.types ObjectId))
  (:require [utils.constants :refer :all]))

(defn get-default-user-right
  "Function for get default user right"
  []
  [
   {:_id (ObjectId.) :name users-collection-name :create "001000" :read "011001" :update "001000" :delete "001000"}
   {:_id (ObjectId.) :name catalogs-collection-name :create "001000" :read "111001" :update "001000" :delete "001000"}
   {:_id (ObjectId.) :name categories-collection-name :create "001000" :read "111001" :update "001000" :delete "001000"}
   {:_id (ObjectId.) :name companies-collection-name :create "001000" :read "111001" :update "001000" :delete "001000"}
   {:_id (ObjectId.) :name products-collection-name :create "001000" :read "111001" :update "001000" :delete "001000"}
   {:_id (ObjectId.) :name tags-collection-name :create "000000" :read "001001" :update "000000" :delete "000000"}
   {:_id (ObjectId.) :name languages-collection-name :create "000000" :read "001001" :update "000000" :delete "000000"}
   {:_id (ObjectId.) :name os-collection-name :create "000000" :read "001001" :update "000000" :delete "000000"}
   {:_id (ObjectId.) :name countries-collection-name :create "000000" :read "001001" :update "000000" :delete "000000"}
   {:_id (ObjectId.) :name cities-collection-name :create "000000" :read "001001" :update "000000" :delete "000000"}
   {:_id (ObjectId.) :name genres-collection-name :create "000000" :read "001001" :update "000000" :delete "000000"}
   ])

(defn create
  "Function for create new right nested document"
  [name-right create-rule read-rule update-rule delete-rule]
  {:_id (ObjectId.)
   :name name-right
   :create create-rule
   :read read-rule
   :update update-rule
   :delete delete-rule})