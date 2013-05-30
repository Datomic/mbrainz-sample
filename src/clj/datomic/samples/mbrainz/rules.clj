;;   Copyright (c) Metadata Partners, LLC. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns datomic.samples.mbrainz.rules)

(defn transitive-net-rules
  "Returns a set of rules for finding transitive entities of a given
  entity for a given :db.cardinality/many attribute up to the given
  depth.

  For example, calling this function with a depth of 10 would return a
  rule set against which you could query sibling-hood anywhere from
  direct siblings to 10 levels of \"siblings-of-siblings\".

  This is an example of generating graph-walking rulesets."
  [depth]
  (let [sib-sym (fn [i]
                  (symbol (str "transitive-net-" i)))]
    (apply concat
           '[[(transitive-net-1 ?attr ?a1 ?a2)
              [?x ?attr ?a1]
              [?x ?attr ?a2]
              [(!= ?a1 ?a2)]]]
           (for [i (range 2 (inc depth))]
             [[(list (sib-sym i) '?attr '?a1 '?a2)
               (list 'transitive-net-1 '?attr '?a1 '?a2)]
              [(list (sib-sym i) '?attr '?a1 '?a2)
               (list (sib-sym (dec i)) '?attr '?a1 '?x)
               (list (sib-sym (dec i)) '?attr '?x '?a2)
               '[(!= ?a1 ?a2)]]]))))

(defn collab-net-rules
  "Returns a set of rules for querying an artist's collaboration
  network up to the given depth using (transitive-net-rules).

  For example, calling this function with a
  depth of 10 would return a rule set against which you could query
  an artist's collaboration network from direct collaborators to 10 levels of
  \"collaborators-of-collaborators\"."
  [depth]
  (let [collab-sym (fn [i]
                     (symbol (str "collab-net-" i)))
        sib-sym    (fn [i]
                     (symbol (str "transitive-net-" i)))]
    (concat
     (transitive-net-rules depth)
     '[[(collab ?artist-name-1 ?artist-name-2)
        [?a1 :artist/name ?artist-name-1]
        (transitive-net-1 :track/artists ?a1 ?a2)
        [?a2 :artist/name ?artist-name-2]]
       [(collab-net-1 ?artist-name-1 ?artist-name-2)
        (collab ?artist-name-1 ?artist-name-2)]]
     (for [i (range 2 (inc depth))]
       [(list (collab-sym i) '?artist-name-1 '?artist-name-2)
        ['?a1 :artist/name '?artist-name-1]
        (list (sib-sym i) :track/artists '?a1 '?a2)
        ['?a2 :artist/name '?artist-name-2]]))))

(def ^{:doc "Rules for looking up entities by single attributes, predicate
            matches, and full-text search."}
  simple-rules
  '[[(track-release ?t ?r)
     [?m :medium/tracks ?t]
     [?r :release/media ?m]]

    [(track-info ?t ?track-name ?artist-name ?album ?year)
     [?t :track/name    ?track-name]
     [?t :track/artists ?a]
     [?a :artist/name   ?artist-name]
     (track-release ?t ?r)
     [?r :release/name  ?album]
     [?r :release/year  ?year]]

    [(short-track ?a ?t ?len ?max)
     [?t :track/artists ?a]
     [?t :track/duration ?len]
     [(< ?len ?max)]]

    [(track-search ?q ?track)
     [(fulltext $ :track/name ?q) [[?track ?tname]]]]])

(def ^{:doc "A convenient collection of rules from this namespace.  Each
            graph-walking ruleset is taken to depth 10."}
  rules
  (concat simple-rules
          (collab-net-rules 10)))
