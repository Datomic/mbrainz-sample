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
            [datomic.samples.mbrainz.rules :as rules :refer (rules)]))

;;;; Helper

(defn qes
  "Returns a collection of entities for the given query. Assumes that
  the query returns a set of tuples containing only a single entid."
  [query db & args]
  (map first
       (apply query/qes query db args)))

;;;; Schema

(defn schema
  "Given an mbrainz db, returns the domain-level schema."
  [db])

;;;; Entity lookup

(defn artists-by-name
  "Lookup artists by name.  Returns a collection of artist entities
  having the given name."
  [db name]
  (qes '[:find ?e
         :in $ ?name
         :where [?e :artist/name ?name]]
       db
       name))

(defn artist-tracks
  "Lookup tracks by artist name.  Returns a collection of track
  entities by the artist having the given name."
  [db name]
  (qes '[:find ?e
         :in $ % ?name
         :where (artist-tracks ?name ?e)]
       db
       rules
       name))

(defn tracks-by-name
  "Returns a collection of track entities having the given name."
  [db name]
  (qes '[:find ?e
         :in $ ?name
         :where [?e :track/name ?name]]
       db
       name))

(defn artist-short-tracks
  "Returns a collection of track entities by the artist with the given
  name which are shorter than the given max-length."
  [db name max-length]
  (qes '[:find ?t
         :in $ % ?aname ?max
         :where (artist-short-tracks ?aname ?t ?len ?max)]
       db
       rules
       name
       max-length))

;;;; Data lookup

(defn title-artists
  "Returns a collection of the names of all artists who contributed to
  a song with the given title."
  [db title]
  (map first
       (d/q '[:find ?artists
              :in $ % ?title
              :where (title-artists ?title ?artists)]
            db
            rules
            title)))

;;;; Fulltext

(defn track-search
  "Returns a collection of track entities whose names match the given
  full-text search string."
  [db search]
  (qes '[:find ?track
         :in $ % ?search
         :where (track-search ?search ?track)]
       db
       rules
       search))

;;;; Graph walking queries

(defn direct-collaborators
  "Returns a collection of the names of artists who collaborated
  directly with the given arist."
  [db artist-name]
  (map first
       (d/q '[:find ?aname2
              :in $ % ?aname
              :where (collab ?aname ?aname2)]
            db
            rules
            artist-name)))

(defn collab-net
  "Returns the full network of collaborators for the artist with the
  given name, down to the given depth (defaults to 2 if not given)."
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
               (rules/collab-net-rules depth)
               artist-name))))

;;;; Entity API, navigation, post-processing

(defn release
  "Formats the given release entity by navigating its attributes and
  relationships."
  [release-ent]
  {:releaseName (:release/name release-ent)
   :media
   (into []
         (for [medium (->> release-ent
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
  "Returns the discography of the artist having the given name. Formats
  each release using (release)."
  [db artist-name]
  (map release
       (qes '[:find ?r
              :in $ ?aname
              :where [?a :artist/name ?aname]
              [?r :release/artists ?a]
              [?r :release/name]]
            db
            artist-name)))

;;;; Aggregation

(defn artist-summary
  [db artist-name])

(defn longest-band-name
  [db])

;;;; Fun stuff

(defn what-albums-am-I-missing?
  "Given a database, an artist-name, and a collection of album-names,
  returns a vector of albums by the given artists not found in the
  given album collection."
  [db artist-name album-names])

(defn artist-v-artist
  [db artist-1-name artist-2-name])
