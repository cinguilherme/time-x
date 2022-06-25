(ns time-x.clock
  (:require [time-x.util :refer :all]
            [schema.core :as s]
            [com.stuartsierra.component :as component])
  (:import [java.time ZonedDateTime ZoneId Clock Instant Duration]))

(def rec (ZoneId/of "America/Recife"))

(def fix-time (ZonedDateTime/of 2020 10 10 10 10 10 10 rec))

(def fix-inst (.toInstant fix-time))

(defn fixed-time []
  (Clock/fixed fix-inst rec))

(fixed-time)

(def running-clock (atom (Clock/offset (fixed-time) (Duration/ofSeconds 1))))

(future
  (loop [t 0]
    (Thread/sleep 1000)
    (swap! running-clock (fn [_] (Clock/offset @running-clock (Duration/ofSeconds 1))))
    (recur (inc t))))

(defn duration-to-millis
  [d]
  (.toMillis d))

(duration-to-millis (Duration/ofSeconds 1))

(duration-to-millis (Duration/ofMinutes 1))

(s/defschema ClockConf
  {:init-inst Instant
   :tick-time-ms s/Num
   :offset Duration})

(s/defschema ClockControl
  {:pause (s/atom s/Bool)
   :stop (s/atom s/Bool)})

(s/explain ClockConf)
(s/explain ClockControl)
(s/check ClockControl {:pause (atom false) :stop (atom false)})

(def slow-clock-conf
  {:init-inst fix-inst
   :tick-time-ms 1000
   :offset (Duration/ofMillis 100)})

(def fast-clock-conf
  {:init-inst fix-inst
   :tick-time-ms 10
   :offset (Duration/ofSeconds 30)})

(def normal-clock-conf
  {:init-inst fix-inst
   :tick-time-ms 1000
   :offset (Duration/ofMillis 1000)})

(defn create-ticking-clock-with-conf
  [{:keys [init-inst tick-time-ms offset]}]
  (let [fix (Clock/fixed init-inst rec)
        running-clock (atom (Clock/offset fix offset))]
    (do (future
          (loop [t 0]
            (Thread/sleep tick-time-ms)
            (swap! running-clock (fn [_] (Clock/offset @running-clock offset)))
            (recur (inc t))))
        running-clock)))

(defn create-ticking-clock-at
  ([]
   (create-ticking-clock-at fix-inst))
  ([inst]
   (create-ticking-clock-at inst (Duration/ofSeconds 1) 1000))
  ([inst tick-time tick-interval]
   (let [fix (Clock/fixed inst rec)
         running-clock (atom (Clock/offset (fixed-time) tick-time))
         pause (atom false)
         stop-button (atom false)]
     (do (future
           (loop [t 0]
             (if @stop-button
               nil                ;; effective shutdown the clock ticker
               (do
                 (when (false? @pause)
                 (Thread/sleep tick-interval
                 (swap! running-clock (fn [_] (Clock/offset @running-clock tick-time)))))
                 (recur (inc t))))))
         running-clock))))

(s/defrecord Clocker
    [maybe-conf :- ClockConf
     clock-control :- ClockControl]

  component/Lifecycle

  (start [this]
    (if (nil? maybe-conf)
      this
      (assoc this :clock (create-ticking-clock-with-conf maybe-conf))))
  (stop [this]
    this))


(comment

  (def another-running-clock (create-ticking-clock-at fix-inst))

  (def a-fast-clock (create-ticking-clock-at fix-inst (Duration/ofSeconds 30) 50))
  (def a-slow-clock (create-ticking-clock-at fix-inst (Duration/ofMillis 200) 1000))
  (def another-fast-clock (create-ticking-clock-with-conf fast-clock-conf))

  (println (.instant @a-slow-clock))
  (println (.instant @a-fast-clock))
  (println (.instant @another-running-clock))
  (println (.instant @running-clock)))
