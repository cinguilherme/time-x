(ns time-x.core
  (:import [java.time ZonedDateTime ZoneId]))

(defn now
  ([] (ZonedDateTime/now))
  ([zone-id] (ZonedDateTime/now zone-id)))

(defn to-instant [date])

(println (now))

(comment (now)
         (now (ZoneId/of "America/Recife"))
         (now (ZoneId/of "America/Sao_Paulo")))

