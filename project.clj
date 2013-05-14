(defproject com.datomic/mbrainz-sample "0.1.0"
  :description "Example queries and rules for working with the Datomic mbrainz example database."
  :url "http://datomic.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.datomic/datomic-pro "0.8.3941"]]
  :profiles {:dev {:source-paths ["examples"]
                   :plugins      [lein-marginalia "0.7.1"]}}
  :jvm-opts ["-Xmx4g" "-server"])
