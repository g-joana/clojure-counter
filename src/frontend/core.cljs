(ns frontend.core
  (:require [frontend.ws :as ws]))


(defn fetch []
  (-> (js/fetch "http://localhost:8080/"
                (clj->js {:method "GET"
                          :headers {"Accept" "application/json"}}))
      (.then #(.json %))
      (.then #(do
                (ws/update-counter! (.-counter %))))
      (.catch #(js/console.log "Error:" %))))

(fetch)


(defn setup-buttons []
  (when-let [button (.getElementById js/document "increment")]
    (.addEventListener button "click" #(.send @ws/connection "inc")))
  (when-let [button (.getElementById js/document "reset")]
    (.addEventListener button "click" #(.send @ws/connection "reset"))))

(setup-buttons)