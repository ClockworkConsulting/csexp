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

# Copyright and License

This code is provided under the [BSD 2-clause license](https://github.com/https://github.com/ClockworkConsulting/csexp/blob/master/LICENSE)
