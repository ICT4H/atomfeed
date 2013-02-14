atomfeed
========

[![Build Status](https://travis-ci.org/ICT4H/atomfeed.png)](https://travis-ci.org/ICT4H/atomfeed)

build
-----
To build:
* mvn compile
* mvn test

To install (assuming a Postgres DB is already created):
* mvn install -P IT -DskipTests

To start up:
* mkdir -p target/work 
* mvn jetty:run

design
------
Our design assumes:
* We have a database with autoincrementing ids for non-time-based feed pagination.
* We need to support a clustered environment, so our only synchronisation point is the database.
