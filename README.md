# time-x
[![Build Status](https://travis-ci.org/cinguilherme/time-x.svg?branch=master)](https://travis-ci.org/cinguilherme/time-x)
[![codecov](https://codecov.io/gh/cinguilherme/time-x/branch/master/graph/badge.svg)](https://codecov.io/gh/cinguilherme/time-x)
[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.cinguilherme/time-x.svg)](https://clojars.org/org.clojars.cinguilherme/time-x)

A Clojure library designed to allow easily the use of the more powerful Date DateTime classes from Java in a clojure ecosystem.

Mainly by changing the default date type from java.util.Date to the more robust java.time Classes and providing functions to work with this types in a immutable and functional manner.

```clj
[org.clojars.cinguilherme/time-x "0.0.0"]
```

## Usage

(:require [time-x/core :as x-time])

(x-time/now) => 
