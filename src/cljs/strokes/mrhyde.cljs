; providing clojure data has a split personality: don't be afraid to let it all out
(ns strokes.mrhyde
  (:use [clojure.string :only [join]]
        [cljs.reader :only [read-string]]))

(defn patch-prototype-as-array [p]
  ;(.log js/console (str " :type is: " (type p)))
  (.__defineGetter__ p "length" #(this-as t (count t)))
  (dotimes [n 100] 
    (.__defineGetter__ p n #(this-as t (nth t n js/undefined))))
  (-> p .-toCljString (set! (-> p .-toString)))
  (-> p .-toString (set! #(this-as t (clojure.string/join ", " t)))))

; there must be a smarter way to do this, but for now i'll forge ahead
(defn patch-known-arrayish-types []
  ; cljs.core.PersistentVector
  (patch-prototype-as-array (.-__proto__ [1 2]))
  ; cljs.core.LazySeq
  (patch-prototype-as-array (.-__proto__ (for [x [1 2]] x)))
  ; ; range
  (patch-prototype-as-array (.-__proto__ (range 10)))
  ; ; cons
  (patch-prototype-as-array (.-__proto__  (conj (for [x (range 2)] x) 3)))
  ; cljs.core.ChunkedSeq?
)

(patch-known-arrayish-types)
