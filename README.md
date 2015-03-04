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
