# shocon
Pure-Scala implementation of [HOCON](https://github.com/typesafehub/config/blob/master/HOCON.md), suitable for cross-platform use

## Usage

Include the following in your sbt file:
```
"org.querki" %%% "shocon" % "0.2"
```
Note that this has some fairly old dependencies at the moment, mainly because Querki is a bit behind the times. Those will be updated in due course. (The lack of unit tests is a consequence of this -- once it is updated enough to use ScalaTest.JS, we can do that properly.)

## What It Is

Suffice it to say, this is a **very** preliminary, **very** partial, reimplementation of the [HOCON file format](https://github.com/typesafehub/config/blob/master/HOCON.md), in pure Scala. Its purpose is mainly to enable a Scala.js client and a Scala server to share the same data file.

As it says, as of this writing this is only a beginning. This library doesn't handle all of the syntax, it's way too forgiving of some syntax errors, and doesn't yet deal with some common constructs. But it does cope with the most common format reasonably well. Pull requests are welcomed for fleshing out the details and getting them right.

This is not yet attempting to be a full-fledged config system: this library doesn't yet have any semantics for what to *do* with this file, it just produces a simple tree of the contents. We may eventually want to make this richer, but that will require a lot of discussion about, eg, what it means to "include" another file when we're interpreting this client-side. I suspect that the parser will need to be embedded in some richer factories, with JS and JVM implementations.

Under the hood, this is based on FastParse, so it is expected to run reasonably quickly.
