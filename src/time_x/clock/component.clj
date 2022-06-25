(ns time-x.clock.component
  (:require [schema.core :as s]
            [com.stuartsierra.component :as component]
            [time-x.clock.schema :as s.clock])
  (:import [java.time Clock]))

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

(s/defrecord Clocker
             [maybe-conf :- s.clock/ClockConf
              clock-control :- s.clock/ClockControl]

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
