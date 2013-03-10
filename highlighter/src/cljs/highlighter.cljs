(ns venn-simple
  (:require [strokes :refer [d3]]
            [clojure.string :refer [join]]))

(strokes/bootstrap)

; quick and dirty highligher...

(def v10 [1 2 3 4 5 6 7 8 9 10])
(def all-classes
  (join "," (map #(str ".high" %) v10)))

; returns decision from keys
; {:all-off true/false, turn-on: [1,2,...]}
(defn key-decision [code shift]
  ; where is clojure.core.char? oh well, sorry.
  (if (< 48 code 59)
    {:all-off shift :turn-on [(- code 48)]}
    (case [code shift]
      ; map '0' to '10'
      [48 false]  {:all-off false :turn-on [10]}
      [48 true]   {:all-off true  :turn-on [10]}
      [192 false] {:all-off true  :turn-on []}
      [192 true]  {:all-off false :turn-on v10}
      (do 
        ; -> debug show unknown keys... (.log js/console code) 
        nil))))

(-> d3 (.select "body") (.on "keyup" (fn [e]
  (when-let [d (key-decision (-> d3 .-event .-keyCode)
      (-> d3 .-event .-shiftKey))]
    (if (:all-off d)
      (-> d3 (.selectAll all-classes)
        (.classed "highlight" false)))
    (doseq [n (:turn-on d)]
      (-> d3 (.selectAll (str ".high" n))
        (.classed "highlight" true))) 
))))
