;;   Copyright (c) Metadata Partners, LLC. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns datomic.samples.mbrainz
  (:require [clojure.pprint :refer (pprint)]
            [datomic.api :as d]
            [datomic.samples.mbrainz.rules :refer (rules)]))

;; this file is intended for evaluation, form-by-form, at the REPL

;;;;;;;;;;;;;;; get a connection ;;;;;;;;;;;;;;;;;;

;; Replace with your transactor's connection information
(def uri "datomic:free://localhost:4334/mbrainz"
  #_"datomic:sql://mbrainz?jdbc:postgresql://192.168.1.27:5432/datomic?user=datomic&password=datomic"
  #_"datomic:sql://mbrainz?jdbc:postgresql://localhost:5432/datomic?user=datomic&password=datomic")

(def conn (d/connect uri))
(def db (d/db conn))

;;;;;;;;;;;;;;; REPL safety and convenience ;;;;;;;;;;;;;;;;;;

;; for when you accidentally ask for all tracks...
(set! *print-length* 250)

;;;;;;;;;;;;;;; data queries ;;;;;;;;;;;;;;;;;;

(time
 (d/q '[:find ?title
        :in $ ?artist-name
        :where
        [?a :artist/name ?artist-name]
        [?t :track/artists ?a]
        [?t :track/name ?title]]
      db
      "John Lennon"))

(time
 (d/q '[:find ?title ?album ?year
        :in $ ?artist-name
        :where
        [?a :artist/name   ?artist-name]
        [?t :track/artists ?a]
        [?t :track/name    ?title]
        [?m :medium/tracks ?t]
        [?r :release/media ?m]
        [?r :release/name  ?album]
        [?r :release/year  ?year]]
      db
      "John Lennon"))

(time
 (d/q '[:find ?title ?album ?year
        :in $ ?artist-name
        :where
        [?a :artist/name   ?artist-name]
        [?t :track/artists ?a]
        [?t :track/name    ?title]
        [?m :medium/tracks ?t]
        [?r :release/media ?m]
        [?r :release/name  ?album]
        [?r :release/year  ?year]
        [(<= ?year 1980)]]
      db
      "John Lennon"))

(time
 (d/q '[:find ?title ?album ?year
        :in $ % ?artist-name
        :where
        [?a :artist/name   ?artist-name]
        [?t :track/artists ?a]
        [?t :track/name    ?title]
        (track-release ?t ?r)
        [?r :release/name  ?album]
        [?r :release/year  ?year]]
      db
      rules
      "John Lennon"))

(time
 (d/q '[:find ?title ?artist ?album ?year
        :in $ % ?search
        :where
        (track-search ?search ?track)
        (track-info ?track ?title ?artist ?album ?year)]
      db
      rules
      "zombie"))

(time
 (d/q '[:find ?artist ?rname ?type
        :in $ ?aname
        :where
        [?a :artist/name ?aname]
        [?ar :abstractRelease/artists ?a]
        [?ar :abstractRelease/name ?rname]
        [?ar :abstractRelease/artistCredit ?artist]
        [?ar :abstractRelease/type ?type-e]
        [?type-e :db/ident ?type]]
      db
      "The Beatles"))

(time
 (d/q '[:find ?aname ?aname2
        :in $ % [?aname ...]
        :where (collab ?aname ?aname2)]
      db rules ["John Lennon" "Paul McCartney" "George Harrison" "Ringo Starr"]))

(time
 (d/q '[:find ?aname ?aname2
        :in $ % ?aname
        :where (collab-net-2 ?aname ?aname2)]
      db
      rules
      "Paul McCartney"))

(def query '[:find ?aname2
             :in $ % [[?aname]]
             :where (collab ?aname ?aname2)])

(time
 (d/q query
      db
      rules
      (d/q query
           db
           rules
           [["Paul McCartney"]])))


(time
 (d/q '[:find ?aname (count ?e)
        :with ?a
        :in $ ?criterion [?aname ...]
        :where
        [?a :artist/name ?aname]
        [?e ?criterion ?a]]
      db
      :abstractRelease/artists
      ["Jay-Z" "Beyoncé Knowles"]))

(time
 (d/q '[:find ?aname ?tname
        :in $ ?artist-name
        :where
        [?a :artist/name ?artist-name]
        [?t :track/artists ?a]
        [?t :track/name ?tname]
        [(!= "Outro" ?tname)]
        [(!= "[outro]" ?tname)]
        [(!= "Intro" ?tname)]
        [(!= "[intro]" ?tname)]
        [?t2 :track/name ?tname]
        [?t2 :track/artists ?a2]
        [(!= ?a2 ?a)]
        [?a2 :artist/name ?aname]]
      db
      "The Who"))
