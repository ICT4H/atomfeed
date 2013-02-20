atomfeed
========

[![Build Status](https://travis-ci.org/ICT4H/atomfeed.png)](https://travis-ci.org/ICT4H/atomfeed)

build
-----
To build:
* mvn compile
* mvn test

To install:
* Create a Postgres DB and update atomfeed-server/maven.properties with credentials.
* mvn install -P IT -DskipTests

To start up:
* Create the DB tables by running install (see above).
* Update atomfeed-standalone/src/main/resources/atomfeed.properties with Postgres credentials.
* mkdir -p target/work 
* mvn jetty:run -P IT

To integration test:
* mvn integration-test -P IT

design
------
Our design assumes:
* We have a database with autoincrementing ids for non-time-based feed pagination.
* We need to support a clustered environment, so our only synchronisation point is the database.
