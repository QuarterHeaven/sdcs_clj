(defproject sdcs_clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [aleph "0.7.0-alpha2"]
                 [org.clojure/core.cache "1.0.225"]
                 [org.clojure/data.json "2.4.0"]
                 [slacker "0.17.0"]]
  :main ^:skip-aot sdcs-clj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
