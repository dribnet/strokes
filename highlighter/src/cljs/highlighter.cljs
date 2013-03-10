(ns venn-simple
  (:require [strokes :refer [d3]]
            [clojure.string :refer [join]]))

(strokes/bootstrap)

; quick and dirty highligher...

(def v10 [1 2 3 4 5 6 7 8 9 10])
(def all-classes
  (join "," (map #(str ".high" %) v10)))

; q w e r t y u i o p
(def hide-keys [81 81 87 69 82 84 89 85 73 79 80])
(def hide-keys-indices (zipmap hide-keys (range)))

; returns decision from keys
; {:all-off true/false, turn-on: [1,2,...]}
(defn key-decision [code shift]
  ; where is clojure.core.char? oh well, sorry.
  (cond 
    (< 48 code 59)
      {:all-off shift :turn-on [(- code 48)]}
    (contains? hide-keys-indices code)
      (if shift
        {:visible [(get hide-keys-indices code)]}
        {:invisible [(get hide-keys-indices code)]})
    :else (case [code shift]
      ; map '0' to '10'
      [48 false]  {:all-off false :turn-on [10]}
      [48 true]   {:all-off true  :turn-on [10]}
      [192 true]  {:all-off true}
      [192 false] {:all-off false :turn-on v10 :visible v10}
      (do 
        ; -> debug show unknown keys... (.log js/console code) 
        nil))))

(-> d3 (.select "body") (.on "keyup" (fn [e]
  (when-let [d (key-decision (-> d3 .-event .-keyCode)
      (-> d3 .-event .-shiftKey))]
    (if (:all-off d)
      (-> d3 (.selectAll all-classes)
        (.classed "highlight" false)
        (.classed "invisible" false)))
    (doseq [n (:turn-on d)]
      (-> d3 (.selectAll (str ".high" n))
        (.classed "highlight" true))) 
    (doseq [n (:invisible d)]
      (-> d3 (.selectAll (str ".high" n))
        (.classed "invisible" true))) 
    (doseq [n (:visible d)]
      (-> d3 (.selectAll (str ".high" n))
        (.classed "invisible" false))) 
))))
