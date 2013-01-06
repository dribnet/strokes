; providing clojure data a split personality: don't be afraid to let it all out
(ns strokes.mrhyde
  (:use [clojure.string :only [join]]
        [cljs.reader :only [read-string]]))

; a replacement for the map call
(defn mapish [f]
  (this-as ct
    (doall (map 
      #(.call f js/undefined % %2 ct) (seq ct) (iterate inc 0)))))

; a replacement for the foreach call (which is map that returns null)
(defn eachish [f]
  ; call mapish with the same 'this'
  (this-as ct (.call mapish ct f))
  nil)

; Add functionality to cljs seq prototype to make it more like a js array
(defn patch-prototype-as-array [p]
  ; array length call
  (.__defineGetter__ p "length" #(this-as t (count t)))
  ; access by index... we obviously need a smarter upper bound here
  (dotimes [n 5000] 
    (.__defineGetter__ p n #(this-as t (nth t n js/undefined))))
  ; if we are acting like an array, we'll need in impl of forEach and map
  (-> p .-forEach (set! eachish))
  (-> p .-map (set! mapish))
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
