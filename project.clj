(defproject com.datomic/mbrainz-sample "0.1.0"
  :description "Example queries and rules for working with the Datomic mbrainz example database."
  :url "http://datomic.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [com.datomic/datomic-pro "1.0.6610"]]
  :source-paths ["src/clj" "examples/clj"]
  :jvm-opts ^:replace ["-Xmx2g" "-server"])
