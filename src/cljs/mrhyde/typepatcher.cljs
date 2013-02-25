(ns mrhyde.typepatcher
  (:require [mrhyde.mrhyde :refer [hyde-array? hyde-object? has-cache? from-cache hyde? IHyde]]
            [mrhyde.guts :refer [get-store-cur-js-fn restore-original-js-fn
                            hyde-proto-array-marker hyde-proto-object-marker]]
            [clojure.set :refer [difference]]
            ))

; debug helper
(defn dp [& args]
  (.log js/console (apply str args)) )

; generate get-prop fn  with closure around stored data
(def install-js-get-prop ((fn []
  (let [reusable-descriptor (js-obj)]
    (aset reusable-descriptor "configurable" true)
    (aset reusable-descriptor "enumerable" true)
    (fn internal-js-getset-prop [obj nam getfn]
      (aset reusable-descriptor "get" getfn)
      (.defineProperty js/Object obj nam reusable-descriptor))))))

; generate hidden get-prop fn  with closure around stored data
(def install-js-hidden-get-prop ((fn []
  (let [reusable-descriptor (js-obj)]
    (aset reusable-descriptor "configurable" true)
    (aset reusable-descriptor "enumerable" false)
    (fn internal-js-getset-prop [obj nam getfn]
      (aset reusable-descriptor "get" getfn)
      (.defineProperty js/Object obj nam reusable-descriptor))))))

; for object obj hide all props in seq (make non-enumerable)
(def hide-js-props ((fn []
  (let [reusable-descriptor (js-obj)]
    (aset reusable-descriptor "enumerable" false)
    (fn internal-js-getset-prop [obj s]
      (doseq [nam s]
        (.defineProperty js/Object obj nam reusable-descriptor)))))))

; generate js-getset-prop fn with closure around stored data
(def install-js-getset-prop ((fn []
  (let [reusable-descriptor (js-obj)]
    (aset reusable-descriptor "configurable" true)
    (aset reusable-descriptor "enumerable" true)
    (fn internal-js-getset-prop [obj nam getfn setfn]
      (aset reusable-descriptor "get" getfn)
      (aset reusable-descriptor "set" setfn)
      (.defineProperty js/Object obj nam reusable-descriptor))))))

; generate js-getset-prop fn with closure around stored data
(def install-js-hidden-getset-prop ((fn []
  (let [reusable-descriptor (js-obj)]
    (aset reusable-descriptor "configurable" true)
    (aset reusable-descriptor "enumerable" false)
    (fn internal-js-getset-prop [obj nam getfn setfn]
      (aset reusable-descriptor "get" getfn)
      (aset reusable-descriptor "set" setfn)
      (.defineProperty js/Object obj nam reusable-descriptor))))))

; add a non-enumerable property
(defn aset-hidden [o nam p]
  (aset o nam p)
  (hide-js-props o [nam]))

(def hyde-cache-key   "$cljs$mrhyde$cache")
(def hyde-access-key  "$cljs$mrhyde$acccess")
(def hyde-keylist-key "$cljs$mrhyde$keylist")
(def hyde-keyset-key  "$cljs$mrhyde$keyset")
(def cljs-partition-key  "cljs$lang$protocol_mask$partition0$")
(def hyde-parition-key (str "$cljs$mrhyde$" cljs-partition-key))

(defn hyde-array-ensure-cached [h]
  (if-not (goog.object.containsKey h hyde-cache-key)
    (aset-hidden h hyde-cache-key (apply array h))))

;;;; ARRAY METHODS

;; Array Mutator methods

(defn hyde-array-pop [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method pop"))

(defn hyde-array-push [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method push"))

(defn hyde-array-reverse [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method reverse"))

(defn hyde-array-shift [& args]
  (this-as t
    ; ensure cache (transient) exists
    (hyde-array-ensure-cached t)
    (let [c (aget t hyde-cache-key)]
      (.shift c))))

(defn hyde-array-sort [& args]
  (this-as t
    ; ensure cache (transient) exists
    (hyde-array-ensure-cached t)
    (let [c (aget t hyde-cache-key)]
      (.sort c)
      ; return yourself
      t)))

(defn hyde-array-splice [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method splice"))

(defn hyde-array-unshift [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method unshift"))

;; Array accessor methods

(defn hyde-array-concat [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method concat"))

(defn hyde-array-join [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method join"))

(defn hyde-array-concat [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method concat"))

(defn hyde-array-concat [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method concat"))

(defn hyde-array-concat [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method concat"))

(defn hyde-array-concat [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method concat"))

; currently only implemented for positive indices
; tries to honor laziness
; most likely has different edge cases if end > begin and whatnot
(defn hyde-array-slice [& args]
  (this-as this
    (let [begin (first args)
          end (second args)]
      (if (nil? end)
        (drop begin this)
        (take (- end begin) (drop begin this))))))

(defn hyde-array-vector-slice [& args]
  (.log js/console "note: calling untested hyde-array vector-slice")
  (this-as this
    (apply subvec this args)))

(defn hyde-array-to-source [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method toSource"))

(defn hyde-array-to-string [& args]
  (this-as this (clojure.string/join ", " this)))

(defn hyde-array-index-of [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method indexOf"))

(defn hyde-array-last-index-of [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method lastIndexOf"))

;; Array iteration methods

(defn hyde-array-every [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method every"))

(defn hyde-array-some [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method some"))

(defn hyde-array-filter [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method filter"))

(defn hyde-array-map [f]
  (this-as ct
    (doall (map 
      #(.call f js/undefined % %2 ct) (seq ct) (range)))))

; forEach is a map that returns null
(defn hyde-array-for-each [f]
  ; call mapish with the same 'this'
  (this-as ct (.call hyde-array-map ct f))
  nil)

(defn hyde-array-reduce [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method reduce"))

(defn hyde-array-reduce-right [& args]
  (.log js/console "WARNING: someone has called unsupported hyde-array method reduce-Right"))


(defn- strkey [x]
  (if (keyword? x)
    (name x)
    x))

(defn gen-map-getter [k]
  (fn []
    (this-as t 
        (get (aget t hyde-access-key) k))))

(defn gen-map-setter [k]
  (fn [v]
    (this-as t
      ; (.log js/console (str "setter: " t "," v "," n))
      ; ensure cache (transient) exists
      (if-not (goog.object.containsKey t hyde-cache-key)
        (let [c (transient t)]
          (aset-hidden t hyde-access-key c)
          (aset-hidden t hyde-cache-key c)))
      ; now use it
      (let [c (aget t hyde-cache-key)]
        (assoc! c k v)))))

; this can be called standalone or auto-run from cljs map initialzation
(defn patch-map [m]
  ; (.log js/console (str "keys: " (keys m)))
  (aset-hidden m hyde-access-key m)
  ; hide all existing keys
  (hide-js-props m (.keys js/Object m))
  (doseq [k (keys m)]
    (if (and (keyword? k) (not (goog.object.containsKey m (name k))))
      (install-js-getset-prop m (name k) (gen-map-getter k) (gen-map-setter k)))
  )
  (if (some keyword? (keys m)) (do
    (aset-hidden m hyde-keylist-key false)
    (aset-hidden m hyde-keyset-key false)
    (aset-hidden m hyde-keylist-key (.keys js/Object m))))
  m)

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

(def MAXLEN (or (this-as ct (aget ct "mrhyde_maxseqlen")) 5000))

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
  (let [orig-fn (aget (.-core js/cljs) s)
        orig-keys (.keys js/Object orig-fn)
        new-fn  (fn [& args]
                  (let [nargs (apply array (cons nil args))
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
  (let [orig-fn (aget (.-core js/cljs) s)
        orig-keys (.keys js/Object orig-fn)
        new-fn  (fn [& args]
                  (let [nargs (apply array (cons nil args))
                        binder (js/Function.prototype.bind.apply orig-fn nargs)
                        that  (new binder)]
                    (patch-map-object that)
                    that))]

    ; (.log js/console (str "patching map type " s "+" orig-keys))
    ; set all properties of the new-fn based on the old one
    (doseq [k orig-keys]
      ;(.log js/console (str "remapping " k))
      ;(.__defineGetter__ new-fn k #(aget orig-fn k)))
      (aset new-fn k (aget orig-fn k)))
    (aset js/cljs.core s new-fn)))

(defn gen-seq-getter [n]
  (fn []
    (this-as t 
      (let [src (if (goog.object.containsKey t hyde-cache-key) (aget t hyde-cache-key) t)]
        (nth src n js/undefined)))))

(defn gen-seq-setter [n]
  (fn [v]
    (this-as t
      ; ensure cache (transient) exists
      (hyde-array-ensure-cached t)
      ; now use it
      (let [c (aget t hyde-cache-key)]
        (aset c n v)))))

; Add functionality to cljs seq prototype to make it more like a js array
(defn patch-prototype-as-array [p o is-vect]
  ; mark this prototype as a 'hyde array'
  (aset-hidden p hyde-proto-array-marker true)
  ; array length call
  (install-js-hidden-get-prop p "length" #(this-as t (count (take MAXLEN t))))
  ; access by index... would be great if there were a smarter solution
  (dotimes [n MAXLEN]
    (install-js-hidden-getset-prop p n (gen-seq-getter n) (gen-seq-setter n)))

  ; add a marker for js libraries that this object implements Array methods
  ; (see https://github.com/dribnet/ArrayLike.js)
  (aset-hidden p "__ArrayLike" true)

  ; squirrel away native print
  (aset-hidden p "toCljString" (-> p .-toString))

  ; install mutator methods
  (aset-hidden p "pop" hyde-array-pop)
  (aset-hidden p "push" hyde-array-push)
  (aset-hidden p "reverse" hyde-array-reverse)
  (aset-hidden p "shift" hyde-array-shift)
  (aset-hidden p "sort" hyde-array-sort)
  (aset-hidden p "splice" hyde-array-splice)
  (aset-hidden p "unshift" hyde-array-unshift)
  ; install accessor methods
  (aset-hidden p "concat" hyde-array-concat)
  (aset-hidden p "join" hyde-array-pop)
  (aset-hidden p "slice" (if is-vect hyde-array-vector-slice hyde-array-slice))
  (aset-hidden p "toSource" hyde-array-to-source)
  (aset-hidden p "toString" hyde-array-to-string)
  (aset-hidden p "indexOf" hyde-array-index-of)
  (aset-hidden p "lastIndexOf" hyde-array-last-index-of)
  ; install iteration methods
  (aset-hidden p "forEach" hyde-array-for-each)
  (aset-hidden p "every" hyde-array-every)
  (aset-hidden p "some" hyde-array-some)
  (aset-hidden p "filter" hyde-array-filter)
  (aset-hidden p "map" hyde-array-map)
  (aset-hidden p "reduce" hyde-array-reduce)
  (aset-hidden p "reduceRight" hyde-array-reduce-right)
)

; Add functionality to cljs seq prototype to make it more like a js array
(defn patch-prototype-as-map [p o]
  ; mark this prototype as a 'hyde object'
  (aset-hidden p hyde-proto-object-marker true)
)

(defn add-hyde-protocol-to-seq [s]
  (extend-type s
  IHyde
  (has-cache? [this]
    (goog.object.containsKey this hyde-cache-key))
  (from-cache [this]
    (if-let [c (aget this hyde-cache-key)]
      (vec c)
      this))
  )
)

(defn filtered-keylist-set [l]
  (set (remove #(re-find #"cljs\$" %) l)))

(defn lazy-init-hyde-setset [m]
  (if (and (not (aget m hyde-keyset-key)) (aget m hyde-keylist-key))
    ; translate keylist into keymap
    (aset-hidden m hyde-keyset-key (filtered-keylist-set (aget m hyde-keylist-key)))))

(defn add-hyde-protocol-to-map [m]
  (extend-type m
    IHyde
    (has-cache? [this]
      (lazy-init-hyde-setset this)
      (or (goog.object.containsKey this hyde-cache-key)
        (not= (aget this hyde-keyset-key) (filtered-keylist-set (.keys js/Object this)))))
    (from-cache [this]
      (lazy-init-hyde-setset this)
      (let [; current keyset (minus a possible cache-key)
            cur-keyset (difference (filtered-keylist-set (.keys js/Object this)) #{hyde-cache-key})
            ; what new keys have appeared
            new-keys   (difference cur-keyset (aget this hyde-keyset-key))
            ; put all new key/value pairs into their own map
            new-map    (into {} (for [k new-keys] [(keyword k) (aget this k)]))
            ; pull out the cache too (might be js/undefined)
            cache      (aget this hyde-cache-key)]
        (if cache
          ; make a persistent copy, and then store right away again as new transient!
          (let [p (persistent! cache)]
            (aset-hidden this hyde-cache-key (transient p))
            ; persistent object mashed up with new keys
            (merge p new-map))
          ; else
          (merge this new-map))
      ))
  )
  ; hide all existing keys
  (let [p (aget m "prototype")]
    (hide-js-props p (.keys js/Object p)))
)

(defn from-cache-if-has-cache [x]
  (if (and (hyde? x) (has-cache? x))
      (from-cache x) 
      x))

(defn recurse-from-hyde-cache
  "Recursively transforms any cached hyde objects to new persistent versions."
  [xo & opts]
  (let [; flexibily handling the skipset should not be a 4 step process...
        opts-map (apply array-map opts)
        skippers (get opts-map :skip [])
        skiplist (if (keyword? skippers) [skippers] skippers)
        skipset (set skiplist)]
    ; (.log js/console (str "skiplist is " skiplist " and skipset " skipset))
    ((fn internal-recurse [x]
      (cond
        (goog.isArray x)
          (vec (map internal-recurse x))
        (map? x)
          (let [c (from-cache-if-has-cache x)]
            (into {} (for [[k v] c] 
              [(internal-recurse k)
                (if (skipset k)
                  v
                  (internal-recurse v))])))
        (coll? x) 
          (let [c (from-cache-if-has-cache x)]
            (into (empty c) (map internal-recurse c)))
        :else
          (from-cache-if-has-cache x))) 
      xo)))

(defn repersist [x & opts]
  "recurse x (or return value of x) and extract persistent value
   options can include 
     :skip [keys] -> include vals for these keys as-is"
  (if (goog.isFunction x)
    (fn [& args] (apply repersist (this-as t (.apply x t (apply array args))) opts))
    ;else
    (apply recurse-from-hyde-cache x opts)))

(def have-patched-arrayish-flag (atom false))
(def have-patched-mappish-flag (atom false))

(defn patch-sequential-type [t]
  (if (hyde-array? (aget t "prototype"))
    nil ;(dp (str "already a hyde-array: " t))
    (do
     (patch-prototype-as-array (aget t "prototype") t false)
     (add-hyde-protocol-to-seq t))))

(defn patch-vector-type [t]
  (if (hyde-array? (aget t "prototype"))
    nil ; (dp (str "already a hyde-array: " t))
    (do
     (patch-prototype-as-array (aget t "prototype") t true)
     (add-hyde-protocol-to-seq t))))

(defn patch-map-type [[t, s]]
  (if (hyde-object? (aget t "prototype"))
    (dp (str "already a hyde-object: " t))
    (do
     (patch-prototype-as-map (aget t "prototype") t)
     (add-hyde-protocol-to-map t)
     (patch-core-map-type s))))

; there must be a smarter way to do this, but for now i'll forge ahead
(defn patch-known-sequential-types []
  (doseq [t [cljs.core.List
             cljs.core.LazySeq
             cljs.core.IndexedSeq
             cljs.core.Cons
             cljs.core.Range
             cljs.core.ArrayNodeSeq
             cljs.core.ChunkedSeq]]
    (patch-sequential-type t)))

; there must be a smarter way to do this, but for now i'll forge ahead
(defn patch-known-vector-types []
  (doseq [t [cljs.core.PersistentVector
             cljs.core.Subvec]]
    (patch-vector-type t)) )
  ; todo: is this doing something? refactor?
  ; (patch-core-seq-type "PersistentVector"))

; a funny idea
; (alter-meta! #'ObjMap assoc :extern true)

; this ensures the symbols exist, but they are copies so the wrong thing is patched...
; (at least when compiled with advanced closure optimations)
; (js* "
; goog.exportSymbol('cljs', cljs);
; goog.exportSymbol('cljs.core', cljs.core);
; goog.exportSymbol('cljs.ObjMap', cljs.core.ObjMap);
; goog.exportSymbol('cljs.PersistentHashMap', cljs.core.PersistentHashMap);
; goog.exportProperty(cljs, 'core', cljs.core);
; goog.exportProperty(cljs.core, 'ObjMap', cljs.core.ObjMap);
; goog.exportProperty(cljs.core, 'PersistentHashMap', cljs.core.PersistentHashMap);
; ")

(defn patch-known-mappish-types [] 
  (patch-sequential-type cljs.core.LazySeq) ; <-- TODO BUG - this should not be necessary!
  (doseq [t [[cljs.core.ObjMap, "ObjMap"]
             [cljs.core.PersistentHashMap, "PersistentHashMap"]]]
    (if (= (first t) (aget cljs.core (second t)))
      (patch-map-type t))))

; on any object, someone somewhere is getting the parition key
(defn get-partition-key []
  (this-as t
    (aget t hyde-parition-key)))

(defn set-partition-key [k]
  (this-as t
    (aset-hidden t hyde-parition-key k)
    (aset js/window "side" "effect")
    (if (= 16123663 k)
      (.log js/console "matches"))
    (.log js/console k)
    (.log js/console t)
      ; )
    ; (when (map? t)
    ;   (.log js/console (str "map: " k)))
    ; (when (not (zero? (unsafe-bit-and k 1024)))
    ;   (.log js/console (str "map: " k))))
))

(defn patch-obj-spy-on-partition []
  (.log js/console set-partition-key)
  ; (.__defineGetter__  (-> cljs.core.PersistentHashMap .-prototype) cljs-partition-key get-partition-key)
  ; (.__defineSetter__  (-> cljs.core.PersistentHashMap .-prototype) cljs-partition-key set-partition-key)

  (install-js-hidden-getset-prop 
    (-> cljs.core.PersistentHashMap .-prototype) cljs-partition-key get-partition-key set-partition-key)
)

; (defn get-member-name [o m]
;   (or (first (filter #(= (aget o %) m) (.keys js/Object o)))
;       (first (filter #(= (aget o %) m) (.keys js/Object js/window)))))

; (defn patch-map-type [[f, p]]
;   (if (hyde-object? (aget f "prototype"))
;     (dp (str "already a hyde-object: " f))
;     (if-let [nam (get-member-name p f)]
;       (do
;         (dp "i found " nam " in " p)
;         (patch-prototype-as-map (aget f "prototype") f)
;         (add-hyde-protocol-to-map f)
;         (patch-core-map-type f p nam)))))

; ; another failed attempt
; (defn patch-core-map-constructor [f]
;   ; (.log js/console (str "patching map type " s))
;   (let [orig-fn (aget f "constructor")
;         new-fn  (fn [& args]
;                   (let [nargs (apply array (cons nil args))
;                         that (js/Function.prototype.bind.apply orig-fn nargs)]
;                     (patch-map-object that)
;                     that))]
;     (aset f "constructor" new-fn)))
