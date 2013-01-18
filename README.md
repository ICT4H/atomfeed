atomfeed
========

build
-----
To build:
* mvn compile
* mvn test

To install (assuming a Postgres DB is already created):
* mvn install

design
------
Our design assumes:
* We have a database with autoincrementing ids for non-time-based feed pagination.
* We need to support a clustered environment, so our only synchronisation point is the databas.
