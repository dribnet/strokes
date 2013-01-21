; providing clojure data a split personality: don't be afraid to let it all out
(ns mrhyde
  (:require [clojure.string :refer [join]]
            [cljs.reader :refer [read-string]]))

; a replacement for the map call
(defn mapish [f]
  (this-as ct
    (doall (map 
      #(.call f js/undefined % %2 ct) (seq ct) (range)))))

; a replacement for the foreach call (which is map that returns null)
(defn eachish [f]
  ; call mapish with the same 'this'
  (this-as ct (.call mapish ct f))
  nil)

; this can be called standalone or auto-run from cljs map initialzation
(defn patch-map [m]
  ; (.log js/console (str "keys: " (keys m)))
  (doseq [k (keys m)]
    (if-not (goog.object.containsKey m (name k))
      (.__defineGetter__ m (name k) #(get m k))))
  m)

(defn- strkey [x]
  (if (keyword? x)
    (name x)
    x))

(def have-patched-js-with-key-lookup (atom false))

(defn patch-js-with-key-lookup []
  (if-not @have-patched-js-with-key-lookup (do
    (reset! have-patched-js-with-key-lookup true)
    (extend-type object
      ILookup
      (-lookup
        ([o k]
           (aget o (strkey k)))
        ([o k not-found]
          (let [s (strkey k)]
            (if (goog.object.containsKey o s)
              (aget o s) 
              not-found))))))))

(def MAXLEN 5000)

(defn patch-seq-object [o]
  ; this works but is now redundant
  ; (let [l (count (take MAXLEN o))]
  ;   (dotimes [n (- l 16)] 
  ;     (.__defineGetter__ o (str (+ n 16)) #(this-as t (nth t (+ n 16) js/undefined)))))
  nil)

(defn patch-map-object [o]
  ;(.log js/console (str (keys o)))
  (patch-map o)
  nil)

; of note -> http://stackoverflow.com/a/8843181/1010653
(defn patch-core-seq-type [s]
  ; (.log js/console (str "patching seq type " s))
  ; (-> js/gdebug (set! (aget js/cljs.core s)))
  (let [orig-fn (aget js/cljs.core s)
        orig-keys (js-keys orig-fn)
        new-fn  (fn [& args]
                  (let [nargs (cons nil args)
                        binder (js/Function.prototype.bind.apply orig-fn nargs)
                        that  (new binder)]
                    (patch-seq-object that)
                    that))]

    ; set all properties of the new-fn based on the old one
    (doseq [k orig-keys]
      ;(.log js/console (str "remapping " k))
      ;(.__defineGetter__ new-fn k #(aget orig-fn k)))
      (aset new-fn k (aget orig-fn k)))
    (aset js/cljs.core s new-fn)))

(defn patch-core-map-type [s]
  ; (.log js/console (str "patching map type " s))
  (let [orig-fn (aget js/cljs.core s)
        orig-keys (js-keys orig-fn)
        new-fn  (fn [& args]
                  (let [nargs (cons nil args)
                        binder (js/Function.prototype.bind.apply orig-fn nargs)
                        that  (new binder)]
                    (patch-map-object that)
                    that))]

    ; set all properties of the new-fn based on the old one
    (doseq [k orig-keys]
      ;(.log js/console (str "remapping " k))
      ;(.__defineGetter__ new-fn k #(aget orig-fn k)))
      (aset new-fn k (aget orig-fn k)))
    (aset js/cljs.core s new-fn)))

(def hydekey "$cljs$mrhyde$cache")

(defn gen-getter [n]
  (fn []
    (this-as t 
      (let [src (if (goog.object.containsKey t hydekey) (aget t hydekey) t)]
        (nth src n js/undefined)))))

(defn gen-setter [n]
  (fn [v]
    (this-as t
      ; (.log js/console (str "setter: " t "," v "," n))
      ; ensure cache (transient) exists
      (if-not (goog.object.containsKey t hydekey)
        (let [c (transient t)]
          (aset t hydekey c)))
      ; now use it
      (let [c (aget t hydekey)]
        (assoc! c n v)))))

;           ))
;       (let [src (if (goog.object.containsKey t hydekey) (t/hydekey) t)]
;         (nth src n js/undefined)))))

; Add functionality to cljs seq prototype to make it more like a js array
(defn patch-prototype-as-array [p o]
  ; array length call
  (.__defineGetter__ p "length" #(this-as t (count (take MAXLEN t))))
  ; access by index... we obviously need a smarter upper bound here
  (dotimes [n MAXLEN]
    (.__defineGetter__ p n (gen-getter n))
    (.__defineSetter__ p n (gen-setter n)))
  ; if we are acting like an array, we'll need in impl of forEach and map
  (-> p .-forEach (set! eachish))
  (-> p .-map (set! mapish))
  ; and we should print like a native string. (& squirrel the native one away)
  (-> p .-toCljString (set! (-> p .-toString)))
  (-> p .-toString (set! #(this-as t (clojure.string/join ", " t)))))

; silence warnings about protocol, what is all that gibber-jabber anyways?
(declare has-cache?)
(declare from-cache)

(defprotocol IHyde
  "Container types extended with js metaprogramming"
  (has-cache? [this] "is there cached information attached from js mutation?")
  (from-cache [this] "render this container with cached information"))

(defn add-hyde-protocol-to-seq [s]
  (extend-type s
  IHyde
  (has-cache? [this]
    (goog.object.containsKey this hydekey))
  (from-cache [this]
    (if-let [c (aget this hydekey)]
      ; attempt1: can we make a transient copy of a transient?
      (let [p (persistent! c)]
        (aset this hydekey (transient p))
        p)
      this))
  )
)

; (this-as ct (aset ct "hascache" (fn [x] (has-cache? x))))
; (this-as ct (aset ct "fromcache" (fn [x] (from-cache x))))

(defn ^boolean hyde?
  "Returns true if coll satisfies IHyde"
  [x] (satisfies? IHyde x))

(def have-patched-arrayish-flag (atom false))
(def have-patched-mappish-flag (atom false))

; there must be a smarter way to do this, but for now i'll forge ahead
(defn patch-known-arrayish-types []
  (if-not @have-patched-arrayish-flag (do
    (reset! have-patched-arrayish-flag true)
    (doseq [p [cljs.core.PersistentVector
               cljs.core.LazySeq
               cljs.core.IndexedSeq
               cljs.core.Cons
               cljs.core.Range
               cljs.core.ChunkedSeq]]
       (patch-prototype-as-array (aget p "prototype") p)
       (add-hyde-protocol-to-seq p))
    (patch-core-seq-type "PersistentVector"))))

(defn patch-known-mappish-types [] 
  (if-not @have-patched-mappish-flag (do
    (reset! have-patched-mappish-flag true)
    (patch-core-map-type "ObjMap"))))

; The following helpers patch individual js functions
; TODO: some (all?) of these require patch-args-clj-to-js to have been run, why?

(defn get-store-cur-js-fn [o field-name]
  (let [cur-fn (aget o field-name)
        js-fn-name (str "_js_" field-name)
        root-fn (aget o js-fn-name)]
    ; first store original (if we have not already done so)
    (if (= js/undefined root-fn)
      ; (.log js/console "storing: " js-fn-name)
      (aset o js-fn-name cur-fn))
    ; return cur-fn
    cur-fn))

; patch a js function to return a clj-ish value
(defn patch-return-value-to-clj [o field-name]
  (let [orig-fn (get-store-cur-js-fn o field-name)]
    (aset o field-name (fn [& args] 
      (js->clj (this-as ct (.apply orig-fn ct args)))))))

; patch a js function convert specified keyword args to functions
(defn patch-args-keyword-to-fn [o field-name & fields]
  (let [orig-fn (get-store-cur-js-fn o field-name)
        arg-filter (if (empty? fields) #(identity true) (set fields))]
    (aset o field-name
      (fn [& args]
        ; (.log js/console (str "patching: " (count args)))
        (let [nargs (map (fn [c x] 
                           (if (and (arg-filter c) (keyword? x)) #(x %) x))
                         (range) args)]
          ; (.log js/console (str "patched: " (type (nth nargs 0))))
          (this-as ct (.apply orig-fn ct nargs)))))))

; patch a js function, converting specified seqs to js arrays
(defn patch-args-seq-to-array [o field-name & fields]
  (let [orig-fn (get-store-cur-js-fn o field-name)
        arg-filter (if (empty? fields) #(identity true) (set fields))]
    (aset o field-name
      (fn [& args]
        ; (.log js/console (str "patching: " (count args)))
        (let [nargs (map #(if (and (arg-filter %1) (sequential? %2))
                            (apply array %2) %2) (range) args)]
          ; (.log js/console (str "patched: " (type (nth nargs 0))))
          (this-as ct (.apply orig-fn ct nargs)))))))

; the big hammer: patch a js function, converting specified args from clj to js
(defn patch-args-clj-to-js [o field-name & fields]
  (let [orig-fn (get-store-cur-js-fn o field-name)
        arg-filter (if (empty? fields) #(identity true) (set fields))]
    (aset o field-name
      (fn [& args]
        ; (.log js/console (str "patching: " (count args)))
        (let [nargs (map #(if (arg-filter %1) (clj->js %2) %2) (range) args)]
          ; (.log js/console (str "patched: " (type (nth nargs 0))))
          (this-as ct (.apply orig-fn ct nargs)))))))


; (defn patch-args-clj-to-js [o field-name]
;   (let [orig-fn (aget o field-name)]
;     (aset o field-name
;       (fn [& args]
;         ; (.log js/console (str "patching: " (count args)))
;         (let [nargs (map clj->js args)]
;           ; (.log js/console (str "patched: " (type (nth nargs 0))))
;           (this-as ct (.apply orig-fn ct nargs)))))))

; poor man's unit test
; add this to your html
;
    ; M = {
    ;   testthis: function(a,b,c) {
    ;     console.log("here comes a b c");
    ;     console.log(a);
    ;     console.log(b);
    ;     console.log(c);
    ;   }
    ; }
;
; then
(this-as ct (aset ct "toclj" js->clj))
; (patch-args-clj-to-js js/M "testthis" 0 2)
;
; and from the browser js console:
; M.testthis(toclj({}), toclj({}), toclj({}))
