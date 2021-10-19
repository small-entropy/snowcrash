(ns interceptors.users.rights-interceptors
  (:require
    [services.users-service :as service]
    [utils.answers :refer :all]
    [utils.helpers :refer :all]
    [utils.constants :refer :all]))

;; Interceptor for get user rights
(def rights-user-interceptor
  {:name ::rights-user-interceptor
   :enter
    (fn [context]
      (let [{connection :connection
             guid :guid
             token :token
             is-owner :is-owner
             user-id :user-id
             decoded-id :decoded-id} context
            {documents :documents user :user} (if (true? is-owner)
                                                (service/get-user-rights connection user-id)
                                                (service/get-user-rights connection user-id decoded-id))]
        (assoc context :response (ok guid documents {:token token
                                                     :request guid
                                                     :user user}))))})

;; Interceptor for get user right
(def right-user-interceptor
  {:name ::right-user-interceptor
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
                                                     (service/get-user-right connection user-id property-id)
                                                     (service/get-user-right connection user-id decoded-id property-id))]
             (assoc context :response (ok guid document {:token token
                                                         :request guid
                                                         :user user}))))})

;; Interceptor for get user right
(def create-user-right-interceptor
  {:name ::right-user-interceptor
   :enter
         (fn [context]
           (let [{connection :connection
                  request :request
                  guid :guid
                  token :token
                  is-owner :is-owner
                  user-id :user-id
                  decoded-id :decoded-id} context
                 {right-name :name
                  create-rule :create
                  read-rule :read
                  update-rule :update
                  delete-rule :delete} (get request :json-params nil)
                 {documents :documents user :user} (if (true? is-owner)
                                                   (service/create-user-right connection user-id right-name create-rule read-rule update-rule delete-rule)
                                                   (service/create-user-right connection user-id decoded-id right-name create-rule read-rule update-rule delete-rule))]
             (assoc context :response (ok guid documents {:token token
                                                         :request guid
                                                         :user user}))))})