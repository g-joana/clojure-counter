(ns frontend.core
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn update-counter! [new-value]
  (when-let [element (.getElementById js/document "counter")]
    (set! (.-innerHTML element) new-value)))



(defn fetch []
  (-> (js/fetch "http://localhost:8080/"
                (clj->js {:method "GET"
                          :headers {"Accept" "application/json"}}))
      (.then #(.json %)) 
      (.then #(do
                (update-counter! (.-counter %))))
      (.catch #(js/console.log "Error:" %))))

(fetch)

(defn reset []
  (-> (js/fetch "http://localhost:8080/reset"
                (clj->js {:method "PUT"
                          :headers {"Accept" "application/json"}}))
      (.then #(.json %))
      (.then #(do
                (update-counter! (.-counter %))))
      (.catch #(js/console.log "Error:" %))))

(defn increment []
  (-> (js/fetch "http://localhost:8080/inc"
                (clj->js {:method "PUT"
                          :headers {"Accept" "application/json"}}))
      (.then #(.json %))
      (.then #(do
                (update-counter! (.-counter %))))
      (.catch #(js/console.log "Error:" %))))




(defn setup-buttons []
  (when-let [button (.getElementById js/document "increment")]
    (.addEventListener button "click" increment))
  (when-let [button (.getElementById js/document "reset")]
    (.addEventListener button "click" reset)))

(setup-buttons)



