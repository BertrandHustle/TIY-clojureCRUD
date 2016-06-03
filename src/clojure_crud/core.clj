(ns clojure-crud.core
  (:require [clojure.string :as str]
            [clojure.walk]
            [compojure.core :as comp]
            [ring.adapter.jetty :as ring]
            [hiccup.core :as hic]))

;(defonce server (atom nil))

(defn -main []
  (println "enter a search query")
  (let [query (read-line)
        file-text (pr-str query)]
    (spit (str "search-history.edn") file-text)
    (println query)))

