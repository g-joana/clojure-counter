(ns backend.core
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [GET PUT OPTIONS routes]]
            [compojure.route :as route]
            [cheshire.core :as json]
            [backend.db :as db]
            [clojure.pprint :as pp]))


(def headers
  {"Content-Type" "application/json" "Access-Control-Allow-Origin" "*"
   "Access-Control-Allow-Headers" "Content-type, Authorization"
   "Access-Control-Allow-Methods" "PUT, GET, OPTIONS" "Access-Control-Allow-Credentials" "true"})

(defn handler []
  (routes
   (GET "/" []
     {:status 200
      :headers headers
      :body (json/encode {:counter (db/current-value)})})
   (PUT "/inc" []
     {:status 200
      :headers headers
      :body (json/encode {:counter (db/inc-counter!)})})
   (OPTIONS "/inc" []
     {:status 200
      :headers headers
      :body ""})
   (PUT "/reset" []
     {:status 200
      :headers headers
      :body (json/encode {:counter (db/reset-counter!)})})
   (OPTIONS "/reset" []
     {:status 200
      :headers headers
      :body ""})
   (route/not-found "Not found")))


(defn start-server []
  (println "starting server on port 8080")
  ;; graceful shutdown: wait 100ms for existing requests to be finished
  ;; :timeout is optional, when no timeout, stop immediately
    (run-server (handler) {:port 8080}))

(defn -main []
    (start-server))

;; interactive dev
(defonce server (atom nil))

(defn start []
  (reset! server (start-server)))

(defn stop []
  (when @server
    (@server :timeout 100)
    (reset! server nil)
    (println "Server after reset:" @server)))

(defn restart []
  (stop)
  (start))
