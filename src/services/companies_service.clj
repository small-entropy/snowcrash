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
                                                   :products {:total 0
                                                              :list []}})]
      {:document document })))

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
                :info {:owner owner
                       :company-id company-id}}))
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
  [connection company-id]
  (let [{id :_id
         profile :profile
         title :title} (rep/find-company-by-id connection company-id ["profile"])]
    {:documents profile :company {:_id id :title title}}))

(defn get-company-profile-property
  "Function for get company profile property"
  [connection company-id property-id]
  (let [{documents :documents
         company :company} (get-company-profile connection company-id)
        document (get-property
                   documents
                   property-id
                   "Can not find company property"
                   {:alias "not-found"
                    :info {:company-id company-id
                           :property-id property-id
                           :profile documents}})]
    {:document document :company company}))

;; Private function for create profile property
(defn- profile-property->create-&-save
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
        {:documents updated-profile :company {:_id _id
                                              :title title}}))))
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
  [connection user company-id key value]
  (let [company (rep/find-company-by-id connection company-id [])]
    (if (check-right company user :read)
      (profile-property->create-&-save connection company key value)
      (throw
        (ex-info
          "Hasn't access to create other profile company property"
          {:alias "has-not-access"
           :info {:user-id (get user :_id nil)
                  :company-id (get company :_id nil)
                  :key key
                  :value value}})))))