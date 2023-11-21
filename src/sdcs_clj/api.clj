(ns sdcs-clj.api
  (:require [clojure.data.json :as json]
            [clojure.core.cache :as cache]
            [slacker.server :as slserver]
            [slacker.client :as slclient]))

(def instance (ref nil))

(defn create-cache []
  (atom (cache/lru-cache-factory {} :threshold 1000)))

(defn get-from-local-cache [key]
  (get @(:cache @instance) key))

(defn set-local-cache [key value]
  (swap! (:cache @instance) assoc key value))

(defn delete-from-local-cache [key]
  (swap! (:cache @instance) cache/evict key))

(defn handle-post [k v]
  (set-local-cache k v)
  {:status 200
   :headers {"content-type" "application/json"}
   :body (json/write-str {k v})})

(defn handle-get [request]
  (let [uri (.substring (:uri request) 1)]
    (if (get-from-local-cache uri)
      {:status 200
       :headers {"content-type" "application/json"}
       :body (json/write-str {uri (get-from-local-cache uri)})}
      {:status 404
       :body nil})
    ))

(defn handle-delete [request]
  (let [uri (.substring (:uri request) 1)]
    (if (get-from-local-cache uri)
      (do
        (delete-from-local-cache uri)
        {:status 200
         :headers {"content-type" "application/json"}
         :body (json/write-str 1)})
      {:status 200
       :headers {"content-type" "application/json"}
       :body (json/write-str 0)})))

(defn handle-request [request]
  (let [uri (.substring (:uri request) 1)]
    (case (:request-method request)
      :get (handle-get request)
      :delete (handle-delete request)
      :default {:status 405
                :headers {"Content-Type" "text/plain"}
                :body "Method Not Allowed"})))

(defn handle-request-test [request]
  {:status 200
   :headers {"content-type" "text/plain"}
   :body (str (:cnt @instance))})

(defn rpc-bypass [request]
  (case (:request-method request)
    :post
    (let [[k v] (reduce into [] (json/read-str (slurp (:body request))))]
      (condp = (mod (int (.charAt k 4)) 3)
        (:cnt @instance) (handle-post k v)
        (:cnt1 @instance) (slclient/call-remote (:conn1 @instance) "sdcs-clj.api" "handle-post" [k v])
        (:cnt2 @instance) (slclient/call-remote (:conn2 @instance) "sdcs-clj.api" "handle-post" [k v]))
      #_(handle-post k v))
    (let [uri (.substring (:uri request) 1)]
      (condp = (mod (int (.charAt uri 4)) 3)
        (:cnt @instance) (handle-request request)
        (:cnt1 @instance) (slclient/call-remote (:conn1 @instance) "sdcs-clj.api" "handle-request" [request])
        (:cnt2 @instance) (slclient/call-remote (:conn2 @instance) "sdcs-clj.api" "handle-request" [request]))
      #_(handle-request request))))
