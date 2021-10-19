(ns database.single.users-repository
  (:require
    [database.common.repository :as repository]
    [database.nested.profile :as prof]
    [database.nested.property :as prop]
    [database.nested.right :as rght]
    [utils.constants :refer :all])
  (:import (org.bson.types ObjectId)))

(defn- get-fields
  [fields]
  (if (nil? fields) [] fields))

(defn create-user
  "Function for create user in database"
  [connection data]
  (repository/create-document connection users-collection-name data))

(defn find-user-by-login
  "Function for find user by login"
  [connection login fields]
  (let [user (repository/get-collection-document
                      connection
                      users-collection-name
                      {:login login
                       :status default-status}
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
                users-collection-name
                {:_id (if (string? id) (ObjectId. ^String id) id)
                 :status default-status}
                true
                (get-fields fields))]
     (if (nil? user)
       (throw (ex-info
                "Can not find user by id"
                {:alias "can-not-find-user"
                 :info {:id id
                        :fields fields}}))
       user)))

(defn get-users-list
  "Function for get users"
  [connection limit skip fields]
  (let [opts {:collection users-collection-name
              :limit limit
              :skip skip}
        filter {:status default-status}
        sort {}
        users (repository/get-list-by-query connection opts filter sort fields)]
    (if (= (count users) 0)
      (throw (ex-info
               "Users list is empty"
               {:alias "not-found"
                :info {:limit limit
                       :skip skip}}))
      users)))

(defn update
  "Function for update user document"
  [connection user-id to-update fields]
  (repository/update-document connection users-collection-name user-id to-update)
  (find-user-by-id connection user-id fields))

(defn create-profile-property
  "Function for create user profile property by key & value"
  [connection user key value fields]
  (let [user-id (get user :_id nil)
        new-property (prof/create key value)
        new-profile {:profile (conj (get user :profile []) new-property)}
        to-update (merge user new-profile)]
    (repository/update-document connection users-collection-name user-id to-update)
    (find-user-by-id connection user-id fields)))

(defn create-user-property
  "Function for create user property by key & value"
  [connection user key value fields]
  (let [user-id (get user :_id nil)
        new-property (prop/create key value)
        new-properties {:properties (conj (get user :properties []) new-property)}
        to-update (merge user new-properties)]
    (repository/update-document connection users-collection-name user-id to-update)
    (find-user-by-id connection user-id fields)))

(defn create-user-right
  "Function for create user right"
  [connection user name-right create-rule read-rule update-rule delete-rule fields]
  (let [user-id (get user :_id nil)
        new-right (rght/create name-right create-rule read-rule update-rule delete-rule)
        new-rights {:rights (conj (get user :rights []) new-right)}
        to-update (merge user new-rights)]
    (repository/update-document connection users-collection-name user-id to-update)
    (find-user-by-id connection user-id fields)))

(defn get-total
  [connection]
  (repository/get-collection-count connection users-collection-name {:status "active"}))