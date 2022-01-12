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
(def uri "datomic:dev://localhost:4334/mbrainz-1968-1973")

(def conn (d/connect uri))
(def db (d/db conn))

;;;;;;;;;;;;;;; REPL safety and convenience ;;;;;;;;;;;;;;;;;;

;; for when you accidentally ask for all tracks...
(set! *print-length* 250)

;;;;;;;;;;;;;;; data queries ;;;;;;;;;;;;;;;;;;

(d/q '[:find ?title
       :in $ ?artist-name
       :where
       [?a :artist/name ?artist-name]
       [?t :track/artists ?a]
       [?t :track/name ?title]]
     db
     "John Lennon")

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
     "John Lennon")

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
       [(< ?year 1970)]]
     db
     "John Lennon")

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
     "John Lennon")

(d/q '[:find ?title ?artist ?album ?year
       :in $ % ?search
       :where
       (track-search ?search ?track)
       (track-info ?track ?title ?artist ?album ?year)]
     db
     rules
     "always")

(d/q '[:find ?aname ?aname2
       :in $ % [?aname ...]
       :where (collab ?aname ?aname2)]
     db rules ["John Lennon" "Paul McCartney" "George Harrison" "Ringo Starr"])

(d/q '[:find ?aname ?aname2
       :in $ % [?aname ...]
       :where (collab-net-2 ?aname ?aname2)]
     db
     rules
     ["George Harrison"])

(def query '[:find ?aname2
             :in $ % [[?aname]]
             :where (collab ?aname ?aname2)])

(d/q query
     db
     rules
     (d/q query
          db
          rules
          [["Diana Ross"]]))


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
     "Bill Withers")
