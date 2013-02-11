(ns mrhyde.tester)

;
; This framework is a mutated form of domina's test suite.
;

; debug helper
; (defn p [& args]
;   (.log js/console (apply str args))
; )

(def d3 (this-as ct (aget ct "d3")))

(js* "
  window['tryfn'] = function(f) {
    try {
      return f.call();
    } catch (e) {
      if(e == \"fail hard\") {
          throw e;
      } else {
          return e;
      }
    }
  };")

(def tests (atom []))

(defn add-test [name testfn]
  (swap! tests conj [name testfn]))

(defn run-test [testfn]
  (js/tryfn testfn))

(defn run-named [n]
  (map (fn [[name testfn]]
         [name (run-test testfn)])
       (filter (fn [[name _]] (= name n))
               @tests)))

(defn run-tests []
  (map (fn [[name testfn]]
         [name (run-test testfn)])
       @tests))

(defn reset-tests []
  (reset! tests []))

(defn report
  [suitename test-results]
  (if-let [script-node (-> d3 (.select "#hook-of-destruction") .node)]
    (if-let [selected-parent (-> d3 (.select (-> script-node .-parentNode)))]
      (let [failure-count (count (filter (complement nil?) (map second test-results)))]
        (-> selected-parent (.selectAll "*") .remove)
        (-> selected-parent (.append "div")
          (.text (str suitename " ran " (count test-results) " tests with " failure-count " failures"))
          (.attr "id" "test-summary")
          (.classed (if (zero? failure-count) "passed" "failed") true))
        (-> selected-parent (.selectAll "div.test-resuls")
            (.data (clj->js test-results))
          (.enter) (.append "div")
            (.attr "class" (fn [[_ res] _] (str "test-result " (if (nil? res) "passed" "failed"))))
            (.text (fn [[nam _] _] nam)) )))))

(def test-results (doall (run-tests)))

(defn run-all-tests [suitename]
  (report suitename (doall (run-tests)))
  (reset-tests))

; #_(def test-results (doall (run-named "can get multiple HTML attributes from a single node.")))
; (report "strokes" test-results)
; (run-all-tests "strokes")