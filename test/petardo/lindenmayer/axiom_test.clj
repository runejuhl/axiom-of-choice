(ns petardo.lindenmayer.axiom-test
  (:require [clojure.test :refer :all]
            [petardo.lindenmayer.axiom :refer :all]))

(deftest basic
  (is (= (:canvas (forward-generation {})) '())))
