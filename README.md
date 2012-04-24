Hash
====

Hash is a Google App Engine (GAE) application that does a map reduce on the MD5 hashes. As calculating millions or billions of hashes is computationally expensive, this project aims to shard (or map) the problem-space across an infinite number of partitions to be executed on individual servers.

This application aims to achieve this by leveraging the Google App Engine platform using Java.