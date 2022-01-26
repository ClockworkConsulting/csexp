[![Build Status](https://travis-ci.org/ClockworkConsulting/csexp.svg?branch=master)](https://travis-ci.org/ClockworkConsulting/csexp)

# Intro

This library is a simple Scala implementation of [Canonical S-expressions](https://en.wikipedia.org/wiki/Canonical_S-expressions).

## Usage

The main entry points for using the library are the functions

```
    SExprParsers.parseFromInputStream
    SExprParsers.parseFromByteArray
    SExprParsers.writeToOutputStream
    SEpxrParsers.writeToByteArray
```

The AST is defined in the `csexp.AST` package.

# Development

## Publishing

```text
$ sbt +publishSigned
$ sbt sonatypeReleaseAll
```

# Copyright and License

This code is provided under the [BSD 2-clause license](https://github.com/ClockworkConsulting/csexp/blob/master/LICENSE)
