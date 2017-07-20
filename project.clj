(defproject com.datomic/mbrainz-sample "0.1.0"
  :description "Example queries and rules for working with the Datomic mbrainz example database."
  :url "http://datomic.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha4"]
                 [com.datomic/datomic-free "0.9.5561.50"]

                 ;; To run on Datomic Pro, comment out the free
                 ;; version above, and enable the pro version below
                 #_[com.datomic/datomic-pro "0.9.5561.50"]]
  :source-paths ["src/clj" "examples/clj"]
  :jvm-opts ^:replace ["-Xmx2g" "-server"])
