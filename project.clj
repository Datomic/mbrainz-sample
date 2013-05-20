(defproject com.datomic/mbrainz-sample "0.1.0"
  :description "Example queries and rules for working with the Datomic mbrainz example database."
  :url "http://datomic.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.datomic/datomic-free "0.8.3960"]

                 ;; Download Pro from http://downloads.datomic.com/pro.html
                 ;; unzip, then cd into distribution root
                 ;; Run `bin/maven-install` to install to local maven repository
                 ;; Then, comment out the free version above, and
                 ;; enable the pro version below
                 #_[com.datomic/datomic-pro "0.8.3960"]]
  :source-paths ["src/clj" "examples/clj"]
  :plugins      [[lein-marginalia "0.7.1"]]
  :jvm-opts ["-Xmx1g" "-server"])
