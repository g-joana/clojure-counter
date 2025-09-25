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

(defn handler [conn headers]
  (routes
   (GET "/" []
     {:status 200
      :headers headers
      :body (json/encode {:counter (db/current-value conn)})})
   (PUT "/inc" []
     {:status 200
      :headers headers
      :body (json/encode {:counter (db/safe-increment! conn)})})
   (OPTIONS "/inc" []
     {:status 200
      :headers headers
      :body ""})
   (PUT "/reset" []
     {:status 200
      :headers headers
      :body (json/encode {:counter (db/update! conn 0)})})
   (OPTIONS "/reset" []
     {:status 200
      :headers headers
      :body ""})
   (route/not-found "Not found")))


(defn start-server [conn]
  (println "starting server on port 8080")
  ;; graceful shutdown: wait 100ms for existing requests to be finished
  ;; :timeout is optional, when no timeout, stop immediately
    (run-server (handler conn headers) {:port 8080}))

(defn -main []
  (let [conn (db/create-connection)]
    (start-server conn)
    (db/create-schema conn)
    (db/init-counter! conn)))

;; interactive dev
(defonce server (atom nil))

(defn start [conn]
  (reset! server (start-server conn)))

(defn stop []
  (when @server
    (@server :timeout 100)
    (reset! server nil)
    (println "Server after reset:" @server)))

(defn restart [conn]
  (stop)
  (start conn))
