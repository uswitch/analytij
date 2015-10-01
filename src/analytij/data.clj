(ns analytij.data
  (:require [schema.core :as s]
            [clojure.string :as st])
  (:import [java.text SimpleDateFormat]
           [com.google.api.services.analytics.model GaData]))

(def Query {:start-date                   s/Inst
            :end-date                     s/Inst
            :metrics                      [s/Str]
            :view-id                      #"ga\:\d+"
            (s/optional-key :max-results) s/Num})

(def date-format (SimpleDateFormat. "yyyy-MM-dd"))

(defn- date-str [dt]
  (.format date-format dt))

(defn coerce-val [t val]
  (condp = t
    "INTEGER" (Integer/valueOf val)))

(defn coerce-cell [{:strs [name dataType]} val]
  [name (coerce-val dataType val)])

(defn- parse-records [^GaData gadata]
  (let [headers      (.getColumnHeaders gadata)]
    (map (fn [row] ;; row is a list of strings, dimensions and then metrics)
           (let [header-and-values (->> row (interleave headers) (partition 2))]
             (->> header-and-values
                  (map (fn [[header val]]
                         (coerce-cell header val)))
                  (into {}))))
         (.getRows gadata))))

(defn results->map [^GaData gadata]
  {:summary  {:total-results (.getTotalResults gadata)}
   :columns  (.getColumnHeaders gadata)
   :sampled? (.getContainsSampledData gadata)
   :records  (parse-records gadata)})

(defn execute
  "Fetches data. Query must have:
  - start date
  - end date
  - metrics

  It may also have
  - dimensions
  - sort
  - filters
  - max results"
  [service {:keys [start-date end-date metrics view-id] :as query}]
  {:pre [(s/validate Query query)]}
  (let [data (.. service data ga)
        q    (.get data
                   view-id
                   (date-str start-date)
                   (date-str end-date)
                   (st/join "," metrics))]
    (results->map (.execute q))))
