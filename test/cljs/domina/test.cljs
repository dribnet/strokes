(ns domina.tester
  (:require [domina :refer [add-class! append! by-id destroy! prepend!
                            single-node set-text!]]
        [domina.xpath :refer [xpath]]
        [domina.events :refer [unlisten!]]))

;
; As the namespace states, this test framework ripped from domina
;

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

(defn reset
  "resets the page"
  []
  (destroy! (xpath "//body/*"))
  (unlisten! (xpath "//*")))

(defn standard-fixture
  "Standard fixture html"
  []
  (append! (xpath "//body")
           "<div class='d1'><p class='p1'>P1</p><p class='p2'>P2</p>
<p id='id1' class='p3'>P3</p>"))


(defn report
  [suitename test-results]
  (reset)
  (doseq [[name result] test-results]
    (let [result-div (single-node "<div class='test-result'></div>")]
      (set-text! result-div name)
      (if (not (= result nil))
        (do
          (add-class! result-div "failed")
          (append! result-div (str "<div class='message'>" result "</div>")))
        (add-class! result-div "passed"))
      (append! (xpath "//body") result-div)))
  (prepend! (xpath "//body") (str "<div id='test-summary'>" suitename ": ran <span id='total-tests'></span> tests with <span id='total-failures'></span> failures"))
  (set-text! (by-id "total-tests") (count test-results))
  (let [failure-count (count (filter (complement nil?) (map second test-results)))]
    (set-text! (by-id "total-failures") failure-count)
    (if (= 0 failure-count)
      (add-class! (by-id "test-summary") "passed")
      (add-class! (by-id "test-summary") "failed"))))

(def test-results (doall (run-tests)))

(defn run-all-tests [suitename]
  (report suitename (doall (run-tests)))
  (reset-tests))

; #_(def test-results (doall (run-named "can get multiple HTML attributes from a single node.")))
; (report "strokes" test-results)
; (run-all-tests "strokes")