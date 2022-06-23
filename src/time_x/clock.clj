(ns time-x.clock
  (:require [time-x.util :refer :all])
  (:import [java.time ZonedDateTime ZoneId Clock Instant Duration]))

(def rec (ZoneId/of "America/Recife"))

(def fix-inst (ZonedDateTime/of 2020 10 10 10 10 10 10 rec))

(defn fixed-time []
  (Clock/fixed (.toInstant fix-inst) rec))

(fixed-time)

(def running-clock (atom (Clock/offset (fixed-time) (Duration/ofSeconds 1))))

(future
  (loop [t 0]
    (Thread/sleep 1000)
    (swap! running-clock (fn [_] (Clock/offset @running-clock (Duration/ofSeconds 1))))
    (recur (inc t))))

(defn create-ticking-clock-at [inst]
  (let [fix (Clock/fixed (.toInstant fix-inst) rec)
        running-clock (atom (Clock/offset (fixed-time) (Duration/ofSeconds 1)))]
    (do (future
          (loop [t 0]
            (Thread/sleep 1000)
            (swap! running-clock (fn [_] (Clock/offset @running-clock (Duration/ofSeconds 1))))
            (recur (inc t))))
        running-clock)))

(def another-running-clock (create-ticking-clock-at fix-inst))

(println (.instant @another-running-clock))

(println (.instant @running-clock))

(defrecord Clocker [])

(defn new-clock-instance []
  (atom {}))

(defn new-clock []
  (ZonedDateTime/now))

(new-clock-instance)
(new-clock)
