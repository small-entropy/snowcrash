(ns services.users-service
  (:require
    [utils.password :as pwd]
    [database.single.users-repository :as rep]
    [database.nested.profile :as prof]
    [database.nested.property :as prop]
    [database.nested.right :as right]
    [utils.check-access :as access]
    [utils.jwt :as jwt]
    [utils.rights :as ur]
    [utils.constants :refer :all]
    [utils.helpers :refer :all])
  (:import (org.bson.types ObjectId)))

(defn- get-fields-by-rule
  [rule owner]
  (let [public-fields ["_id" "login" "profile"]
        private-fields (into [] (concat public-fields ["properties"]))
        global-fields (into [] (concat private-fields ["tokens" "rights" "status"]))]
    (access/get-fields-by-rule rule owner public-fields private-fields global-fields)))

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
  [connection token limit skip]
  (let [oid (jwt/decode-and-get token :id)
        founded-user (rep/find-user-by-id connection oid [])
        rule (ur/get-user-rule founded-user users-collection-name :read)
        users (rep/get-users-list connection limit skip (get-fields-by-rule rule :other))
        total (rep/get-total connection)]
    {:documents users :token token :total total }))

(defn- get-users-list-without-rule
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
  [values property-id]
  (filter (fn [current-property]
            (= property-id (str (get current-property :_id nil)))) values))

(defn- filter-profile-property
  "Private function for find user property from collection"
  [profile user-id property-id]
  (let [founded (filter-property profile property-id)]
    (if (= (count founded) 0)
      (throw (ex-info
               "Can not find user profile property"
               {:alias "not-found"
                :info {:user-id user-id
                       :property-id property-id
                       :profile profile}}))
      (first founded))))

(defn get-user-profile-property
  "Function for get user profile properties"
  [connection user-id property-id]
  (let [{documents :documents user :user} (get-user-profile connection user-id)
        property (filter-profile-property documents user-id property-id)]
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

(defn- exist-in-profile-by-key?
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
         is-exist (exist-in-profile-by-key? profile key)]
     (if (true? is-exist)
       (throw (ex-info
                "Profile property already exist"
                {:alias "is-exist"
                 :info {:key key :value value :profile profile}}))
       (let [{_id :_id
              login :login
              updated-profile :profile} (rep/create-profile-property connection user key value [])]
         {:documents updated-profile :user {:_id _id :login login}}))))
  ([connection user-id decoded-id key value]
     (if (access/other-global? connection users-collection-name decoded-id :create)
       (create-user-profile-property connection user-id key value)
       (throw (ex-info
                "Hasn't access to create other profile property"
                {:alias "has-not-access"
                 :info {:user-id user-id
                        :decoded-id decoded-id
                        :key key
                        :value value}})))))

(defn- exist-in-profile-by-id?
  [profile id]
  (let [founded (filter (fn [current-property]
                          (= id (str (get current-property :_id nil)))) profile)]
    (if (= (count founded) 0)
      false
      true)))

(defn- update-profile-list
  [profile property-id key value]
  (map (fn [item]
         (if (= (str (get item :_id nil)) property-id)
           {:_id (get item :_id nil) :key key :value value} item)) profile))

(defn update-user-profile-property
  "Function for update user property"
  ([connection property-id user-id key value]
   (let [founded-user (rep/find-user-by-id connection user-id [])
         profile (get founded-user :profile nil)
         is-exist (exist-in-profile-by-id? profile property-id)]
     (if (true? is-exist)
       (let [updated-profile (update-profile-list profile property-id key value)
             to-update (merge founded-user {:profile updated-profile})
             updated-user (rep/update-profile-property connection user-id to-update [])]
         {:documents (get updated-user :profile nil)
          :user {:_id (get updated-user :_id nil)
                 :login (get updated-user :login)}})
       (throw (ex-info
                "Profile property not exist"
                {:alias "not-found"
                 :info {:user-id user-id
                        :property-id property-id
                        :key key
                        :value value}})))))
  ([connection property-id user-id decoded-id key value]
     (if (access/other-global? connection users-collection-name decoded-id :update)
       (update-user-profile-property connection property-id user-id key value)
       (throw (ex-info
                "Hasn't access to update other profile property"
                {:alias "has-not-access"
                 :info {:user-id user-id
                        :decoded-id decoded-id
                        :property-id property-id
                        :key key
                        :value value}})))))

(defn- delete-from-profile-list
  [profile property-id]
  (filter (fn [current-property]
             (not= property-id (str (get current-property :_id nil)))) profile))

(defn delete-user-profile-property
  "Function for delete user profile property"
  ([connection user-id property-id]
   (let [founded-user (rep/find-user-by-id connection user-id [])
         profile (get founded-user :profile nil)
         is-exist (exist-in-profile-by-id? profile property-id)]
     (if (true? is-exist)
       (let [updated-profile (delete-from-profile-list profile property-id)
             to-update (merge founded-user {:profile updated-profile})
             updated-user (rep/update-profile-property connection user-id to-update [])]
         {:documents (get updated-user :profile nil)
          :user {:_id (get updated-user :_id nil)
                 :login (get updated-user :login nil)}})
       (throw (ex-info
                "Profile property not exist"
                {:alias "not-found"
                 :info {:user-id user-id
                        :property-id property-id}})))))
  ([connection user-id decoded-id property-id]
     (if (access/other-global? connection users-collection-name decoded-id :delete)
       (delete-user-profile-property connection user-id property-id)
       (throw (ex-info
                "Hasn't access to delete other profile property"
                {:alias "has-not-access"
                 :info {:user-id user-id
                        :decoded-id decoded-id
                        :property-id property-id}})))))

(defn- get-user-properties-common
  [connection user-id]
  (let [founded-user (rep/find-user-by-id connection user-id ["_id" "login" "properties"])
        properties (get founded-user :properties nil)]
    {:documents properties
     :user {:_id (get founded-user :_id nil)
            :login (get founded-user :login nil)}}))

(defn get-user-properties
  "Function for get user properties"
  ([connection user-id]
   (if (access/my-private? connection users-collection-name user-id :read)
     (get-user-properties-common connection user-id)
     (throw (ex-info
              "Hasn't access to get user properties"
              {:alias "has-not-access"
               :info {:user-id user-id}}))))
  ([connection user-id decoded-id]
   (if (access/other-private? connection users-collection-name decoded-id :read)
     (get-user-properties-common connection user-id)
     (throw (ex-info
              "Hasn't access to get other user properties"
              {:alias "has-not-access"
               :info {:user-id user-id
                      :decoded-id decoded-id}})))))

(defn- filter-properties-property
  [properties user-id property-id]
  (let [founded (filter-property properties property-id)]
    (if (= (count founded) 0)
      (throw (ex-info
               "Can not find user property"
               {:alias "not-found"
                :info {:user-id user-id
                       :property-id property-id
                       :properties properties}}))
      (first founded))))

(defn- get-user-property-common
  [connection user-id property-id]
  (let [{documents :documents
         user :user} (get-user-properties-common connection user-id)
        property (filter-properties-property documents user-id property-id)]
    {:document property :user user}))

(defn get-user-property
  "Function for get user property"
  ([connection user-id property-id]
   (if (access/my-private? connection users-collection-name user-id :read)
     (get-user-property-common connection user-id property-id)
     (throw (ex-info
              "Hasn't access to get user property"
              {:alias "has-not-access"
               :info {:user-id user-id}}))))
  ([connection user-id decoded-id property-id]
   (if (access/other-private? connection users-collection-name decoded-id :read)
     (get-user-property-common connection user-id property-id)
     (throw (ex-info
              "Hasn't access to get other user property"
              {:alias "has-not-access"
               :info {:user-id user-id
                      :decoded-id decoded-id}})))))