(ns clojure-crud.core
  (:require [clojure.string :as str]
            [clojure.walk]
            [compojure.core :as comp]
            [ring.adapter.jetty :as ring]
            [hiccup.core :as hic]))

(defonce server (atom nil))

(defn -main [] (+ 1 1))
