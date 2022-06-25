(ns time-x.clock.constants
  (:import [java.time ZoneId ZonedDateTime]))

(def rec (ZoneId/of "America/Recife"))
(def fix-time (ZonedDateTime/of 2020 10 10 10 10 10 10 rec))
(def fix-inst (.toInstant fix-time))
