(ns database.single.users-repository
  (:require
    [database.common.repository :as repository]
    [database.nested.profile :as prof]
    [database.nested.property :as prop]
    [database.nested.right :as rght]
    [utils.constants :refer :all]
    [utils.helpers :as h]
    [utils.repository-helpers :as rh]))

(defn create-user
  "Function for create user in database"
  [connection data]
  (rh/create-document connection users-collection-name data))

(defn find-user-by-login
  "Function for find user by login"
  [connection login fields]
  (rh/find-document
    connection
    users-collection-name
    {:login login :status default-status}
    fields
    "Can not find user by login"
    "can-not-find-user"
    {:login login :fields fields}))

(defn find-user-by-id
  "Function for find user by id"
  [connection id fields]
  (rh/find-document
    connection
    users-collection-name
    {:_id (h/value->object-id id) :status default-status}
    fields
    "Can not find user by id"
    "can-not-find-user"
    {:_id id :fields fields}))

(defn get-users-list
  "Function for get users"
  [connection limit skip fields]
  (rh/list-documents
    connection
    users-collection-name
    limit
    skip
    {:status default-status}
    {}
    fields
    "Users list is empty"
    "not-found"
    {:limit limit :skip skip}))

(defn update-document
  "Function for update user document"
  [connection user-id to-update fields]
  (rh/update-document
    connection
    users-collection-name
    user-id
    to-update
    fields
    "Can not find user"
    "not-found"))

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
  "Function for get total count users"
  [connection]
  (rh/get-total-documents connection users-collection-name))