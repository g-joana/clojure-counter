(ns backend.db
  (:require [datomic.client.api :as d]))


;; config db
(def client (d/client {:server-type   :datomic-local
                       :system "counter"
                       :storage-dir (str (System/getProperty "user.dir") "/data")}))

(def schema [{:db/ident :counter/value
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one}])

(defn create-db []
  (d/create-database client {:db-name "counter-state"}))

(defn create-connection []
  (d/connect client {:db-name "counter-state"}))

(defn create-schema [conn]
  (d/transact conn {:tx-data schema}))

(defn delete-db []
  (d/delete-database client {:db-name " counter-state"}))

(defn init-counter! [conn]
  (d/transact conn {:tx-data [{:counter/value 0}]}))



;; db views
;; #datom[entity-id attribute-id value tx-id added?]
(defn list-facts [conn]
  (let [db (d/db conn)]
    (d/datoms db {:index :eavt})))

(defn eids [conn]
  (d/q '[:find ?e
         :where [?e :counter/value]]
       (d/db conn)))



;; establish connection & init db
(defonce conn (create-connection))

(defn init-db! [conn]
  (when-not (contains? (set (d/list-databases client {})) "counter-state")
    (create-db)
    (create-schema conn)
    (init-counter! conn)))

(init-db! conn)
(def eid (ffirst (eids conn)))



;; db operations
(defn current-value []
  (ffirst (d/q '[:find ?v
                 :in $ ?e
                 :where [?e :counter/value ?v]]
               (d/db conn) eid)))

(defn inc-counter! []
  (let [new-value (inc (current-value))]
    (d/transact conn {:tx-data [{:db/id eid :counter/value new-value}]}) new-value))

(defn reset-counter! []
  (d/transact conn {:tx-data [{:db/id eid :counter/value 0}]}) 0)