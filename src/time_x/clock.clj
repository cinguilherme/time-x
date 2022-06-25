(ns time-x.clock
  (:require
   [clojure.pprint :refer :all]
   [com.stuartsierra.component :as component]
   [schema.core :as s]
   [time-x.util :refer :all])
  (:import
   [java.time
    Clock
    Duration
    Instant
    ZoneId
    ZonedDateTime]))

(def rec (ZoneId/of "America/Recife"))
(def fix-time (ZonedDateTime/of 2020 10 10 10 10 10 10 rec))
(def fix-inst (.toInstant fix-time))

(defn fixed-time []
  (Clock/fixed fix-inst rec))

(defn duration-to-millis
  [d]
  (.toMillis d))

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

(def slow-clock-conf
  {:init-inst    fix-inst
   :tick-time-ms 1000
   :offset       (Duration/ofMillis 100)})

(def fast-clock-conf
  {:init-inst    fix-inst
   :tick-time-ms 10
   :offset       (Duration/ofSeconds 30)})

(def normal-clock-conf
  {:init-inst    fix-inst
   :tick-time-ms 1000
   :offset       (Duration/ofMillis 1000)})

(defn create-ticking-clock-with-conf
  [{:keys [init-inst tick-time-ms offset]}
   {:keys [pause stop]}]
  (let [fix (Clock/fixed init-inst rec)
        running-clock (atom (Clock/offset fix offset))]
    (future
      (loop [t 0]
        (if (true? @stop)
          nil
          (do (Thread/sleep tick-time-ms)
              (when (false? @pause)
                (swap! running-clock (fn [_] (Clock/offset @running-clock offset))))
              (recur (inc t))))))
    running-clock))

(defn create-ticking-clock-at
  ([]
   (create-ticking-clock-at fix-inst))
  ([inst]
   (create-ticking-clock-at inst (Duration/ofSeconds 1) 1000))
  ([inst tick-time tick-interval]
   (let [fix (Clock/fixed inst rec)
         running-clock (atom (Clock/offset fix tick-time))
         pause (atom false)
         stop-button (atom false)]
     (do (future
           (loop [t 0]
             (if @stop-button
               nil                                          ;; effective shutdown the clock ticker
               (do
                 (when (false? @pause)
                   (Thread/sleep tick-interval)
                   (swap! running-clock (fn [_] (Clock/offset @running-clock tick-time))))
                 (recur (inc t))))))
         running-clock))))

(s/defrecord Clocker
             [maybe-conf :- ClockConf
              clock-control :- ClockControl]

  component/Lifecycle

  (start [this]
    (if (or (nil? maybe-conf) (nil? clock-control))
      this
      (assoc this :clock (create-ticking-clock-with-conf maybe-conf clock-control))))
  (stop [this]
    this))

(defn time-now
  [clock]
  (let [clock @(:clock clock)]
    (.instant clock)))

(defn pause
  "pauses the clock, making it not offset as long as is paused"
  [clock]
  (let [control (:clock-control clock)]
    (swap! (:pause control) (fn [_] true))))

(defn stop
  "breaks out of loop of tick, only to use if clock is no longer needed"
  [clock]
  (let [control (:clock-control clock)]
    (swap! (:stop control) (fn [_] true))))

(defn start
  [{:keys [clock maybe-conf clock-control]}]
  (let [{:keys [stop pause]} clock-control
        {:keys [init-inst tick-time-ms offset]} maybe-conf
        fix (Clock/fixed init-inst rec)]
    (when clock
      (do
        (swap! (:pause clock-control) (fn [_] false))
        (swap! (:stop clock-control) (fn [_] false))
        (swap! clock (fn [_] (Clock/offset fix offset)))
        (future
          (loop [t 0]
            (if (true? @stop)
              nil
              (do
                (Thread/sleep tick-time-ms)
                (when (false? @pause)
                  (swap! clock (fn [_] (Clock/offset @clock offset))))
                (recur (inc t))))))))))

(defn play
  "unpauses the clock"
  [clock]
  (let [control (:clock-control clock)]
    (swap! (:pause control) (fn [_] false))))

(defn new-clock [conf state]
  (->Clocker conf state))

(comment

  (def clock-comp (new-clock fast-clock-conf {:pause (atom false) :stop (atom false)}))
  (def started (component/start clock-comp))
  (pause started)
  (stop started)
  (start started)
  (play started)
  (pprint started)
  (pprint (time-now started))

  (def another-running-clock (create-ticking-clock-at fix-inst))

  (def a-fast-clock (create-ticking-clock-at fix-inst (Duration/ofSeconds 30) 50))
  (def a-slow-clock (create-ticking-clock-at fix-inst (Duration/ofMillis 200) 1000))

  (println (.instant @a-slow-clock))
  (println (.instant @a-fast-clock))
  (println (.instant @another-running-clock)))
