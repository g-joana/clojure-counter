(ns core
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [defroutes GET PUT]]
            [compojure.route :as route]
            [cheshire.core :as json]))

(defroutes counter
  (GET "/" [] {:status 200
               :headers {"Content-Type" "application/json"}
               :body (json/generate-string {:hello "world"})})
  (PUT "/++" [] {:status 200
               :headers {"Content-Type" "application/json"}
               :body (json/generate-string {:hello "world"})})
  (PUT "/reset" [] {:status 200
               :headers {"Content-Type" "application/json"}
               :body (json/generate-string {:hello "world"})}))

(defn -main []
  (run-server counter {:port 8080})
  )
