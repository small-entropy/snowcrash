(ns interceptors.users.profile-interceptors
  (:require
    [services.users-service :as service]
    [utils.answers :refer :all]
    [utils.helpers :refer :all]
    [utils.constants :refer :all]))

;; Interceptor for get user profile
(def profile-user-interceptor
  {:name ::profile-user-interceptor
   :enter
     (fn [context]
       (let [{connection :connection
              request :request
              guid :guid} context
             {header-token auth-header} (get request :headers nil)
             {user-id :user-id} (get request :path-params nil)
             {documents :documents user :user} (service/get-user-profile connection user-id)]
         (assoc context :response (ok guid documents {:token (not-send header-token)
                                                      :request guid
                                                      :user user}))))})

;; Interceptor for get user profile property
(def profile-user-property-interceptor
  {:name ::profile-user-property-interceptor
   :enter
     (fn [context]
       (let [{connection :connection
              request :request
              guid :guid} context
             {header-token auth-header} (get request :headers nil)
             {user-id :user-id property-id :property-id} (get request :path-params nil)
             {document :document user :user} (service/get-user-profile-property connection user-id property-id)]
         (assoc context :response (ok guid document {:token (not-send header-token)
                                                     :request guid
                                                     :user user}))))})

;; Interceptor for create user profile property
(def create-profile-user-property-interceptor
  {:name ::create-profile-user-property-interceptor
   :enter
     (fn [context]
       (let [{connection :connection
              request :request
              guid :guid
              token :token
              is-owner :is-owner
              user-id :user-id
              decoded-id :decoded-id} context
             {key :key value :value} (get request :json-params nil)
             {documents :documents user :user} (if (true? is-owner)
                            (service/create-user-profile-property connection user-id key value)
                            (service/create-user-profile-property connection user-id decoded-id key value))]
         (assoc context :response (ok guid documents {:token token
                                                      :request guid
                                                      :user user}))))})

;; Interceptor for update user profile property
(def update-profile-user-property-interceptor
  {:name ::update-profile-user-property-interceptor
   :enter
     (fn [context]
       (let [{connection :connection
              request :request
              guid :guid
              token :token
              is-owner :is-owner
              user-id :user-id
              decoded-id :decoded-id} context
             {property-id :property-id} (get request :path-params nil)
             {key :key value :value} (get request :json-params nil)
             {documents :documents user :user} (if (true? is-owner)
                            (service/update-user-profile-property connection property-id user-id key value)
                            (service/update-user-profile-property connection property-id user-id decoded-id key value))]
         (assoc context :response (ok guid documents {:token token
                                                      :request guid
                                                      :user user}))))})

;; Interceptor for delete user profile property
(def delete-profile-user-property-interceptor
  {:name ::delete-profile-user-property-interceptor
   :enter
     (fn [context]
       (let [{connection :connection
              request :request
              guid :guid
              token :token
              is-owner :is-owner
              user-id :user-id
              decoded-id :decoded-id} context
             {property-id :property-id} (get request :path-params nil)
             {documents :documents user :user} (if (true? is-owner)
                            (service/delete-user-profile-property connection user-id property-id)
                            (service/delete-user-profile-property connection user-id decoded-id property-id))]
         (assoc context :response (ok guid documents {:token token
                                                      :request guid
                                                      :user user}))))})