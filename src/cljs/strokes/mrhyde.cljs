; providing clojure data has a split personality: don't be afraid to let it all out
(ns strokes.mrhyde
  (:use [clojure.string :only [join]]
        [cljs.reader :only [read-string]]))

; todo: let's make a method that hooks into patched-array constructors
; (defn check-headroom [this proto & args]
;   (.log js/console (str " :type is: " (type proto)))
;   )

; a replacement for the foreach call
; thanks Arthur Ulfeldt: http://stackoverflow.com/questions/1651351/clojure-call-a-function-for-each-element-in-a-vector-with-it-index
(defn eachish [f]
  (this-as ct
    (doall (map 
      #(.call f js/undefined % %2 ct) (seq ct) (iterate inc 0))))
  nil)

(defn patch-prototype-as-array [p]
  ; array length call
  (.__defineGetter__ p "length" #(this-as t (count t)))
  ; access by index... we obviously need a smarter upper bound here
  (dotimes [n 5000] 
    (.__defineGetter__ p n #(this-as t (nth t n js/undefined))))
  ; (-> p .-bareConstructor (set! (-> p .-constructor)))
  ; (-> p .-constructor (set! (fn [& args] (this-as t (check-headroom t p args)))))
  ; if we are acting like an array, we'll need in impl of forEach
  (-> p .-forEach (set! eachish))
  ; and we should print like a native string. (& squirrel the native one away)
  (-> p .-toCljString (set! (-> p .-toString)))
  (-> p .-toString (set! #(this-as t (clojure.string/join ", " t)))))

; there must be a smarter way to do this, but for now i'll forge ahead
(defn patch-known-arrayish-types []
  (doseq [p [cljs.core.PersistentVector.prototype
      cljs.core.LazySeq.prototype
      cljs.core.IndexedSeq.prototype
      cljs.core.Cons.prototype
      cljs.core.Range.prototype
      cljs.core.ChunkedSeq.prototype]]
     (patch-prototype-as-array p)))

; ideally this would be hooked into the mapish-types constructor
(defn patch-map [m]
  ; (.log js/console (str "keys: " (keys m)))
  (doseq [k (keys m)]
      (.__defineGetter__ m (name k) #(get m k)))
  m)

; this should probably be in an init call or something
(patch-known-arrayish-types)
