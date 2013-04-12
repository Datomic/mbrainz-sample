# MusicBrainz on Datomic

[Datomic](http://datomic.com) is a database of flexible, time-based
facts, supporting queries and joins with elastic scalability, and ACID
transactions.

[MusicBrainz](http://musicbrainz.org) is an open music encyclopedia
that collects music metadata and makes it available to the public.

MusicBrainz data makes a great example database for learning,
evaluating, or testing Datomic.  Included in this project are:

* Instructions for downloading and restoring a Datomic backup to your local transactor
* Datomic Datalog rules to be composed together to create interesting queries
* Some sample queries as a starting point

## Getting Started

You need to do two things to use this sample: get the sample code and
get the Datomic backup containing the mbrainz data.

### Getting the Code

Clone the git repo somewhere convenient:

    git clone git@github.com:Datomic/mbrainz-sample.git
    cd mbrainz-sample

## Getting the Data

First download a [Datomic distribution](http://www.datomic.com/get-datomic.html), and unzip it somewhere convenient:

    wget http://downloads.datomic.com/0.8.3862/datomic-free-$VERSION.zip
    unzip datomic-free-$VERSION.zip

Then, start the transactor (using a large enough heap):

    cd datomic-free-$VERSION
    bin/transactor -Xmx2g config/samples/free-transactor-template.properties

Next, in a new shell, download the
[mbrainz backup](http://s3.amazonaws.com/mbrainz/20130328-backup.zip):

    wget http://s3.amazonaws.com/mbrainz/20130328-backup.zip
    unzip 20130328-backup.zip # unzips as `backup`, and takes a while

Finally, restore the backup:

    bin/datomic restore-db file:backup datomic:free://localhost:4334/mbrainz

Now you're ready to fire up a REPL and evaluate the forms in
src/datomic/samples/mbrainz.clj one at a time.

## License

Copyright © Metadata Partners, LLC. All rights reserved.

Distributed under the Eclipse Public License, the same as Clojure.
