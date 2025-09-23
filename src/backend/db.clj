(ns backend.db
  (:require [datomic.client.api :as d])
  (:require [clojure.pprint :as pp]))

(def client (d/client {:server-type   :dev-local
                       :system "counter"
                       :storage-dir "/home/g-joana/clojure-counter/data"
                       ;;             :storage-dir dev-local stores databases under $ {storage-dir} /$ {system} /$ {db-name}
                       }))

(d/create-database client {:db-name "counter-state"})

(println "test")

(defonce connection (d/connect client {:db-name "counter-state"}))

(def schema [{:db/ident :counter/value  ;; entity is represented by entity/attribute 
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one}])

(pp/pprint (d/transact connection {:tx-data schema}))

(defn update! [connection {:keys [counter]}]
  (pp/pprint (d/transact connection {:tx-data [[:db/add "temp-id?" :counter counter]]})))

(println (d/list-databases client {}))



(defn delete []
  (d/delete-database client {:db-name "counter-state"}))
