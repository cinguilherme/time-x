(ns time-x.util)

(def not-nil? (comp not nil?))

(defn tap [v]
  (println v)
  v)

(comment
  (not-nil? nil)
  (not-nil? {})

  (tap 20)
  (println "end comment"))
