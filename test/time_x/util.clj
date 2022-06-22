(ns time-x.util
  (:require [time-x.util :as sut :refer :all]
            [clojure.test :as t :refer :all]))

(deftest tap-test
  (testing "return identity"
    (is (= 10 (tap 10)))))

(deftest not-nil?-test
  (testing "only nil is nil"
    (is (true? (not-nil? {}))))
  (testing "only nil is nil"
    (is (false? (not-nil? nil)))))
