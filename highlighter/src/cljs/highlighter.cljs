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
; {:color-on: [1,2,...]
;  :color-off: [1,2,...]
;  :visible: [1,2,...]
;  :invisible: [1,2,...]}
(defn key-decision [code shift]
  ; where is clojure.core.char? oh well, sorry.
  (cond 
    (< 48 code 59)
      (if shift
        ; shift+number = hide
        {:invisible [(- code 48)]}
        ; number = show
        {:visible [(- code 48)]})
    (contains? hide-keys-indices code)
      (if shift
        ; shift+letter = highlight off
        {:color-on [(get hide-keys-indices code)]}
        ; letter = highlight
        {:color-off [(get hide-keys-indices code)]})
    :else (case [code shift]
      ; map '0' to '10'
      [48 false]  {:visible [10]}
      [48 true]   {:invisible [10]}
      ; ` key hides or shows everything
      [192 true]  {:invisible v10}
      [192 false] {:visible v10}
      ; A key highlights or unhighlights everything
      [65 true]  {:color-on v10}
      [65 false] {:color-off v10}
      (do 
        ; -> debug show unknown keys... 
        (.log js/console code) 
        nil))))

(-> d3 (.select "body") (.on "keyup" (fn [e]
  (when-let [d (key-decision (-> d3 .-event .-keyCode)
      (-> d3 .-event .-shiftKey))]
    (doseq [n (:color-on d)]
      (-> d3 (.selectAll (str ".high" n))
        (.classed "highlight" true))) 
    (doseq [n (:color-off d)]
      (-> d3 (.selectAll (str ".high" n))
        (.classed "highlight" false))) 
    (doseq [n (:invisible d)]
      (-> d3 (.selectAll (str ".high" n))
        (.classed "invisible" true))) 
    (doseq [n (:visible d)]
      (-> d3 (.selectAll (str ".high" n))
        (.classed "invisible" false))) 
))))
