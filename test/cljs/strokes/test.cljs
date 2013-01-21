(ns strokes.test
    (:require [domina.tester :refer [add-test run-all-tests]]))

(defn ^:export launch []
  ; (add-test "2 + 2 = 2"
  ;           #(do (assert (= 2 (+ 2 2)))))

  (add-test "2 + 2 = 4"
            #(do (assert (= 4 (+ 2 2)))))

  (run-all-tests "strokes"))