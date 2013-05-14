# Datomic MusicBrainz sample database

[Datomic](http://datomic.com) is a database of flexible, time-based
facts, supporting queries and joins with elastic scalability, and ACID
transactions.

[MusicBrainz](http://musicbrainz.org) is an open music encyclopedia
that collects music metadata and makes it available to the public.
This sample project uses the MusicBrainz dataset, but is in no way
affiliated with or sponsored by MusicBrainz.

The MusicBrainz dataset makes a great example database for learning,
evaluating, or testing Datomic.  To create this sample database, we have
exported the MusicBrainz distribution database as EDN data files,
imported that data into Datomic according to the Schema described
below, and [backed up](http://docs.datomic.com/backup.html) that
database.

Included in this project are:

* Instructions for downloading and restoring the Datomic backup to your local transactor
* Datomic Datalog rules to be composed together to create interesting queries
* Some sample queries as a starting point

## Getting Started

### Getting Datomic

First download a
[Datomic distribution](http://www.datomic.com/get-datomic.html) and
unzip it somewhere convenient:

    wget http://downloads.datomic.com/$VERSION/datomic-free-$VERSION.zip
    unzip datomic-free-$VERSION.zip

For this walkthrough, we'll use
[Datomic Free](http://downloads.datomic.com/free.html) and local
storage, but you could use
[Datomic Pro](http://downloads.datomic.com/pro.html) with any of the
available [storage options](http://docs.datomic.com/storage.html).

Then, start the transactor (using a reasonably large heap):

    cd datomic-free-$VERSION
    bin/transactor -Xmx2g config/samples/free-transactor-template.properties

### Getting the Data

Next, in a new shell, download the
[mbrainz backup](http://s3.amazonaws.com/mbrainz/20130510-backup.zip):

    # 2.6 GB
    wget http://s3.amazonaws.com/mbrainz/20130510-backup.zip

and unzip:

    # this takes a while
    unzip 20130510-backup.zip

Finally, [restore the backup](http://docs.datomic.com/backup.html):

    # takes a while, but prints progress
    bin/datomic restore-db file:20130510-backup datomic:free://localhost:4334/mbrainz

### Getting the Code

Clone this git repo somewhere convenient:

    git clone git@github.com:Datomic/mbrainz-sample.git
    cd mbrainz-sample

### Running the examples

Now you're ready to fire up a REPL in this and evaluate the forms in
`examples/datomic/samples/mbrainz.clj` one at a time:

    # using Leiningen 2
    lein repl
    => ;; run the examples

The Datomic Peer library is included as a dependency in `project.clj`,
and so will be on the classpath automatically.  See
`examples/datomic/samples/mbrainz.clj` for example usage.

## Schema

This schema is an adaptation of a subset of the full
[MusicBrainz schema](http://musicbrainz.org/doc/MusicBrainz_Database/Schema).
We didn't include some entities, and we made some simplifying
assumptions and combined some entities.  In particular:

* We omit any notion of [Work](http://musicbrainz.org/doc/Work)
* We combine Track, Tracklist and [Recording](http://musicbrainz.org/doc/Recording) into simply "track"
* We de-normalize to use :db.cardinality/many where appropriate
* We renamed [Release group](http://musicbrainz.org/doc/Release_Group) to "abstractRelease"

### Abstract Release vs. Release vs. Medium

(Adapted from the MusicBrainz [schema docs](http://musicbrainz.org/doc/MusicBrainz_Database/Schema))

An "abstractRelease" is an abstract "album" entity (e.g. "The Wall" by
Pink Floyd).  A "release" is something you can buy in your music store
(e.g. the 1984 US vinyl release of "The Wall" by Columbia, as opposed
to the 2000 US CD release by Capitol Records).

Therefore, when you query for releases e.g. by name, you may see
duplicate releases.  To find just the "work of art" level album
entity, query for abstractRelease.

The media are the physical components comprising a release (disks,
CDs, tapes, cartriges, piano rolls).  One medium will have several
tracks, and the total tracks across all media represent the track list
of the release.

### Diagram

![mbrainz schema](schema.png)

## Queries and Rules



## Thanks

We would like to thank the MusicBrainz project for defining and
compiling a great dataset, and for making it freely available.

## License

Copyright © Metadata Partners, LLC. All rights reserved.

Distributed under the Eclipse Public License, the same as Clojure.
