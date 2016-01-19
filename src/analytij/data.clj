(ns analytij.data
  (:require [schema.core :as s]
            [clojure.string :as st])
  (:import [java.text SimpleDateFormat]
           [com.google.api.services.analytics.model GaData]
           [java.math BigDecimal]))

(def Query {:start-date                   s/Inst
            :end-date                     s/Inst
            :metrics                      [s/Str]
            :view-id                      #"ga\:\d+"
            (s/optional-key :max-results) s/Num
            (s/optional-key :dimensions)  [#"ga:\w+"]
            (s/optional-key :filters)     s/Str})

(def date-format (SimpleDateFormat. "yyyy-MM-dd"))

(defn- date-str [dt]
  (.format date-format dt))

(defn coerce-val [t val]
  (condp = t
    "CURRENCY" (BigDecimal. val)
    "INTEGER" (Integer/valueOf val)
    "PERCENT" (Float/valueOf val)
    "TIME" (Float/valueOf val)
    "FLOAT" (Float/valueOf val)
    "STRING" val
    val))

(defn coerce-cell [{:strs [name columnType dataType]} val]
  {:name        name
   :column-type columnType
   :value       (coerce-val dataType val)})

(defn- parse-records [headers rows]
  (letfn [(parse-row [row]
            (let [header-and-values (->> row (interleave headers) (partition 2))]
              (->> header-and-values
                   (map (fn [[header val]]
                          (coerce-cell header val))))))]
    (map parse-row rows)))

(defn results->map [^GaData gadata]
  {:summary  {:total-results (.getTotalResults gadata)}
   :columns  (.getColumnHeaders gadata)
   :sampled? (.getContainsSampledData gadata)
   :records  (parse-records (.getColumnHeaders gadata) (.getRows gadata))})

(defn collect-records-handler
  "Default collector - appends records to results.
   Returns vector of [results new-records]"
  [results new-records]
  [(update-in results [:records] concat new-records) new-records])

(defn update-processed-record-count
  "Update the :processed count in results"
  [[results new-records]]
  (update-in results [:processed] + (count new-records)))

(defn execute
  "Fetches data. Query must have:
  - start date
  - end date
  - metrics
  - view id

  It may also have
  - dimensions
  - sort
  - filters
  - max results"
  [service {:keys [start-date end-date dimensions filters metrics view-id max-results] :as query} page-handler]
  {:pre [(s/validate Query query)]}
  (let [data (.. service data ga)]
    (letfn [(build-query [start-index]
              (let [q (.get data
                            view-id
                            (date-str start-date)
                            (date-str end-date)
                            (st/join "," metrics))]
                (.setMaxResults q (int (or max-results 10000)))
                (.setStartIndex q (int start-index))
                (when dimensions
                  (.setDimensions q (st/join "," dimensions)))
                (when filters
                  (.setFilters q filters))
                q))]
      (let [gadata        (.execute (build-query 1))
            headers       (.getColumnHeaders gadata)
            total-results (.getTotalResults gadata)
            results {:total-results total-results
                     :columns       headers
                     :sampled?      (.getContainsSampledData gadata)
                     :processed 0
                     :records []}
            counting-handler (comp update-processed-record-count page-handler)]

        (loop [res (counting-handler results (->> gadata (.getRows) (parse-records headers)))]
          (if (> total-results (:processed res))
            (recur (counting-handler res
                           (->> (.execute (build-query (inc (:processed res))))
                                (.getRows)
                                (parse-records headers)))) ;paginate
            res))))))
