(ns build
  (:require [clojure.tools.build.api :as b]
            [cljs.build.api :as cljs]))

(def build-folder "target")
(def jar-content (str build-folder "/classes"))
(def basis (b/create-basis {:project "deps.edn"}))
(def version "0.0.1")
(def app-name "counter-server")
(def uber-file (format "%s/%s-%s-standalone.jar" build-folder app-name version))


(defn clean [_]
  (b/delete {:path "target"})
  (println (str "build folder removed: " build-folder)))

(defn uber [_]
  (clean nil)
  (b/copy-dir {:src-dirs []
               :target-dir jar-content})
  
  (b/compile-clj {:basis basis
                  :src-dirs ["src/backend"]
                  :class-dir jar-content})
  
  (b/uber {:class-dir jar-content
           :uber-file uber-file
           :basis basis
           :main 'backend.core})
    (println "uber server file created:" uber-file))