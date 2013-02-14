; providing clojure data a split personality: don't be afraid to let it all out
(ns mrhyde.mrhyde
  (:require [clojure.string :refer [join re-matches]]
            [cljs.reader :refer [read-string]]
            [mrhyde.guts :refer [hyde-proto-array-marker hyde-proto-object-marker]]
            ))

; TODO
; add benchmarking on repersist call and setters

; silence warnings about protocol, what is all that gibber-jabber anyways?
(declare has-cache?)
(declare from-cache)

; 
(defprotocol IHyde
  "Container types extended with js metaprogramming"
  (has-cache? [this] "is there cached information attached from js mutation?")
  (from-cache [this] "render this container with cached information"))

(defn ^boolean hyde?
  "Returns true if coll satisfies IHyde"
  [x] (satisfies? IHyde x))

(defn ^boolean hyde-array?
  "Returns true if coll satisfies IHyde"
  [x] (and (satisfies? IHyde x) (aget x hyde-proto-array-marker)))

(defn ^boolean hyde-object?
  "Returns true if coll satisfies IHyde"
  [x] (and (satisfies? IHyde x) (aget x hyde-proto-object-marker)))

; these three functions are mainly handy from the debug console
(defn ^:export toclj
  "Convert js object to cljs equivalent"
  [x]
  (js->clj x :keywordize-keys true))

(defn ^:export tojs
  "Convert cljs object to js equivalent"
  [x]
  (clj->js x))

(defn ^:export cljread
  "Pass a string through the cljs reader and return the result"
  [x]
  (read-string x))
