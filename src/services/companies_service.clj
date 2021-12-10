(ns services.companies-service
  (:require
    [database.single.companies-repository :as rep]
    [database.nested.profile :as prof]
    [database.nested.property :as prop]
    [utils.check-access :as access]
    [utils.nested-documents :refer :all]
    [utils.constants :refer :all]
    [utils.helpers :as h]
    [utils.rights :as ur])
  (:import (org.bson.types ObjectId)))

;; Function for get document fields
(defn- get-fields-by-rule
  [rule owner]
  (let [public-fields ["_id"
                       "title"
                       "logo"
                       "description"
                       "profile"
                       "publisher"
                       "developer"
                       "country"]
        private-fields (into [] (concat public-fields ["properties" "staff" "products"]))
        global-fields (into [] (concat private-fields ["owner" "status"]))]
    (access/get-fields-by-rule rule owner public-fields private-fields global-fields)))

(defn create-company
  "Function for create company"
  [connection user title logo description publisher developer country]
  (if (or (nil? title) (nil? publisher) (nil? developer))
    (throw (ex-info
             "Not send data for create company"
             {:alias "not-send-some-data"
              :info {:title (h/not-send title)
                     :publisher (h/not-send publisher)
                     :developer (h/not-send developer)}}))
    (let [owner {:_id (get user :_id nil)
                 :login (get user :login nil)}
          document (rep/create-company connection {:_id (ObjectId.)
                                                   :title title
                                                   :logo logo
                                                   :description description
                                                   :country country
                                                   :status default-status
                                                   :publisher publisher
                                                   :developer developer
                                                   :profile (prof/get-default-company-profile)
                                                   :properties (prop/get-default-company-properties)
                                                   :owner owner
                                                   :staff [owner]
                                                   :products []})]
      {:document document :user owner})))

(defn get-companies
  "Function for get companies list"
  ([connection user limit skip]
   (let [rule (ur/get-user-rule user companies-collection-name :read)
         documents (rep/get-companies-list connection limit skip (get-fields-by-rule rule :other))
         total (rep/get-total connection)]
     {:documents documents :total total}))
  ([connection limit skip]
   (let [documents (rep/get-companies-list connection limit skip (get-fields-by-rule nil nil))
         total (rep/get-total connection)]
     {:documents documents :total total})))

(defn- check-is-owner
  [connection company-id]
  (let [{owner :owner} (rep/find-company-by-id connection company-id ["owner"])
        owner-id (get owner :_id nil)]
    (if (nil? owner-id)
      (throw (ex-info
               "Can not get owner id"
               {:alias "internal-error"
                :info {:owner owner :company-id company-id}}))
      (= (str owner-id) company-id))))

(defn get-company
  "Function for get company document"
  ([connection company-id]
   {:documents (rep/find-company-by-id connection company-id (get-fields-by-rule nil nil))})
  ([connection company-id user]
   (let [rule (ur/get-user-rule user companies-collection-name :read)
         is-owner (check-is-owner connection company-id)
         document (rep/find-company-by-id connection company-id (get-fields-by-rule rule (if is-owner :my :other)))]
     {:document document})))

(defn get-company-profile
  "Function for get company profile"
  [company]
  (let [{id :_id profile :profile title :title} company]
    {:documents profile :company {:_id id :title title}}))

(defn get-company-profile-property
  "Function for get company profile property"
  [company property-id]
  (let [{_id :_id profile :profile title :title} company
        document (get-property
                   profile
                   property-id
                   "Can not find company property"
                   {:alias "not-found"
                    :info {:company-id (get company :_id nil)
                           :property-id property-id
                           :profile profile}})]
    {:document document :company {:_id _id :title title}}))

;; Private function for create profile property
(defn- profile-property->create
  [connection company key value]
  (let [profile (get company :profile [])]
    (if (exist-by-key? profile key)
      (throw
        (ex-info
          "Profile property already exist"
          {:alias "is-exist"
           :info {:key key :value value :profile profile}}))
      (let [{_id :_id
             title :title
             updated-profile :profile} (rep/create-profile-property connection company key value [])]
        {:documents updated-profile :company {:_id _id :title title}}))))
;; Private function for check right on action.
;; If action try call owner - return true.
;; If action try call not owner - check right & return result
(defn- check-right
  [user company action-name]
  (let [user-id (get user :_id nil)
        owner (get company :owner nil)
        is-owner (= (str (get owner :_id nil) (str user-id)))]
    (if is-owner
      is-owner
      (access/other-global?->by-user user companies-collection-name action-name))))

(defn create-company-profile-property
  "Function for create company profile property"
  [connection user company key value]
  (if (check-right user company :create)
    (profile-property->create connection company key value)
    (throw
      (ex-info
        "Hasn't access to create other profile company property"
        {:alias "has-not-access"
         :info {:user-id (get user :_id nil)
                :company-id (get company :_id nil)
                :key key
                :value value}}))))

;; Private function for update company profile property
(defn- profile-property->update
  [connection company property-id key value]
  (let [company-id (get company :_id nil)
        profile (get company :profile [])
        updated-profile (update-list profile property-id key value)
        to-update (merge company {:profile updated-profile})
        updated-company (rep/update-document connection company-id to-update [])]
    {:documents (get updated-company :profile [])
     :company {:_id (get updated-company :_id nil)
               :title (get updated-company :title nil)}}))

(defn update-company-profile-property
  "Function for update company profile property"
  [connection user company property-id key value]
  (if (check-right user company :update)
    (profile-property->update connection company property-id key value)
    (throw
      (ex-info
        "Hasn't access to update profile property"
        {:alias "has-not-access"
         :info {:property-id property-id
                :company-id (get company :_id nil)
                :key key
                :value value}}))))

;; Private function for delete company profile property
(defn- profile-property->delete
  [connection company property-id]
  (let [profile (get company :profile [])]
    (if (exist-by-id? profile property-id)
      (let [company-id (get company :_id nil)
            updated-profile (delete-from-list profile property-id)
            to-update (merge company {:profile updated-profile})
            updated-company (rep/update-document connection company-id to-update [])]
        {:documents (get updated-company :profile [])
         :company {:_id (get updated-company :_id nil)
                   :title (get updated-company :title nil)}})
      (throw
        (ex-info
          "Profile property not exist"
          {:alias "not-found"
           :info {:company-id (get company :_id nil)
                  :property-id property-id}})))))

(defn delete-company-profile-property
  "Function for delete company profile property"
  [connection user company property-id]
  (if (check-right user company :delete)
    (profile-property->delete connection company property-id)
    (throw
      (ex-info
        "Hasn't access to delete company profile property"
        {:alias "has-not-access"
         :info {:company-id (get company :_id nil)
                :property-id property-id}}))))

(defn get-company-properties
  "Function for get company properties"
  [user company]
  (if (check-right user company :read)
    (let [{_id :id title :title properties :properties} company]
      {:documents properties :company {:_id _id :title title}})
    (throw
      (ex-info
        "Hasn't access to get company properties"
        {:alias "has-not-access"
         :info {:company-id (get company :_id nil)
                :user {:_id (get user :_id nil)
                       :login (get user :login nil)}}}))))

(defn get-company-property
  "Function for get company property"
  [user company property-id]
  (if (check-right user company :read)
    (let [{_id :_id title :title properties :properties} company
          property (get-property
                     properties
                     property-id
                     "Can not fond company property"
                     {:alias "not-found"
                      :info {:company-id _id
                             :property-id property-id
                             :user {:_id (get user :_id nil)
                                    :login (get user :login nil)}}})]
      {:document property :company {:_id _id :title title}})
    (throw
      (ex-info
        "Hasn't access to get company property"
        {:alias "has-not-access"
         :info {:company-id (get company :_id nil)
                :property-id property-id
                :user {:_id (get user :_id nil)
                       :login (get user :login nil)}}}))))

;; Private function for create profile property
(defn- company-property->create
  [connection company key value]
  (let [properties (get company :properties [])]
    (if (exist-by-key? properties key)
      (throw
        (ex-info
          "Profile property already exist"
          {:alias "is-exist"
           :info {:key key :value value :properties properties}}))
      (let [{_id :_id
             title :title
             updated-properties :properties} (rep/create-company-property connection company key value [])]
        {:documents updated-properties :company {:_id _id :title title}}))))

(defn create-company-property
  "Function for create company property"
  [connection user company key value]
  (if (check-right user company :create)
    (company-property->create connection company key value)
    (throw
      (ex-info
        "Hasn't access to create company property"
        {:alias "has-not-access"
         :info {:company-id (get company :_id nil)
                :user {:_id (get user :_id nil)
                       :login (get user :login nil)}
                :key key
                :value value}}))))

;; Function for delete company property
(defn- company-property->delete
  "Function for delete property by id from company document"
  [connection company property-id]
  (let [properties (get company :properties [])]
    (if (exist-by-id? properties property-id)
      (let [updated-properties (delete-from-list properties nil)
            to-update (merge company {:properties updated-properties})
            {_id :_id
             properties :properties
             title :title} (rep/update-document
                              connection
                              (get company :_id nil)
                              to-update
                              [])]
        {:documents properties :company {:_id _id :title title}})
      (throw
        (ex-info
          "Company property not exist"
          {:alis "not-found"
           :info {:company-id (get company :_id nil) :property-id property-id} })))))

(defn delete-company-property
  "Function for delete company property"
  [connection user company property-id]
  (if (check-right user company :delete)
    (company-property->delete connection company property-id)
    (throw
      (ex-info
        "Hasn't access to create company property"
        {:alias "has-not-access"
         :info {:company-id (get company :_id nil)
                :property-id property-id
                :user {:_id (get user :_id nil)
                       :login (get user :login nil)}}}))))

;; Function for update company property
(defn- company-property->update
  [connection company property-id key value]
  (let [properties (get company :properties [])]
    (if (exist-by-id? properties property-id)
      (let [updated-properties (update-list properties property-id key value)
            to-update (merge company {:properties updated-properties})
            {documents :properties
             _id :_id
             title :title} (rep/update-document connection (get company :_id nil) to-update [])]
        {:documents documents :company {:_id _id :title title}})
      (throw
        (ex-info
          "Company property not exist"
          {:alias "not-found"
           :info {:company-id (get company :_id nil)
                  :property-id property-id
                  :key key
                  :value value}})))))

(defn update-company-property
  "Function for update company property"
  [connection user company property-id key value]
  (if (check-right user company :update)
    (company-property->update connection company property-id key value)
    (throw
      (ex-info
        "Hasn't access to update company property"
        {:alis "has-not-access"
         :info {:company-id (get company :_id nil)
                :property-id property-id
                :key key
                :value value
                :user {:_id (get user :_id nil)
                       :login (get user :login nil)}}}))))

(defn get-company-products
  "Function for get company products"
  [company]
  {:documents (get company :products [])
   :company {:_id (get company :_id nil)
             :title (get company :title nil)}})

(defn get-company-product
  "Function for get company products"
  [company product-id]
  (let [products (get company :products [])
        product (get-property
                  products
                  product-id
                  "Can not find user property"
                  {:alias "not-found"
                   :info {:product-id product-id
                          :company {:_id (get company :_id nil)
                                    :title (get company :title)
                                    :products products}}})]
    {:document product :company {:_id (get company :_id nil)
                                 :title (get company :title)}}))

;; Function for create company product
(defn- company-product->create
  [connection company product-id title image uri]
  (let [{_id :_id
         title :title
         products :products} (rep/create-company-product connection company product-id title image uri [])]
    {:documents products :company {:_id _id :title title}}))

(defn create-company-product
  "Function for create company product"
  [connection user company product-id title image uri]
  (if (check-right user company :create)
    (let [products (get company :products [])]
      (if (exist-by-id? products product-id)
        (throw
          (ex-info
            "Company product already exist"
            {:alias "is-exist"
             :info {:company-id (get company :_id nil)
                    :product-id product-id
                    :products products
                    :title title
                    :image image
                    :uri uri}}))
        (company-product->create connection company product-id title image uri)))
    (throw
      (ex-info
        "Hasn't access to create company product"
        {:alias "ha-not-access"
         :info {:company-id (get company :_id nil)
                :product-id product-id
                :title title
                :image image
                :uri uri}}))))