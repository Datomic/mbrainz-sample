(ns datomic.samples.schema
  (:require [clojure.string :as s]
            [datomic.api :as d]
            [hiccup.core :as h]))

(defn type-description
  [attr]
  (let [indexed?     (:db/index attr)
        fulltext?    (:db/fulltext attr)
        unique?      (= (:db/unique attr)
                        :db.unique/value)
        identifying? (= (:db/unique attr)
                        :db.unique/identity)
        component?   (:db/isComponent attr)
        adjectives   (cond-> []
                             unique? (conj "unique")
                             identifying? (conj "uniquely identifying")
                             indexed? (conj "indexed")
                             fulltext? (conj "fulltext")
                             component? (conj "component"))]
    (s/trim
     (s/join " "
             [(s/join ", "
                      adjectives)
              (-> attr :db/valueType name)]))))

(defn schema-table
  [db attr-names]
  (let [attributes (map (comp (partial d/entity db)
                              first)
                        (d/q '[:find ?e
                               :where
                               [?e :db/valueType]]
                             db))
        grouped    (group-by (comp namespace :db/ident) attributes)
        selected   (select-keys grouped attr-names)
        sorted     (->> selected
                        (sort-by (fn [[ent attrs]]
                                   ent))
                        (map (fn [[ent attrs]]
                               [ent (sort-by (fn [attr]
                                               (let [type (-> attr :db/valueType name)]
                                                 (if (= type "ref")
                                                   "z"
                                                   type)))
                                             attrs)])))]
    (for [[ent attrs] sorted]
      [:table
       [:caption ent]
       [:thead
        [:tr
         [:th "attribute"]
         [:th "type"]
         [:th "cardinality"]]]
       [:tfoot
        [:tr]]
       [:tbody
        (for [attr attrs]
          [:tr
           [:td (:db/ident attr)]
           [:td (type-description attr)]
           [:td (-> attr :db/cardinality name)]])]])))

(comment

  (def uri "datomic:sql://mbrainz?jdbc:postgresql://localhost:5432/datomic?user=datomic&password=datomic"
    #_"datomic:free://localhost:4334/mbrainz")

  (def conn (d/connect uri))

  (defn write-to-file
    [content filename]
    (spit filename content))

  (-> conn
      d/db
      (schema-table ["release"
                     "abstractRelease"
                     "label"
                     "artist"
                     "medium"
                     "track"
                     "country"
                     "language"
                     "script"])
      h/html
      (write-to-file "entity_tables.html"))

  )
