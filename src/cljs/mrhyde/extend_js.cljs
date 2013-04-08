;;Namespace that extends JavaScript's object and array to play nicely
;;with Clojure's semantics and act as transient collections.
;;(this code by Kevin Lynagh / http://keminglabs.com)

(ns mrhyde.extend-js
  (:require [goog.object :as gobject])
  (:refer-clojure :exclude [assoc!
                            map filter remove]))

;;TODO: Why isn't this in cljs core?
(defn assoc!
  "Transient associate allowing multiple k/v pairs."
  ([tcoll k v]
     (-assoc! tcoll k v))
  ([tcoll k v & kvs]
     (let [ret (assoc! tcoll k v)]
       (if kvs
         (recur ret (first kvs) (second kvs) (nnext kvs))
         ret))))

(defn assoc-in!
  "Mutates a value in a nested associative structure, where ks is a
  sequence of keys and v is the new value and returns the nested structure."
  [m [k & ks] v]
  (if ks
    (assoc! m k (assoc-in! (get m k) ks v))
    (assoc! m k v)))

(defn update-in!
  ([m [k & ks] f & args]
     (if ks
       (assoc! m k (apply update-in! (get m k) ks f args))
       (assoc! m k (apply f (get m k) args)))))

;;To use Clojure's idiomatic seq-manipulation functions (filter, remove, &c.) on JavaScript arrays and objects we need to lift cljs.core's functions into ones that can delegate into JavaScript or ClojureScript appropriate implementations.
;;TODO: macro away this ugliness, and put together some benchmarks to see which is faster: multimethods or a double fn call that uses protocols with a fascade to keep the collection as the second fn argument to match clojure.core's seq-related fn style.
(defn seqtype [x]
  (cond
    (goog.isArray x)  :js-arr
    (seq? x)          :seq
    (goog.isObject x) :js-obj))


(defmulti filter (fn [pred coll] (seqtype coll)))

(defmethod filter :js-arr
  [pred a]
  (.filter a #(pred %)))

(defmethod filter :seq
  [pred coll]
  (cljs.core.filter pred coll))


(defmulti remove (fn [pred coll] (seqtype coll)))

(defmethod remove :js-arr
  [pred a]
  (.filter a #(not (pred %))))

(defmethod remove :seq
  [pred coll]
  (cljs.core.remove pred coll))


(defmulti map (fn [pred coll] (seqtype coll)))

(defmethod map :js-arr
  [f a]
  ;;defend against the extra two arguments that Array.map passes into f; we want the same semantics as CLJS map (i.e., the mapper fn gets just the item)
  (.map a #(f %1)))

(defmethod map :seq
  [f coll]
  (cljs.core.map f coll))

;;Make JavaScript objects and arrays place nicely with ClojureScript by implementing lookup protocols and acting like Clojure's transient collections

(defn strkey [x]
  (if (keyword? x)
    (name x)
    x))

(extend-type object
  ILookup
  (-lookup
    ([o k]
       (aget o (strkey k)))
    ([o k not-found]
       (let [s (strkey k)]
         (if (goog.object.containsKey o s)
           (aget o s)
           not-found))))

  IEmptyableCollection
  (-empty [_]
    (js-obj))

  ITransientCollection
  (-conj! [o [k v]]
    (assoc! o k v))
  (-persistent! [_]
    (throw (js/Error. "JavaScript object isn't a real transient, don't try to make it persistent.")))

  ITransientAssociative
  (-assoc! [o k v]
    (aset o (strkey k) v)
    o)

  ITransientMap
  (-dissoc! [o key]
    (gobject/remove o key)
    o)

  ISeqable
  (-seq [o]
    (let [keys (gobject/getKeys o)]
      (when (pos? (alength keys))
        (map #(vector % (aget o %))
             (sort keys))))))

(extend-type array
  IEmptyableCollection
  (-empty [a]
    (array))

  ITransientCollection
  (-conj! [a x]
    (.push a x)
    a)
  (-persistent! [_]
    (throw (js/Error. "JavaScript array isn't a real transient, don't try to make it persistent.")))

  ITransientAssociative
  (-assoc! [a k v]
    (aset a (strkey k) v)
    a))
