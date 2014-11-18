package datomic.samples;


import java.util.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;

import datomic.Connection;
import datomic.Database;
import static datomic.Util.*;
import static datomic.Peer.*;

public class Mbrainz {

  public static void main(String[] args) {
    String uri = "datomic:free://localhost:4334/mbrainz";
    Connection conn = connect(uri);
    InputStream rulesInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("rules.edn");

    Database db = conn.db();
    Object rules = readAll(new PushbackReader(new InputStreamReader(rulesInputStream))).get(0);

    System.out.println(rules);

    Collection<List<Object>> result;

    System.out.println("What are the titles of all the tracks John Lennon played on?");

    result = q(
        "[:find ?title\n" +
            " :in $ ?artist-name\n" +
            " :where\n" +
            " [?a :artist/name ?artist-name]\n" +
            " [?t :track/artists ?a]\n" +
            " [?t :track/name ?title]]\n",
        db, "John Lennon"
    );

    System.out.println(result);

    System.out.println("What are the titles, album names, and release years of John Lennon's tracks?");

    result = q(
        "[:find ?title ?album ?year\n" +
            " :in $ ?artist-name\n" +
            " :where\n" +
            " [?a :artist/name   ?artist-name]\n" +
            " [?t :track/artists ?a]\n" +
            " [?t :track/name    ?title]\n" +
            " [?m :medium/tracks ?t]\n" +
            " [?r :release/media ?m]\n" +
            " [?r :release/name  ?album]\n" +
            " [?r :release/year  ?year]]\n",
        db, "John Lennon"
    );

    System.out.println(result);

    System.out.println("What are the titles, album names, and release years of the John Lennon tracks released before or during 1980?");

    result = q(
        "[:find ?title ?album ?year\n" +
            " :in $ ?artist-name\n" +
            " :where\n" +
            " [?a :artist/name   ?artist-name]\n" +
            " [?t :track/artists ?a]\n" +
            " [?t :track/name    ?title]\n" +
            " [?m :medium/tracks ?t]\n" +
            " [?r :release/media ?m]\n" +
            " [?r :release/name  ?album]\n" +
            " [?r :release/year  ?year]\n" +
            " [(<= ?year 1980)]]\n",
        db, "John Lennon"
    );

    System.out.println(result);

    System.out.println("What are the titles, album names, and release years of John Lennon's tracks?");

    result = q(
        "[:find ?title ?album ?year\n" +
            " :in $ % ?artist-name\n" +
            " :where\n" +
            " [?a :artist/name   ?artist-name]\n" +
            " [?t :track/artists ?a]\n" +
            " [?t :track/name    ?title]\n" +
            " (track-release ?t ?r)\n" +
            " [?r :release/name  ?album]\n" +
            " [?r :release/year  ?year]]\n",
        db, rules, "John Lennon"
    );

    System.out.println(result);

    System.out.println("What is the full set of Beatles albums?");

    result = q(
        "[:find ?artist ?rname ?type\n" +
            " :in $ ?aname\n" +
            " :where\n" +
            " [?a :artist/name ?aname]\n" +
            " [?ar :abstractRelease/artists ?a]\n" +
            " [?ar :abstractRelease/name ?rname]\n" +
            " [?ar :abstractRelease/artistCredit ?artist]\n" +
            " [?ar :abstractRelease/type ?type-e]\n" +
            " [?type-e :db/ident ?type]]\n",
        db, "The Beatles"
    );

    System.out.println(result);

    System.out.println("What are the titles, artists, album names, and release years of all tracks having the word \"nevermore\" in their titles?");

    result = q(
        "[:find ?title ?artist ?album ?year\n" +
            " :in $ % ?search\n" +
            " :where\n" +
            " (track-search ?search ?track)\n" +
            " (track-info ?track ?title ?artist ?album ?year)]\n",
        db, rules, "nevermore"
    );

    System.out.println(result);

    System.out.println("Who collaborated with one of the Beatles?");

    result = q(
        "[:find ?aname ?aname2\n" +
            " :in $ % [?aname ...]\n" +
            " :where (collab ?aname ?aname2)]\n",
        db, rules, list("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr")
    );

    System.out.println(result);

    System.out.println("Who either directly collaborated any of the Beatles, or collaborated with one of their collaborators?");

    result = q(
        "[:find ?aname ?aname2\n" +
            " :in $ % [?aname ...]\n" +
            " :where (collab-net-2 ?aname ?aname2)]\n",
        db, rules, list("Paul McCartney", "John Lennon", "George Harrison", "Ringo Starr")
    );

    System.out.println(result);

    System.out.println("Who collaborated with any of the Beatles or any of their collaborators? (via recursion)");

    String query = "[:find ?aname2\n" +
                   " :in $ % [[?aname]]\n" +
                   " :where (collab ?aname ?aname2)]\n";

    result = q(
               query, db, rules,
                q(
                  query, db, rules, list(list("John Lennon", "Paul McCartney",
                                              "George Harrison", "Ringo Starr"))
                 )
              );

    System.out.println(result);


    System.out.println("Which artists have songs that might be covers of The Who (or vice versa)?");

    result = q(
        "[:find ?aname ?tname\n" +
            " :in $ ?artist-name\n" +
            " :where\n" +
            " [?a :artist/name ?artist-name]\n" +
            " [?t :track/artists ?a]\n" +
            " [?t :track/name ?tname]\n" +
            " [(!= \"Outro\" ?tname)]\n" +
            " [(!= \"[outro]\" ?tname)]\n" +
            " [(!= \"Intro\" ?tname)]\n" +
            " [(!= \"[intro]\" ?tname)]\n" +
            " [?t2 :track/name ?tname]\n" +
            " [?t2 :track/artists ?a2]\n" +
            " [(!= ?a2 ?a)]\n" +
            " [?a2 :artist/name ?aname]]\n",
        db, "The Who"
    );

    System.out.println(result);

  }

}
