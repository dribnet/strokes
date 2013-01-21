(ns mrhyde.test
    (:require [domina.tester :refer [add-test run-all-tests]]))

(defn ^:export launch []
  (add-test "1 + 1 = 2"
            #(do (assert (= 2 (+ 1 1)))))

  ; (add-test "1 + 1 = 3"
  ;           #(do (assert (= 3 (+ 1 1)))))

  (run-all-tests "mrhyde"))