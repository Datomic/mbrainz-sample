;;   Copyright (c) Metadata Partners, LLC. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns mbrainz.rules)

(defn net-rules
  [depth]
  (let [net-depth (fn [i]
                    (symbol (str "net-depth-" i)))]
    (apply concat
           '[[(net-depth-1 ?root ?attr ?nodes)
              [?root ?attr ?nodes]]]
           (for [i (range 2 (inc depth))]
             [[(list (net-depth i) '?attr '?root '?nodes)
               (list 'net-depth-1  '?attr '?root '?nodes)]
              [(list (net-depth i) '?root '?attr '?nodes)
               (list (net-depth 1) '?root '?attr '?x)
               (list (net-depth (dec i)) '?x '?attr '?nodes)]]))))

(defn sibling-net-rules
  [n]
  (let [sib-sym (fn [i]
                  (symbol (str "sibling-net-" i)))]
    (apply concat
           '[[(sibling-net-1 ?attr ?a1 ?a2)
              [?x ?attr ?a1]
              [?x ?attr ?a2]
              [(!= ?a1 ?a2)]]]
           (for [i (range 2 (inc n))]
             [[(list (sib-sym i) '?attr '?a1 '?a2)
               (list 'sibling-net-1 '?attr '?a1 '?a2)]
              [(list (sib-sym i) '?attr '?a1 '?a2)
               (list (sib-sym (dec i)) '?attr '?a1 '?x)
               (list (sib-sym (dec i)) '?attr '?x '?a2)
               '[(!= ?a1 ?a2)]]]))))

(defn collab-net-rules
  [depth]
  (let [collab-sym (fn [i]
                     (symbol (str "collab-net-" i)))
        sib-sym    (fn [i]
                     (symbol (str "sibling-net-" i)))]
    (concat
     '[[(collab ?aname1 ?aname2)
        [?a1 :artist/name ?aname1]
        (sibling-net-1 :artists ?a1 ?a2)
        [?a2 :artist/name ?aname2]]
       [(collab-net-1 ?aname1 ?aname2)
        (collab ?aname1 ?aname2)]]
     (for [i (range 2 (inc depth))]
       [(list (collab-sym i) '?aname1 '?aname2)
        ['?a1 :artist/name '?aname1]
        (list (sib-sym i) :artists '?a1 '?a2)
        ['?a2 :artist/name '?aname2]]))))

(def rules
  (concat '[[(artist-tracks ?aname ?t)
             [?a :artist/name ?aname]
             [?t :artists ?a]]

            [(track-artists ?t ?aname)
             [?t :artists ?a]
             [?a :artist/name ?aname]]

            [(title-artists ?title ?aname)
             [?t :track/name ?title]
             [?t :artists ?a]
             [?a :artist/name ?aname]]

            [(short-track ?a ?t ?len ?max)
             [?t :artists ?a]
             [?t :track/duration ?len]
             [(< ?len ?max)]]

            [(artist-short-tracks ?aname ?t ?len ?max)
             [?a :artist/name ?aname]
             (short-track ?a ?t ?len ?max)]

            [(track-search ?q ?track ?tname ?aname)
             [(fulltext $ :track/name ?q) [[?track ?tname]]]
             (track-artists ?track ?aname)]]
          (sibling-net-rules 10)
          (collab-net-rules 10)
          (net-rules 10)))
