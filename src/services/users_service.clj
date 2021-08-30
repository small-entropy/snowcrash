(ns services.users-service
  (:require
    [utils.password :as pwd]
    [database.single.users-repository :as rep]
    [database.nested.profile :as prof]
    [database.nested.property :as prop]
    [database.nested.right :as right]
    [utils.jwt :as jwt]
    [utils.rights :as ur]
    [utils.constants :refer :all])
  (:import (org.bson.types ObjectId)))

(defn get-fields-by-rule
  "Get list of fields by rule and owner"
  [rule owner]
  (let [public-fields ["_id" "login" "profile"]
        private-fields (into [] (concat public-fields ["properties"]))
        global-fields (into [] (concat private-fields ["tokens" "rights" "status"]))]
    (if (= owner :my)
      (cond
        (true? (get rule my-global false)) global-fields
        (true? (get rule my-private false)) private-fields
        :else public-fields)
      (cond
        (true? (get rule other-global false)) global-fields
        (true? (get rule other-private false)) private-fields
        :else public-fields))))

(defn register-user
  "Function for register new user"
  [connection login password]
  (if (or (nil? login) (nil? password))
    (throw (Exception. "Not send data for register user"))
    (let [derived-password (pwd/derive-password password)
          oid (ObjectId.)
          token (jwt/encode oid login)
          document (rep/create-user connection {:_id oid
                                                :login      login
                                                :password   derived-password
                                                :status     "active"
                                                :profile    (prof/get-default-user-profile)
                                                :properties (prop/get-default-user-properties)
                                                :rights     (right/get-default-user-right)
                                                :tokens     [token]})
          rule (ur/get-user-rule document users-collection-name :read)
          user (rep/find-user-by-username connection login (get-fields-by-rule rule :my))]
      {:document user :token token })))

(defn login-user
  "Function for login user"
  [connection login incoming-password]
  (if (or (nil? login) (nil? incoming-password))
    (throw (Exception. "Not send login or password"))
    (let [founded-user (rep/find-user-by-username connection login [])
          derived-password (get founded-user :password nil)
          result-check (pwd/check-password incoming-password derived-password)]
      (if (false? result-check)
        (throw (Exception. "Not correct user password"))
        (let [rule (ur/get-user-rule founded-user users-collection-name :read)
              user (rep/find-user-by-username connection login (get-fields-by-rule rule :my))]
          {:document user :token (first (get founded-user :tokens nil))})))))

(defn logout-user
  "Function for logout user"
  []
  nil)

(defn change-user-password
  "Function for change user password"
  []
  nil)

(defn get-users-list
  "Function for get users list"
  []
  nil)

(defn get-user
  "Function for get user"
  []
  nil)