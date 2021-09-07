(ns database.single.users-repository
  (:require
    [database.common.repository :as repository])
  (:import (org.bson.types ObjectId)))

(defonce collection "users")

(defn- get-fields
  [fields]
  (if (nil? fields) [] fields))

(defn create-user
  "Function for create user in database"
  [connection data]
  (repository/create-document connection collection data))

(defn find-user-by-login
  "Function for find user by login"
  [connection login fields]
  (let [user (repository/get-collection-document
                      connection
                      collection
                      {:login login}
                      true
                      (get-fields fields))]
    (if (nil? user)
      (throw (ex-info
               "Can not find user by login"
               {:alias "can-not-find-user"
                :info {:login login
                       :fields fields}}))
      user)))

(defn find-user-by-id
  "Function for find user by id.
  If send id as string - create ObjectId from string & use it,
  If send id as ObjectId - use it."
  [connection id fields]
   (let [user (repository/get-collection-document
                connection
                collection
                {:_id (if (string? id) (ObjectId. ^String id) id)}
                true
                (get-fields fields))]
     (if (nil? user)
       (throw (ex-info
                "Can not find user by id"
                {:alias "can-not-find-user"
                 :info {:id id
                        :fields fields}}))
       user)))