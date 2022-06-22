(ns time-x.core
  (:import [java.time ZonedDateTime ZoneId]))

(defn now
  ([] (ZonedDateTime/now))
  ([zone-id] (ZonedDateTime/now zone-id)))


(defn after? [d1 d2])
(defn before? [d1 d2])
(defn very-close? [d1 d2])
(defn diff-in-unit [d1 d2])
(defn plus [d1 unit])
(defn week-day? [d])
(defn weekend? [d])
(defn work-date? [d])

(defn to-instant [date])

(println (now))

(comment (now)
         (println (now (ZoneId/of "America/Recife")))
         (now (ZoneId/of "America/Sao_Paulo")))

