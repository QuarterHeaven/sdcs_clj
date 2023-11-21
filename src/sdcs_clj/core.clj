(ns sdcs-clj.core
  (:require [sdcs-clj.api :as api]
            [aleph.http :as http]
            [slacker.server :as slserver]
            [slacker.client :as slclient])
  (:gen-class))

(def port-http '(9527
                 9528
                 9529))

(def port-rpc '(9530
                9531
                9532))

(defn start-server [which_server]
  (let [n (mod which_server 3)]
    (dosync (ref-set api/instance {:cache (api/create-cache)
                                   :server (http/start-server #(api/rpc-bypass %)
                                                              {:port (nth port-http n)})
                                   :slserver (slserver/start-slacker-server [(the-ns 'sdcs-clj.api)] (nth port-rpc n))
                                   :conn1 (slclient/slackerc (str "127.0.0.1:" (nth port-rpc (mod (inc n) 3))))
                                   :conn2 (slclient/slackerc (str "127.0.0.1:" (nth port-rpc (mod (+ n 2) 3))))
                                   :cnt n
                                   :cnt1 (mod (inc n) 3)
                                   :cnt2 (mod (+ n 2) 3)}))))

#_(defn start-server [which_server]
  (if (zero? (mod which_server 3))
         (dosync (ref-set api/instance {:cache (api/create-cache)
                                        :server (http/start-server #(api/rpc-bypass %)
                                                                   {:port (nth port-http 0)})
                                        :server1 (http/start-server #(api/rpc-bypass %)
                                                                    {:port (nth port-http 1)})
                                        :server2 (http/start-server #(api/rpc-bypass %)
                                                                    {:port (nth port-http 2)})}))))

(defn close-server []
  (do (.close (:server @api/instance))
      (slserver/stop-slacker-server (:slserver @api/instance))
      (dosync (ref-set api/instance nil))))

(defn -main
  ([] (println "usage: lein run [server_num]"))
  ([arg]
   (start-server (Integer/parseInt arg))
   (.addShutdownHook (Runtime/getRuntime)
                     (Thread. ^Runnable
                              (fn []
                                (println "About to shutting down server")
                                (close-server)
                                (println "Server stopped."))))
   (println "Server" arg "started.")))
