(ns analytij.auth
  (:require [clojure.java.io :as io])
  (:import [com.google.api.client.googleapis.auth.oauth2 GoogleCredential$Builder]
           [com.google.api.client.googleapis.javanet GoogleNetHttpTransport]
           [com.google.api.client.http HttpRequestInitializer]
           [com.google.api.client.json.jackson2 JacksonFactory]
           [com.google.api.services.analytics Analytics$Builder AnalyticsScopes]))

(defn- service-credentials
  [account-id private-key-file]
  (let [credential (doto (GoogleCredential$Builder. )
                     (.setTransport (GoogleNetHttpTransport/newTrustedTransport))
                     (.setJsonFactory (JacksonFactory. ))
                     (.setServiceAccountId account-id)
                     (.setServiceAccountScopes [AnalyticsScopes/ANALYTICS])
                     (.setServiceAccountPrivateKeyFromP12File (io/file private-key-file)))]
    (.build credential)))

(defn- request-initializer [credentials read-timeout-ms]
  (proxy [HttpRequestInitializer] []
    (initialize [http-request]
      (.initialize credentials http-request)
      (.setReadTimeout http-request read-timeout-ms))))

(defn service
  [account-id private-key-file & {:keys [read-timeout-ms]
                                  :or   {read-timeout-ms 60000}}]
  (let [creds (service-credentials account-id private-key-file)
        analytics (doto
                      (Analytics$Builder. (GoogleNetHttpTransport/newTrustedTransport)
                                          (JacksonFactory.)
                                          (request-initializer creds read-timeout-ms))
                    (.setApplicationName "Analytij"))]
    (.build analytics)))
