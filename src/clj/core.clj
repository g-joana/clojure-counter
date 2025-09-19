(ns core
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [defroutes GET PUT POST]]
            [compojure.route :as route]
            [cheshire.core :as json]))

(defonce counter (atom 0))

(defn inc-counter []
  (swap! counter inc))

(defn reset-counter []
  (reset! counter 0))

(defroutes app
  (GET "/" [] {:status 200
               :headers {"Content-Type" "application/json"}
               :body (json/encode {:counter @counter})})
  (PUT "/inc" [] {:status 200
                  :headers {"Content-Type" "application/json"}
                  :body (json/encode {:counter (inc-counter)})})
  (PUT "/reset" [] {:status 200
                    :headers {"Content-Type" "application/json"}
                    :body (json/encode {:counter (reset-counter)})})
  )

(defn start-server []
  (println "starting server on port 8080")
  ;; graceful shutdown: wait 100ms for existing requests to be finished
  ;; :timeout is optional, when no timeout, stop immediately
  (run-server app {:port 8080}))

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
