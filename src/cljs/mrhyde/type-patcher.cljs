(ns mrhyde.type-patcher
  (:require [mrhyde :refer [hyde-array? has-cache? from-cache hyde? IHyde]]
            [mrhyde.guts :refer [get-store-cur-js-fn restore-original-js-fn
                            hyde-proto-array-marker hyde-proto-object-marker]]
            [clojure.set :refer [difference]]
            ))

; generate get-set-prop fn  with closure around stored data
(def install-js-get-prop ((fn []
  (let [reusable-descriptor (js-obj)]
    (aset reusable-descriptor "configurable" true)
    (aset reusable-descriptor "enumerable" true)
    (fn internal-js-getset-prop [obj nam getfn]
      (aset reusable-descriptor "get" getfn)
      (.defineProperty js/Object obj nam reusable-descriptor))))))

; generate js-getset-prop fn with closure around stored data
(def install-js-getset-prop ((fn []
  (let [reusable-descriptor (js-obj)]
    (aset reusable-descriptor "configurable" true)
    (aset reusable-descriptor "enumerable" true)
    (fn internal-js-getset-prop [obj nam getfn setfn]
      (aset reusable-descriptor "get" getfn)
      (aset reusable-descriptor "set" setfn)
      (.defineProperty js/Object obj nam reusable-descriptor))))))

; a replacement for the array map call
(defn mapish [f]
  (this-as ct
    (doall (map 
      #(.call f js/undefined % %2 ct) (seq ct) (range)))))

; a replacement for the array foreach call (which is map that returns null)
(defn eachish [f]
  ; call mapish with the same 'this'
  (this-as ct (.call mapish ct f))
  nil)

(defn hyde-array-splice [& args]
  (.log js/console "WARNING: someone has called unsupported method splice")
)

; currently only implemented for positive indices
; tries to honor laziness
; most likely has different edge cases if end > begin and whatnot
(defn hyde-array-slice [& args]
  (this-as this
    (let [begin (first args)
          end (second args)]
      (if (nil? end)
        (drop begin this)
        (take (- end begin) (drop begin this)))
    ))
)

(defn hyde-array-push [& args]
  (.log js/console "WARNING: someone has called unsupported method push")
)

(defn hyde-array-sort [& args]
  (.log js/console "WARNING: someone has called unsupported method sort")
)

(def hyde-cache-key   "$cljs$mrhyde$cache")
(def hyde-access-key  "$cljs$mrhyde$acccess")
(def hyde-keylist-key "$cljs$mrhyde$keylist")
(def hyde-keyset-key  "$cljs$mrhyde$keyset")

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
          (aset t hyde-access-key c)
          (aset t hyde-cache-key c)))
      ; now use it
      (let [c (aget t hyde-cache-key)]
        (assoc! c k v)))))

; this can be called standalone or auto-run from cljs map initialzation
(defn patch-map [m]
  ; (.log js/console (str "keys: " (keys m)))
  (aset m hyde-access-key m)
  (doseq [k (keys m)]
    ; TODO: we need a better way to get prototype and could fallback to m
    (let [p (.-__proto__ m)]
      (if (and (keyword? k) (not (goog.object.containsKey m (name k))))
        (install-js-getset-prop p (name k) (gen-map-getter k) (gen-map-setter k))
  )))
  (if (some keyword? (keys m)) (do
    (aset m hyde-keylist-key false)
    (aset m hyde-keyset-key false)
    (aset m hyde-keylist-key (.keys js/Object m))))
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
  (let [orig-fn (aget js/cljs.core s)
        orig-keys (.keys js/Object orig-fn)
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
        orig-keys (.keys js/Object orig-fn)
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

(defn gen-seq-getter [n]
  (fn []
    (this-as t 
      (let [src (if (goog.object.containsKey t hyde-cache-key) (aget t hyde-cache-key) t)]
        (nth src n js/undefined)))))

(defn gen-seq-setter [n]
  (fn [v]
    (this-as t
      ; (.log js/console (str "setter: " t "," v "," n))
      ; ensure cache (transient) exists
      (if-not (goog.object.containsKey t hyde-cache-key)
        (let [c (transient t)]
          (aset t hyde-cache-key c)))
      ; now use it
      (let [c (aget t hyde-cache-key)]
        (assoc! c n v)))))

; Add functionality to cljs seq prototype to make it more like a js array
(defn patch-prototype-as-array [p o]
  ; mark this prototype as a 'hyde array'
  (aset p hyde-proto-array-marker true)
  ; array length call
  (install-js-get-prop p "length" #(this-as t (count (take MAXLEN t))))
  ; access by index... would be great if there were a smarter solution
  (dotimes [n MAXLEN]
    (install-js-getset-prop p n (gen-seq-getter n) (gen-seq-setter n)))
  ; if we are acting like an array, we'll need in impl of forEach and map
  (-> p .-forEach (set! eachish))
  (-> p .-map (set! mapish))
  ; this is a half-hearted version
  (-> p .-slice (set! hyde-array-slice))
  ; these not yet supported, but at least we get a warning
  (-> p .-splice (set! hyde-array-splice))
  (-> p .-push (set! hyde-array-push))
  (-> p .-sort (set! hyde-array-sort))
  ; and we should print like a native string. (& squirrel the native one away)
  (-> p .-toCljString (set! (-> p .-toString)))
  (-> p .-toString (set! #(this-as t (clojure.string/join ", " t)))))

; Add functionality to cljs seq prototype to make it more like a js array
(defn patch-prototype-as-map [p o]
  ; mark this prototype as a 'hyde array'
  (aset p hyde-proto-object-marker true))

(defn add-hyde-protocol-to-seq [s]
  (extend-type s
  IHyde
  (has-cache? [this]
    (goog.object.containsKey this hyde-cache-key))
  (from-cache [this]
    (if-let [c (aget this hyde-cache-key)]
      ; attempt1: can we make a transient copy of a transient?
      (let [p (persistent! c)]
        (aset this hyde-cache-key (transient p))
        p)
      this))
  )
)

(defn filtered-keylist-set [l]
  (set (remove #(re-find #"cljs\$" %) l)))

(defn lazy-init-hyde-setset [m]
  (if (and (not (aget m hyde-keyset-key)) (aget m hyde-keylist-key))
    ; translate keylist into keymap
    (aset m hyde-keyset-key (filtered-keylist-set (aget m hyde-keylist-key)))))

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
            (aset this hyde-cache-key (transient p))
            ; persistent object mashed up with new keys
            (merge p new-map))
          ; else
          (merge this new-map))
      ))
  ))

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
    (fn [& args] (apply repersist (this-as t (.apply x t args)) opts))
    ;else
    (apply recurse-from-hyde-cache x opts)))

(def have-patched-arrayish-flag (atom false))
(def have-patched-mappish-flag (atom false))

; there must be a smarter way to do this, but for now i'll forge ahead
(defn patch-known-arrayish-types []
  (if-not @have-patched-arrayish-flag (do
    (reset! have-patched-arrayish-flag true)
    (doseq [p [cljs.core.PersistentVector
               cljs.core.List
               cljs.core.LazySeq
               cljs.core.IndexedSeq
               cljs.core.Cons
               cljs.core.Range
               cljs.core.ArrayNodeSeq
               cljs.core.ChunkedSeq]]
       (patch-prototype-as-array (aget p "prototype") p)
       (add-hyde-protocol-to-seq p))
    (patch-core-seq-type "PersistentVector"))))

(defn patch-known-mappish-types [] 
  (if-not @have-patched-mappish-flag (do
    (reset! have-patched-mappish-flag true)
    (doseq [p [cljs.core.ObjMap
               cljs.core.PersistentHashMap]]
       (patch-prototype-as-map (aget p "prototype") p)
       (add-hyde-protocol-to-map p))

    (patch-core-map-type "ObjMap")
    (patch-core-map-type "PersistentHashMap"))))

