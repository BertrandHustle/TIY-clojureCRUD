(ns clojure-crud.core
  (:require [clojure.string :as str]
            [clojure.walk]
            [compojure.core :as comp]
            [ring.adapter.jetty :as ring]
            [ring.util.response :as response]
            [ring.middleware.params :as params]
            [ring.middleware.session :as session]
            [hiccup.core :as hic]
            [clojure.java.io :as io]))

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
(defn delete-history [] (spit "search-history.edn" ""))

;webroot, displays search history
(comp/defroutes root
                (comp/GET "/" []
                    (hic/html
                          [:html [:form {:action "/add" :method "post"}
                          [:input {:type "text" :name "search-query" :placeholder "search"}]
                          [:button {:type "submit"} "Search" ]
                          [:ol (map (fn [searches] [:li searches]) @history)]]]))

                (comp/POST "/add" request
                  (let [param (get request :params)
                        search-query (get param "search-query")]
                    (swap! history conj search-query)
                    (spit "search-history.edn" (pr-str @history))
                  (response/redirect "/")))

                (comp/GET "/delete" []
                  (spit "search-history.edn" "")
                  (reset! history [])
                  (response/redirect "/")))


(defn -main []
  ;@ points to defonce binding
  (try
    (let [history (slurp "search-history.edn")
          str-history (read-string history)]
      (reset! history str-history))
    (catch Exception _))
  (when @server
    (.stop @server))
  (let[root(params/wrap-params root)]
    (reset! server (ring/run-jetty root {:port 3000 :join? false}))))