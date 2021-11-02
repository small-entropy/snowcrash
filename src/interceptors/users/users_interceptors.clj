(ns interceptors.users.users-interceptors
  (:require
    [services.users-service :as service]
    [utils.answers :refer :all]
    [utils.helpers :as h]
    [utils.constants :refer :all]))

;; Interceptor for get paginate list users list
(def list-users-interceptor
  {:name ::list-users-interceptor
   :enter
    (fn [context]
      (let [{request :request guid :guid connection :connection} context
            query-params (get request :query-params nil)
            limit (h/get-limit query-params)
            skip (h/get-skip query-params)
            {header-token auth-header} (get request :headers nil)
            {documents :documents token :token total :total} (service/get-users-list connection header-token limit skip)]
        (assoc context :response (ok guid documents {:token token
                                                     :request guid
                                                     :total total
                                                     :limit limit
                                                     :skip skip}))))})
;; Interceptor for get user document
(def entity-users-interceptor
  {:name ::entity-users-interceptor
   :enter
     (fn [context]
       (let [{is-owner :is-owner
              user-id :user-id
              guid :guid
              token :token
              connection :connection
              decoded-id :decoded-id} context
             {document :document} (if (nil? token)
                                    (service/get-user connection user-id)
                                    (service/get-user connection user-id decoded-id is-owner))]
         (assoc context :response (ok guid document {:token (h/not-send token)
                                                     :request guid}))))})