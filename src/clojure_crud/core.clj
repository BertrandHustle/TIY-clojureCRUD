(ns clojure-crud.core
  (:require [clojure.string :as str]
            [clojure.walk]
            [compojure.core :as comp]
            [ring.adapter.jetty :as ring]
            [ring.util.response :as response]
            [ring.middleware.params :as params]
            [hiccup.core :as hic]))

(defonce server (atom nil))
(defonce history (atom []))

;lets user enter search query
(defn add-history [search-query]
  ;(println "enter a search query")
  (let [file-text (pr-str @history)]
    ;vector error here
    (swap! history conj search-query)
    (spit "search-history.edn" file-text)))

;deletes search history
(defn delete-history [] (spit (str "search-history.edn") ""))

;webroot, displays search history
(comp/defroutes root
                (comp/GET "/" []
                  (let [history (slurp "search-history.edn")
                        ](hic/html
                          [:html history [:form {:action "/add" :method "post"}
                          [:input {:type "text" :name "search-query" :placeholder "search"}]
                          [:button {:type "submit"} "Search" ]]
                           [:form [:button "Delete history" [:action (delete-history)]]]])))

                (comp/POST "/add" request
                  (let [params (get request :params)
                        search-query (get params "search-query")]
                    (add-history search-query)
                    (response/redirect "/"))))


(defn -main []
  ;@ points to defonce binding
  (when @server
    (.stop @server))
  (let[root(params/wrap-params root)]
    (reset! server (ring/run-jetty root {:port 3000 :join? false}))))