(ns axiom-test
  (:require [clojure.test :refer :all]
            [axiom :refer :all]))

(testing "basic tests"
  (is (= (second (forward-generation {})) '())))
