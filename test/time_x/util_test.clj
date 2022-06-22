(ns time-x.util-test
  (:require [time-x.util :as util :refer :all]
            [clojure.test :refer :all]))

(deftest not-nil?-test
  (testing "only nil is nil map"
    (is (true? (util/not-nil? {}))))
  (testing "only nil is nil"
    (is (false? (util/not-nil? nil))))
  (testing "only nil is nil again vec"
    (is (true? (util/not-nil? []))))
  (testing "only nil is nil again set"
    (is (true? (util/not-nil? #{})))))
