;;   Copyright (c) Metadata Partners, LLC. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns datomic.samples.mbrainz.query
  (:require [datomic.api :as d]
            [datomic.samples.query :as query]
            [datomic.samples.mbrainz.rules :refer (rules)]))

(defn schema
  "Given an mbrainz db, returns the domain-level schema."
  [db])

(defn qes
  "Returns a collection of entities, assuming the query returns a
  single entity."
  [query db & args]
  (map first
       (apply query/qes query db args)))

(defn artists-by-name
  "Lookup artists by name.  Returns a collection of all artists
  matching the given name, calling (touch) on each."
  [db name]
  (qes '[:find ?e
         :in $ ?name
         :where [?e :artist/name ?name]]
       db
       name))

(defn artist-tracks
  [db name]
  (qes '[:find ?e
         :in $ % ?name
         :where (artist-tracks ?name ?e)]
       db
       rules
       name))

(defn tracks-by-name
  [db name]
  (qes '[:find ?e
         :in $ ?name
         :where [?e :track/name ?name]]
       db
       name))

;; find track/artist by fulltext search on track

(defn track-search
  [db search]
  (qes '[:find ?track
         :in $ % ?search
         :where (track-search ?search ?track ?tname ?aname)]
       db
       rules
       search))

;; with query, fills a placeholder with a collection
(defn title-artists
  "Returns all artists who contributed to a song with the given title."
  [db title]
  (map first
       (d/q '[:find ?artists
              :in $ % ?title
              :where (title-artists ?title ?artists)]
            db
            rules
            title)))

;; rules uses predicate (see :short-track rule)

(defn artist-short-tracks
  [db name max-length]
  (qes '[:find ?t
         :in $ % ?aname ?max
         :where (artist-short-tracks ?aname ?t ?len ?max)]
       db
       rules
       name
       max-length))

;; who directly collaborated with one of these artists

(defn direct-collaborators
  [db artist-name]
  (map first
       (d/q '[:find ?aname2
              :in $ % ?aname
              :where (collab ?aname ?aname2)]
            db
            rules
            artist-name)))

;; transitive query
(defn collab-net
  ([db artist-name]
     (collab-net db artist-name 2))
  ([db artist-name depth]
     (map first
          (d/q [:find '?aname2
                :in '$ '% '?aname1
                :where (list (symbol (str "collab-net-" depth))
                             '?aname1
                             '?aname2)]
               db
               rules
               artist-name))))

(defn release
  [e]
  {:releaseName (:release/name e)
   :media
   (into []
         (for [medium (->> e
                           :release/media
                           (sort-by :medium/position))]
           (merge {:tracks (->> medium
                                :medium/tracks
                                (sort-by :track/position)
                                (mapv (juxt :track/position
                                            :track/name
                                            :track/duration)))}
                  (when-let [name (:medium/name medium)]
                    {:mediumName name}))))})

(defn discography
  [db artist-name]
  (map release
       (qes '[:find ?r
              :in $ ?aname
              :where [?a :artist/name ?aname]
              [?r :release/artists ?a]
              [?r :release/name]]
            db
            artist-name)))

(defn what-albums-am-I-missing?
  "Given a database, an artist-name, and a collection of album-names,
  returns a vector of albums by the given artists not found in the
  given album collection."
  [db artist-name album-names])

;;;;;;;;;;;;;;; database stats ;;;;;;;;;;;;;;;;;;

;; database stats (number of datoms, number of disk segments)
;; (->> (stats/aevt db)
;;      (map (fn [[attr stats]] (assoc stats :attr attr)))
;;      (sort-by :data-count >)
;;      (pp/print-table))
