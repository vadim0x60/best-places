(require '[org.httpkit.client :as h])
(require '[clojure.data.json :as json])
(require '[clojure.walk :refer [postwalk]])
(require '[clojure.string :as s])
(require '[environ.core :refer [env]])

(defn transform-keys
  [m f]
  (let [transform-pair (fn [[k v]] [(f k) v])]
    ;; only apply to maps
    (postwalk (fn [x] (if (map? x) (into {} (map transform-pair x)) x)) m)))

(defn keywordize [x] (-> x (s/replace #"_" "-") keyword))

(defn json2clj [json]
  (-> json json/read-str (transform-keys keywordize)))

(defn vadim-metric [{:keys [rating user-ratings-total] :as place}]
  (* (- rating 4) user-ratings-total))

(defn make-query [endpoint params]
  (let [request {
          :url (str "https://maps.googleapis.com/maps/api/place/" endpoint "/json")
          :method :get
          :query-params (assoc params :key (env :google-api-key))}]
        }
        response-promise (h/request request)]
    (-> response-promise deref :body json2clj delay)))

(defn start-search [query]
  @(make-query "textsearch" {:query query :maxprice 2}))

(defn continue-search [query {:keys [:next-page-token] :as response}]
  (if next-page-token 
      @(make-query "textsearch" {:query query :pagetoken next-page-token})))

(defn get-places [query]
  (for [page (take-while some? (iterate (partial continue-search query) (start-search query)))
        result (:results page)]
    result))

(defn best-places [query]
  (->> (get-places query)
       (take 60)
       (sort-by vadim-metric >)
       (take 15)))