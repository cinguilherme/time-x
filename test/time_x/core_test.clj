(ns time-x.core-test
  (:require [clojure.test :refer :all]
            [time-x.core :refer :all]))


(deftest a-test
  (testing "FIXME, I don't fail."
    (is (true? (= 2 (count (str (= 1 1))))))))

(test-var a-test)
