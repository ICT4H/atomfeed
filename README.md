Atomfeed
========

[![Build Status](https://travis-ci.org/ICT4H/atomfeed.png)](https://travis-ci.org/ICT4H/atomfeed)

AtomFeed is an implementation of the ATOM protocol in Java. It consists of modules such as

* atomfeed-server - An ATOM server implementaion that broadcasts events
* atomfeed-client - An ATOM client implementation that consumes events.
* atomfeed-spring-server - A spring wrapper on atomfeed-server
* atomfeed-standalone - A reference web application that demonstrates how events can be created and consumed.

Design
------

AtomFeed is designed to work in a clustered evnironment. Our only point of synchronisation is the database

We allow for two different pagination strategies. Time and Number based pagination. 

* Number based pagination works by creating chunks based on sequence ids.
* Time based pagination works by creating chunks based on timestamps of created events.

By allowing support to these two pagination mechanisms, AtomFeed works with databases that support auto incrementing Ids and those that don't.

<p>
We have support to change the pagination chunk size after the module has been installed and feeds have been generated.
As expected, this change will only affect the feeds created after the change in page size has been effected.

Please take a look at the documentation to see how this can be achieved.
</p>

Build
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
* cd atomfeed-standalone
* mvn jetty:run -P IT

To integration test:
* mvn integration-test -P IT

Documentation
------
Please see the [wiki](https://github.com/ICT4H/atomfeed/wiki) for documentation on using Atomfeed.

## License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

