(ns services.users-service
  (:require
    [utils.password :as pwd]
    [database.single.users-repository :as rep]
    [database.nested.profile :as prof]
    [database.nested.property :as prop]
    [database.nested.right :as rght]
    [utils.check-access :as access]
    [utils.jwt :as jwt]
    [utils.rights :as ur]
    [utils.constants :refer :all]
    [utils.helpers :refer :all]
    [utils.nested-documents :refer :all])
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
                                                :rights     (rght/get-default-user-right)})
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

(defn get-user-profile-property
  "Function for get user profile properties"
  [connection user-id property-id]
  (let [{documents :documents user :user} (get-user-profile connection user-id)
        property (get-property
                   documents
                   property-id
                   "Can not find user profile property"
                   {:alias "not-found"
                    :info {:user-id user-id
                           :property-id property-id
                           :profile documents}})]
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
            user (rep/update-document connection decoded-id to-update fields)]
        {:document user}))))

(defn create-user-profile-property
  "Function for create user profile property for user"
  ([connection user-id key value]
   (let [user (rep/find-user-by-id connection user-id [])
         profile (get user :profile nil)
         is-exist (exist-by-key? profile key)]
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

(defn update-user-profile-property
  "Function for update user property"
  ([connection property-id user-id key value]
   (let [founded-user (rep/find-user-by-id connection user-id [])
         profile (get founded-user :profile nil)
         is-exist (exist-by-id? profile property-id)]
     (if (true? is-exist)
       (let [updated-profile (update-list profile property-id key value)
             to-update (merge founded-user {:profile updated-profile})
             updated-user (rep/update-document connection user-id to-update [])]
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

(defn delete-user-profile-property
  "Function for delete user profile property"
  ([connection user-id property-id]
   (let [founded-user (rep/find-user-by-id connection user-id [])
         profile (get founded-user :profile nil)
         is-exist (exist-by-id? profile property-id)]
     (if (true? is-exist)
       (let [updated-profile (delete-from-list profile property-id)
             to-update (merge founded-user {:profile updated-profile})
             updated-user (rep/update-document connection user-id to-update [])]
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

(defn get-user-properties
  "Function for get user properties"
  ([connection user-id]
   (if (access/my-private? connection users-collection-name user-id :read)
     (let [founded-user (rep/find-user-by-id connection user-id ["_id" "login" "properties"])
           properties (get founded-user :properties nil)]
       {:documents properties
        :user {:_id (get founded-user :_id nil)
               :login (get founded-user :login nil)}})
     (throw (ex-info
              "Hasn't access to get user properties"
              {:alias "has-not-access"
               :info {:user-id user-id}}))))
  ([connection user-id decoded-id]
   (if (access/other-private? connection users-collection-name decoded-id :read)
     (get-user-properties connection user-id)
     (throw (ex-info
              "Hasn't access to get other user properties"
              {:alias "has-not-access"
               :info {:user-id user-id
                      :decoded-id decoded-id}})))))

(defn get-user-property
  "Function for get user property"
  ([connection user-id property-id]
   (if (access/my-private? connection users-collection-name user-id :read)
     (let [{documents :documents
            user :user} (get-user-properties connection user-id)
           property (get-property
                      documents
                      property-id
                      "Can not find user property"
                      {:alias "not-found"
                       :info {:user-id user-id
                              :property-id property-id
                              :properties documents}})]
       {:document property :user user})
     (throw (ex-info
              "Hasn't access to get user property"
              {:alias "has-not-access"
               :info {:user-id user-id}}))))
  ([connection user-id decoded-id property-id]
   (if (access/other-private? connection users-collection-name decoded-id :read)
     (get-user-property connection user-id property-id)
     (throw (ex-info
              "Hasn't access to get other user property"
              {:alias "has-not-access"
               :info {:user-id user-id
                      :decoded-id decoded-id}})))))

(defn create-user-property
  "Function for create user property"
  ([connection user-id key value]
   (if (access/my-private? connection users-collection-name user-id :create)
     (let [user (rep/find-user-by-id connection user-id [])
           properties (get user :properties nil)
           is-exist (exist-by-key? properties key)]
       (if (true? is-exist)
         (throw (ex-info
                  "User property already exist"
                  {:alias "is-exist"
                   :info {:user-id user-id
                          :key key
                          :value value
                          :properties properties}}))
         (let [{_id :_id
                login :login
                updated-properties :properties} (rep/create-user-property connection user key value [])]
           {:documents updated-properties :user {:_id _id :login login}})))
     (throw (ex-info
              "Can't create user property"
              {:alias "has-not-access"
               :info {:user-id user-id
                      :key key
                      :value value}}))))
  ([connection user-id decoded-id key value]
   (if (access/other-private? connection users-collection-name decoded-id :create)
     (create-user-property connection user-id key value)
     (throw (ex-info
              "Can't create other user property"
              {:alias "has-not-access"
               :info {:user-id user-id
                      :decoded-id decoded-id
                      :key key
                      :value value}})))))

(defn delete-user-property
  "Function for delete user property"
  ([connection user-id property-id]
   (let [founded-user (rep/find-user-by-id connection user-id [])
         properties (get founded-user :properties nil)
         is-exist (exist-by-id? properties property-id)]
     (if (true? is-exist)
       (let [updated-properties (delete-from-list properties property-id)
             to-update (merge founded-user {:properties updated-properties})
             updated-user (rep/update-document connection user-id to-update [])]
         {:documents (get updated-user :properties nil)
          :user {:_id (get updated-user :_id nil)
                 :login (get updated-user :login nil)}})
       (throw (ex-info
                "User property not exist"
                {:alias "not-found"
                 :info {:user-id user-id
                        :property-id property-id}})))))
  ([connection user-id decoded-id property-id]
   (if (access/other-global? connection users-collection-name decoded-id :delete)
     (delete-user-property connection user-id property-id)
     (throw (ex-info
              "Hasn't access to delete other profile property"
              {:alias "has-not-access"
               :info {:user-id user-id
                      :decoded-id decoded-id
                      :property-id property-id}})))))

(defn update-user-property
  "Function for update user property"
  ([connection user-id property-id key value]
   (let [founded-user (rep/find-user-by-id connection user-id [])
         properties (get founded-user :properties nil)
         is-exist (exist-by-id? properties property-id)]
     (if (true? is-exist)
       (let [updated-properties (update-list properties property-id key value)
             to-update (merge founded-user {:properties updated-properties})
             updated-user (rep/update-document connection user-id to-update [])]
         {:documents (get updated-user :properties nil)
          :user {:_id (get updated-user :_id nil)
                 :login (get updated-user :login nil)}})
       (throw (ex-info
                "User property not exist"
                {:alias "not-found"
                 :info {:user-id user-id
                        :property-id property-id
                        :key key
                        :value value}})))))
  ([connection user-id decoded-id property-id key value]
   (if (access/other-global? connection users-collection-name decoded-id :update)
     (update-user-property connection user-id property-id key value)
     (throw (ex-info
              "Hasn't access to update other user property"
              {:alias "has-not-access"
               :info {:user-id user-id
                      :decoded-id decoded-id
                      :property-id property-id
                      :key key
                      :value value}})))))

(defn- get-user-right-by-id
  [connection user-id]
  (let [{_id :_id
         login :login
         rights :rights} (rep/find-user-by-id connection user-id ["_id" "login" "rights"])]
    {:documents rights :user {:_id _id
                              :login login}}))

(defn get-user-rights
  "Function for get user rights"
  ([connection user-id]
   (if (access/my-global? connection users-collection-name user-id :read)
     (get-user-right-by-id connection user-id)
     (throw (ex-info
              "Hasn't access to get user rights"
              {:alias "has-not-access"
               :info {:user-id user-id}}))))
  ([connection user-id decoded-id]
   (if (access/other-global? connection users-collection-name decoded-id :read)
     (get-user-right-by-id connection user-id)
     (throw (ex-info
              "Hasn't access to get other user rights"
              {:alias "has-not-access"
               :info {:user-id user-id
                      :decoded-id decoded-id}})))))

(defn get-user-right
  "Function for get user right"
  ([connection user-id property-id]
   (let [{documents :documents user :user} (get-user-rights connection user-id)
         right (filter-by-id documents property-id)]
     {:document right :user user}))
  ([connection user-id decoded-id property-id]
   (let [{documents :documents user :user} (get-user-rights connection user-id decoded-id)
         right (filter-by-id documents property-id)]
     {:document right :user user})))

(defn- add-user-right
  [connection user-id name-right create-rule read-rule update-rule delete-rule]
  (let [user (rep/find-user-by-id connection user-id [])
        rights (get user :rights nil)
        is-exist (exist-by-name? rights name-right)]
    (if (true? is-exist)
      (throw (ex-info
               "User rights already exist"
               {:alias "is-exist"
                :info {:user-id user-id
                       :right-name name-right
                       :create-rule create-rule
                       :read-rule read-rule
                       :update-rule update-rule
                       :delete-rule delete-rule}}))
      (let [{_id :_id
             login :login
             updated-rights :rights} (rep/create-user-right
                                       connection
                                       user
                                       name-right
                                       create-rule
                                       read-rule
                                       update-rule
                                       delete-rule
                                       [])]
        {:documents updated-rights :user {:_id _id :login login}}))))

(defn create-user-right
  "Function for create user right"
  ([connection user-id name-right create-rule read-rule update-rule delete-rule]
   (if (access/my-global? connection users-collection-name user-id :create)
     (add-user-right connection user-id name-right create-rule read-rule update-rule delete-rule)
     (throw (ex-info
              "Hasn't access to create user right"
              {:alias "has-not-access"
               :info {:user-id user-id
                      :name-right name-right
                      :create-rule create-rule
                      :read-rule read-rule
                      :update-rule update-rule
                      :delete-rule delete-rule}}))))
  ([connection user-id decoded-id name-right create-rule read-rule update-rule delete-rule]
   (if (access/other-global? connection users-collection-name decoded-id :create)
     (add-user-right connection user-id name-right create-rule read-rule update-rule delete-rule)
     (throw (ex-info
              "Hasn't access to create other user right"
              {:alias "has-not-access"
               :info {:user-id user-id
                      :decoded-id decoded-id
                      :name-right name-right
                      :create-rule create-rule
                      :read-rule read-rule
                      :update-rule update-rule
                      :delete-rule delete-rule}})))))

(defn- remove-user-property
  [connection user-id property-id]
  (let [founded-user (rep/find-user-by-id connection user-id [])
        rights (get founded-user :rights [])
        is-exist (exist-by-id? rights property-id)]
    (if (true? is-exist)
      (let [updated-rights (delete-from-list rights property-id)
            to-update (merge founded-user {:rights updated-rights})
            updated-user (rep/update-document connection user-id to-update [])]
        {:documents (get updated-user :rights nil)
         :user {:_id (get updated-user :_id nil)
                :login (get updated-user :login nil)}})
      (throw (ex-info
               "User right not found"
               {:alias "not-found"
                :info {:user-id user-id
                       :property-id property-id}})))))

(defn delete-user-right
  "Function for delete user right"
  ([connection user-id property-id]
   (if (access/my-global? connection users-collection-name user-id :delete)
     (remove-user-property connection user-id property-id)
     (throw (ex-info
              "Hasn't access to delete user property"
              {:alias "has-not-access"
               :info {:user-id user-id
                      :property-id property-id}}))))
  ([connection user-id decoded-id property-id]
   (if (access/other-global? connection users-collection-name decoded-id :delete)
     (remove-user-property connection user-id property-id)
     (throw (ex-info
              "Hasn't access to delete other user right"
              {:alias "has-not-access"
               :info {:user-id user-id
                      :decoded-id decoded-id
                      :property-id property-id}})))))

(defn- update-right
  [connection user-id property-id to-update]
  (let [founded-user (rep/find-user-by-id connection user-id [])
        rights (get founded-user :rights nil)
        is-exist (exist-by-id? rights property-id)]
    (if (true? is-exist)
      (let [updated-rights (map (fn [item]
                                  (if (= (str (get item :_id nil)) property-id)
                                    (merge item to-update)
                                    item)) rights)
            user-to-update (merge founded-user {:rights updated-rights})
            updated-user (rep/update-document connection user-id user-to-update [])]
        {:documents (get updated-user :rights nil)
         :user {:_id (get updated-user :_id nil)
                :login (get updated-user :login nil)}})
      (throw (ex-info
               "User right not found"
               {:alias "not-found"
                :info (merge {:user-id user-id
                              :property-id property-id} to-update)})))))

(defn update-user-right
  "Function for update user right"
  ([connection user-id property-id to-update]
   (if (access/my-global? connection users-collection-name user-id :update)
     (update-right connection user-id property-id to-update)
     (throw (ex-info
              "Hasn't access to update user right"
              {:alias "has-not-access"
               :info (merge {:user-id user-id
                             :property-id property-id} to-update)}))))
  ([connection user-id decoded-id property-id to-update]
   (if (access/other-global? connection users-collection-name decoded-id :update)
     (update-right connection user-id property-id to-update)
     (throw (ex-info
              "Hasn't access to update other user right"
              {:alias "has-not-access"
               :info (merge {:user-id user-id
                             :decoded-id decoded-id
                             :property-id property-id} to-update)})))))