(ns backend.core
  (:require [org.httpkit.server :as hk]
            [compojure.core :refer [GET PUT OPTIONS defroutes]]
            [compojure.route :as route]
            [cheshire.core :as json]
            [backend.db :as db])
  (:gen-class))


(def headers
  {"Content-Type" "application/json"
   "Access-Control-Allow-Origin" "*"
   "Access-Control-Allow-Headers" "Content-type, Authorization"
   "Access-Control-Allow-Methods" "PUT, GET, OPTIONS"
   "Access-Control-Allow-Credentials" "true"})

(defn home-handler []
  {:status 200
   :headers headers
   :body (json/encode {:counter (db/current-value)})})

(defn inc-handler []
  {:status 200
   :headers headers
   :body (json/encode {:counter (db/inc-counter!)})})

(defn reset-handler []
  {:status 200
   :headers headers
   :body (json/encode {:counter (db/reset-counter!)})})

(defn options-handler []
  {:status 200
   :headers headers
   :body ""})

(defroutes app
  (GET "/" [] (home-handler))
  (PUT "/inc" [] (inc-handler))
  (PUT "/reset"  [] (reset-handler))
  (OPTIONS "/inc" [] (options-handler))
  (OPTIONS "/reset" [] (options-handler))
  (route/not-found "Not found"))

(defn start-server []
  (println "starting server on port 8080")
  (hk/run-server app {:port 8080}))

(defn -main []
  (start-server))