(ns time-x.clock
  (:require [time-x.util :refer :all])
  (:import [java.time ZonedDateTime ZoneId]))

(defn new-clock-instance []
  (atom {}))

(defn new-clock []
  (ZonedDateTime/now))

(new-clock-instance)
(new-clock)
