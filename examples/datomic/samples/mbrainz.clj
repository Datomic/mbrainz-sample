;;   Copyright (c) Metadata Partners, LLC. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns datomic.samples.mbrainz
  (:require [clojure.pprint :as pp :refer [pprint]]
            [datomic.api :as d]
            [datomic.samples.mbrainz.query :as q :refer :all]))

;; this file is intended for evaluation, form-by-form, at the REPL

;;;;;;;;;;;;;;; get a connection ;;;;;;;;;;;;;;;;;;

;; Replace with your transactor's connection information
(def uri "datomic:free://localhost:4334/mbrainz")

(def conn (d/connect uri))
(def db (d/db conn))

;;;;;;;;;;;;;;; REPL safety and convenience ;;;;;;;;;;;;;;;;;;

;; for when you accidentally ask for all tracks...
(set! *print-length* 250)

;;;;;;;;;;;;;;; schema queries ;;;;;;;;;;;;;;;;;;

;; TODO

;;;;;;;;;;;;;;; data queries ;;;;;;;;;;;;;;;;;;

(artists-by-name db "Prince")

(artist-tracks db "Foo Fighters")

;; Same query as above, with some post-processing on entities
(map (juxt (comp :release/name :release/_media :medium/_tracks) ;; Navigate "up"!
           :track/position
           :track/name
           :track/duration)
     (artist-tracks db "Foo Fighters"))

(tracks-by-name db "Zoom")

(track-search db "zombie")

(pprint
 (title-artists db "Sunshine"))
(pprint
 (title-artists db "Yesterday"))
(pprint
 (title-artists db "Zombie"))

(artist-short-tracks db "Foo Fighters" 100000)

(direct-collaborators db "Sting")
(direct-collaborators db "Frank Zappa")

(pprint
 (collab-net db "Paul McCartney"))

(pprint
 (discography db "The Strokes"))
