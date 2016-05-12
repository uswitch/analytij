(ns analytij.data
  (:require [schema.core :as s]
            [clojure.string :as st]
            [clj-time.format :as f]
            [clj-time.coerce :as c])
  (:import [com.google.api.services.analytics.model GaData UnsampledReport UnsampledReport$CloudStorageDownloadDetails]
           [java.math BigDecimal]))

(def Query {:start-date                   s/Inst
            :end-date                     s/Inst
            :metrics                      [s/Str]
            :view-id                      #"ga\:\d+"
            (s/optional-key :max-results) s/Num
            (s/optional-key :dimensions)  [#"ga:\w+"]
            (s/optional-key :filters)     s/Str})

(defn- date-str [dt]
  (->> dt (c/from-date) (f/unparse (f/formatter "yyyy-MM-dd"))))

(defn coerce-val [t val]
  (condp = t
    "CURRENCY" (BigDecimal. val)
    "INTEGER"  (Integer/valueOf val)
    "PERCENT"  (Float/valueOf val)
    "TIME"     (Float/valueOf val)
    "FLOAT"    (Float/valueOf val)
    "STRING"   val
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

(defn execute
  "Fetches data. Query must have:
  - start date
  - end date
  - metrics
  - view-id

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
                     :sampled?      (.getContainsSampledData gadata)}]

        (loop [records (->> gadata (.getRows) (parse-records headers))]
          (if (> total-results (count records))
            (recur (concat records
                           (->> (.execute (build-query (inc (count records))))
                                (.getRows)
                                (parse-records headers)))) ;paginate
            (assoc results :records records)))))))


(comment
   (.setCloudStorageDownloadDetails r download)
    (.setDownloadType r "CLOUD_STORAGE"))
<
(defn create-unsampled-report
  [service title {:keys [start-date end-date dimensions filters metrics account-id property-id view-id bucket object] :as query}]
  (let [r (UnsampledReport.)
        download (doto (UnsampledReport$CloudStorageDownloadDetails. )
                   (.setBucketId bucket)
                   (.setObjectId object))]
    (.setTitle     r title)
    (.setStartDate r (date-str start-date))
    (.setEndDate   r (date-str end-date))
    (when metrics
      (.setMetrics r (st/join "," metrics)))
    (when dimensions
      (.setDimensions r (st/join "," dimensions)))
    (when filters
      (.setFilters r filters))
    (let [insert-request (-> service
                             (.management)
                             (.unsampledReports)
                             (.insert account-id
                                      property-id
                                      view-id
                                      r))]
      (let [result (.execute insert-request)]
        {:created-at (.getCreated result)
         :status     (.getStatus result)
         :title      (.getTitle result)
         :id         (.getId result)}))))

(defn status-unsampled-report
  [service {:keys [account-id property-id view-id report-id]}]
  (let [x   (.. service management unsampledReports)
        req (.get x account-id property-id view-id report-id)]
    (let [report           (.execute req)
          download-details (.getCloudStorageDownloadDetails report)]
      {:title                    (.getTitle report)
       :status                   (.getStatus report)
       :type                     (.getDownloadType report)
       :id                       (.getId report)
       :download-details         {:object-id (.getObjectId download-details)
                                  :bucket-id (.getBucketId download-details)}})))
