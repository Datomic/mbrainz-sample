(defproject com.datomic/mbrainz-sample "0.1.0"
  :description "Example queries and rules for working with the Datomic mbrainz example database."
  :url "http://datomic.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.datomic/datomic-pro "0.8.3885"
                  :exclusions [org.slf4j/slf4j-nop org.slf4j/log4j-over-slf4j]]
                 [org.slf4j/slf4j-log4j12 "1.6.4"]
                 [log4j/log4j "1.2.16"]]
  :jvm-opts ["-Xmx4g" "-server"])
