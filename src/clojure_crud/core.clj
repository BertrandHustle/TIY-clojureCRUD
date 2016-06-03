(ns clojure-crud.core
  (:require [clojure.string :as str]
            [clojure.walk]
            [compojure.core :as comp]
            [ring.adapter.jetty :as ring]
            [hiccup.core :as hic]))

(defonce server (atom nil))

;lets user enter search query
(defn query []
  (println "enter a search query")
  (let [query (read-line)
        file-text (pr-str query)]
    (spit (str "search-history.edn") file-text)
    (println query)))

;webroot, displays search history
(comp/defroutes root
                (comp/GET "/" []
                  (let [history (slurp "search-history.edn")
                        ](hic/html[:html history]))))
;search route
(comp/defroutes search
                (comp/GET "/search" []
                  (query)))

(defn -main []
  ;@ points to defonce binding
  (when @server
    (.stop @server))
  (reset! server (ring/run-jetty root {:port 3000 :join? false})))