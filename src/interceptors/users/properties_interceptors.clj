(ns interceptors.users.properties-interceptors
  (:require
    [services.users-service :as service]
    [utils.answers :refer :all]
    [utils.helpers :refer :all]
    [utils.constants :refer :all]))

;; Interceptor for get user properties
(def properties-user-interceptor
  {:name ::properties-user-interceptor
   :enter
     (fn [context]
       (let [{connection :connection
              guid :guid
              token :token
              is-owner :is-owner
              user-id :user-id
              decoded-id :decoded-id} context
             {documents :documents user :user} (if (true? is-owner)
                            (service/get-user-properties connection user-id)
                            (service/get-user-properties connection user-id decoded-id))]
         (assoc context :response (ok guid documents {:token token
                                                      :request guid
                                                      :user user}))))})

;; Interceptor for get user property
(def properties-user-property-interceptor
  {:name ::properties-user-property-interceptor
   :enter
     (fn [context]
       (let [{connection :connection
              request :request
              guid :guid
              token :token
              is-owner :is-owner
              user-id :user-id
              decoded-id :decoded-id} context
             {property-id :property-id} (get request :path-params)
             {document :document user :user} (if (true? is-owner)
                            (service/get-user-property connection user-id property-id)
                            (service/get-user-property connection user-id decoded-id property-id))]
         (assoc context :response (ok guid document {:token token
                                                     :request guid
                                                     :user user}))))})

;; Interceptor for create user property
(def create-user-property-interceptor
  {:name ::create-user-property-interceptor
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
                            (service/create-user-property connection user-id key value)
                            (service/create-user-property connection user-id decoded-id key value))]
         (assoc context :response (ok guid documents {:token token
                                                      :request guid
                                                      :user user}))))})

;; Interceptor for delete user property
(def delete-user-property-interceptor
  {:name ::delete-user-property-interceptor
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
                            (service/delete-user-property connection user-id property-id)
                            (service/delete-user-property connection user-id decoded-id property-id))]
         (assoc context :response (ok guid documents {:token token
                                                      :request guid
                                                      :user user}))))})

;; Interceptor for update user property
(def update-user-property-interceptor
  {:name ::update-user-property-interceptor
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
                            (service/update-user-property connection user-id property-id key value)
                            (service/update-user-property connection user-id decoded-id property-id key value))]
         (assoc context :response (ok guid documents {:token token
                                                      :request guid
                                                      :user user}))))})