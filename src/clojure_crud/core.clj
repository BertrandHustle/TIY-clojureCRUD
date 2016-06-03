(ns clojure-crud.core
  (:require [clojure.string :as str]
            [clojure.walk]
            [compojure.core :as comp]
            [ring.adapter.jetty :as ring]
            [hiccup.core :as hic]))

(defonce server (atom nil))

;lets user enter search query
(defn add-history [search-query]
  ;(println "enter a search query")
  (let [file-text (pr-str search-query)]
    (spit (str "search-history.edn") file-text :append true)))

;deletes search history
(defn delete-history [] (spit (str "search-history.edn") ""))

;webroot, displays search history
(comp/defroutes root
                (comp/GET "/" [search-query]
                  (let [history (slurp "search-history.edn")
                        ](hic/html[:html history [:form [:input [:button "Delete history" [:action (delete-history)]] [:button "Search" [:action (add-history search-query)] [:type "submit"]]]]])))
                (comp/GET "/test" [] (hic/html [:html [:body] [:form [:input [:button ]]]])))



(defn -main []
  ;@ points to defonce binding
  (when @server
    (.stop @server))
  (reset! server (ring/run-jetty root {:port 3000 :join? false})))