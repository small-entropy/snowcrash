(ns services.users-service
  (:require
    [utils.password :as pwd]
    [database.single.users-repository :as rep]
    [database.nested.profile :as prof]
    [database.nested.property :as prop]
    [database.nested.right :as right]
    [utils.jwt :as jwt]
    [utils.rights :as ur]
    [utils.constants :refer :all]
    [utils.helpers :refer :all])
  (:import (org.bson.types ObjectId)))

(defn get-fields-by-rule
  "Get list of fields by rule and owner"
  [rule owner]
  (let [public-fields ["_id" "login" "profile"]
        private-fields (into [] (concat public-fields ["properties"]))
        global-fields (into [] (concat private-fields ["tokens" "rights" "status"]))]
    (if (or (nil? rule) (nil? owner))
      public-fields
      (if (= owner :my)
        (cond
          (true? (get rule my-global false)) global-fields
          (true? (get rule my-private false)) private-fields
          :else public-fields)
        (cond
          (true? (get rule other-global false)) global-fields
          (true? (get rule other-private false)) private-fields
          :else public-fields)))))

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
                                                :status     default-status
                                                :profile    (prof/get-default-user-profile)
                                                :properties (prop/get-default-user-properties)
                                                :rights     (right/get-default-user-right)})
          rule (ur/get-user-rule document users-collection-name :read)
          user (rep/find-user-by-login connection login (get-fields-by-rule rule :my))]
      {:document user :token token })))

(defn login-user
  "Function for login user"
  ([connection login incoming-password]
   (if (or (nil? login) (nil? incoming-password))
     (throw (ex-info
              "Not send login or password"
              {:alias "can-not-login"
               :info {:login (not-send login)
                      :password (not-send incoming-password)}}))
     (let [founded-user (rep/find-user-by-login connection login [])
           derived-password (get founded-user :password nil)
           result-check (pwd/check-password incoming-password derived-password)]
       (if (false? result-check)
         (throw (ex-info
                  "Not correct user password"
                  {:alias "not-send-password"
                   :info {:password incoming-password}}))
         (let [rule (ur/get-user-rule founded-user users-collection-name :read)
               user (rep/find-user-by-login connection login (get-fields-by-rule rule :my))]
           {:document user :token (jwt/encode (get user :_id nil) login)})))))
  ([connection token]
   (if (nil? token)
     (throw (ex-info
              "Not send token"
              {:alias "not-send-token"
               :info {:token (not-send token)}}))
     (let [oid (jwt/decode-and-get token :id)
           founded-user (rep/find-user-by-id connection oid [])
           rule (ur/get-user-rule founded-user users-collection-name :read)
           user (rep/find-user-by-id connection oid (get-fields-by-rule rule :my))]
       {:document user :token token}))))

(defn- get-users-list-by-rule
  "Private function for get users list with authorization"
  [connection token limit skip]
  (let [oid (jwt/decode-and-get token :id)
        founded-user (rep/find-user-by-id connection oid [])
        rule (ur/get-user-rule founded-user users-collection-name :read)
        users (rep/get-users-list connection limit skip (get-fields-by-rule rule :other))
        total (rep/get-total connection)]
    {:documents users :token token :total total }))

(defn- get-users-list-without-rule
  "Private function for get users list without authorization"
  [connection limit skip]
  (let [fields (get-fields-by-rule nil nil)
        users (rep/get-users-list connection limit skip fields)
        total (rep/get-total connection)]
    {:documents users :token (not-send nil) :total total}))

(defn get-users-list
  "Public function for get users list"
  [connection token limit skip]
  (if (nil? token)
    (get-users-list-without-rule connection limit skip)
    (get-users-list-by-rule connection token limit skip)))

(defn get-user
  "Function for get user"
  ([connection user-id]
   (let [fields (get-fields-by-rule nil nil)
         user (rep/find-user-by-id connection user-id fields)]
     {:document user :decoded-user user}))
  ([connection user-id decoded-id is-owner]
   (let [founded-user (rep/find-user-by-id connection decoded-id [])
         rule (ur/get-user-rule founded-user users-collection-name :read)
         fields (get-fields-by-rule rule (if (true? is-owner) :my :other))
         user (rep/find-user-by-id connection user-id fields)]
     {:document user :decoded-user founded-user})))

(defn get-user-profile
  "Function for get user profile"
  [connection user-id]
  (let [{document :document} (get-user connection user-id)
        {login :login
         id :_id
         profile :profile} document]
    {:documents profile :user {:login login :_id id}}))

(defn- filter-property
  "Private function for find user property from collection"
  [profile user-id property-id]
  (let [founded (filter (fn [current-property]
                           (= property-id (str (get current-property :_id nil)))) profile)]
    (if (= (count founded) 0)
      (throw (ex-info
               "Can not find user property"
               {:alias "not-found"
                :info {:user-id user-id
                       :property-id property-id
                       :profile profile}}))
      (first founded))))

(defn get-user-profile-property
  "Function for get user profile properties"
  [connection user-id property-id]
  (let [{documents :documents user :user} (get-user-profile connection user-id)
        property (filter-property documents user-id property-id)]
    {:document property :user user}))

(defn logout-user
  "Function for logout user"
  [token]
  (let [token (jwt/decode token)
        id (get token :id default-empty-value)
        login (get token :login default-empty-value)]
    {:document {:_id id :login login} :token default-empty-value}))

(defn change-user-password
  "Function for change user password"
  [connection token password]
  (let [decoded-id (jwt/decode-and-get token :id)
        founded-user (rep/find-user-by-id connection decoded-id [])
        founded-user-password (get founded-user :password nil)
        rule (ur/get-user-rule founded-user users-collection-name :read)
        fields (get-fields-by-rule rule :my)]
    (if (pwd/check-password password founded-user-password)
      {:document (rep/find-user-by-id connection decoded-id fields)}
      (let [derived-password (pwd/derive-password password)
            to-update (merge founded-user {:password derived-password})
            user (rep/update-password connection decoded-id to-update fields)]
        {:document user}))))

(defn- filter-property-by-key
  "Private function for filter profile properties by key"
  [profile key]
  (let [founded (filter (fn [current-property]
                          (= key (get current-property :key nil))) profile)]
    (if (= (count founded) 0)
      false
      true)))

(defn create-user-profile-property
  "Function for create user profile property for user"
  ([connection user-id key value]
   (let [user (rep/find-user-by-id connection user-id [])
         profile (get user :profile nil)
         is-exist (filter-property-by-key profile key)]
     (if (true? is-exist)
       (throw (ex-info
                "Profile property already exist"
                {:alias "is-exist"
                 :info {:key key
                        :value value
                        :profile profile}}))
       (let [{_id :_id
              login :login
              updated-profile :profile} (rep/create-profile-property connection user key value [])]
         {:documents updated-profile :user {:_id _id
                                            :login login}}))))
  ([connection user-id decoded-id key value] nil))