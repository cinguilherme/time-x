(ns time-x.clock.schema
  (:require [schema.core :as s])
  (:import
   [java.time
    Clock
    Duration
    Instant
    ZoneId
    ZonedDateTime]))

(s/defschema ClockConf
  {:init-inst    Instant
   :tick-time-ms s/Num
   :offset       Duration})

(s/defschema ClockControl
  {:pause (s/atom s/Bool)
   :stop  (s/atom s/Bool)})

(s/explain ClockConf)
(s/explain ClockControl)
(s/check ClockControl {:pause (atom false) :stop (atom false)})
