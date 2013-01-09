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

(def MAXLEN 10000)

(defn patch-seq-object [o]
  ; this works but is now redundant
  ; (let [l (count (take MAXLEN o))]
  ;   (dotimes [n (- l 16)] 
  ;     (.__defineGetter__ o (str (+ n 16)) #(this-as t (nth t (+ n 16) js/undefined)))))
  nil)

; http://stackoverflow.com/questions/7015693/how-to-set-the-prototype-of-a-javascript-object-that-has-already-been-instantiat
; http://stackoverflow.com/questions/12035061/call-javascript-function-whenever-object-is-created
; http://javascriptweblog.wordpress.com/2010/11/15/extending-objects-with-javascript-getters/
; golden -> http://stackoverflow.com/a/8843181/1010653
(defn patch-core-seq-type [s]
  (.log js/console (str "patching call for " s))
  ; (-> js/gdebug (set! (aget js/cljs.core s)))
  (let [orig-fn (aget js/cljs.core s)
        orig-keys (js-keys orig-fn)
        ; new-fn  (fn [a b c d e f]
        ;           (orig-fn. a b c d e f))]

        ; new-fn  (fn [a b c d e f]
        ;           (let [binder (js/Function.prototype.bind.call orig-fn nil a b c d e f)]
        ;             (new binder)))]

        new-fn  (fn [& args]
                  (let [nargs (cons nil args)
                        binder (js/Function.prototype.bind.apply orig-fn nargs)
                        that  (new binder)]
                    (patch-seq-object that)
                    that))]

    ; set all properties of the new-fn based on the old one
    (doseq [k orig-keys]
      (.log js/console (str "remapping " k))
      ;(.__defineGetter__ new-fn k #(aget orig-fn k)))
      (aset new-fn k (aget orig-fn k)))
    (aset js/cljs.core s new-fn)))

; Add functionality to cljs seq prototype to make it more like a js array
(defn patch-prototype-as-array [p o]
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
  (doseq [p [cljs.core.PersistentVector
             cljs.core.LazySeq
             cljs.core.IndexedSeq
             cljs.core.Cons
             cljs.core.Range
             cljs.core.ChunkedSeq]]
     (patch-prototype-as-array (aget p "prototype") p))
  (patch-core-seq-type "PersistentVector"))

; ideally this would be hooked into the mapish-types constructor
(defn patch-map [m]
  ; (.log js/console (str "keys: " (keys m)))
  (doseq [k (keys m)]
      (.__defineGetter__ m (name k) #(get m k)))
  m)

; patch a (1 arity) js function to return a clj-ish value
(defn patch-fn1-return-value [o field-name]
  (let [orig-fn (aget o field-name)]
    (aset o field-name (fn [x] (js->clj (orig-fn x))))))

; patch a (2 arity) js function convert any keyword args to functions
(defn patch-args-keyword-to-fn2 [o field-name]
  (let [orig-fn (aget o field-name)]
    (aset o field-name
      (fn [x1,x2]
        (let [y1 (if (keyword? x1) #(x1 %) x1)
              y2 (if (keyword? x2) #(x2 %) x2)]
          (this-as ct (.call orig-fn ct y1 y2)))))))
