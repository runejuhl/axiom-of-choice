(ns petardo.lindenmayer.axiom-test
  (:require [clojure.test :refer :all]
            [petardo.lindenmayer.axiom :refer :all]))

(deftest basic
  (is (= (:canvas (forward-generation {})) '())))

(deftest turning
  (is (= 270
         (turn {:angle 360} -90))))
