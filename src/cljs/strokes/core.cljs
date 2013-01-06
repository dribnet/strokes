(ns strokes.core
  (:use [strokes.mrhyde :only [patch-known-arrayish-types patch-fn1-return-value]]
        [clojure.string :only [join]]
        [cljs.reader :only [read-string]]))

(def d3 js/d3)
(def Tau (* 2 Math/PI))

; filter d3.selection.attr inputs: v might be keyword function
(let [orig-d3-proto-attr (-> d3 .-selection .-prototype .-attr)]
  (-> d3 .-selection .-prototype .-attr
    (set! (fn [n,v]
      (let [vf (if (keyword? v) #(v %) v)]
        (this-as ct (.call orig-d3-proto-attr ct n vf)))))))

(defn- d3-edn
  ([url callback]
    (d3-edn url nil callback))
  ([url mime callback]
    (letfn [(ready [req] (callback (if req  (read-string (.-responseText req)) req)))]
      (.log js/console (str "loading: " url))
      (.xhr d3 url mime ready))))

(-> d3 .-edn (set! d3-edn)) 

(defn add-stragglers [x]
  "adds elements to map x that have been stuck onto the js object x"
  (let [empty-map {}
        new-keys (remove (set (js-keys empty-map)) (js-keys x))]
        ;main-keys (remove #(re-matches #"^:cljs\$.*" (str %)) new-keys)] <- idea for future?
    (into x
        (for [k new-keys]
          [(keyword k) (aget x k)]))))

(defn array-add-stragglers [a]
  "unpack an array of maps, add stragglers, and repack into array"
  (apply array (map add-stragglers a)))

; patch the return value of d3.mouse to provide clj values
(defn patch-mouse []
  (patch-fn1-return-value d3 "mouse"))

; this should probably be in an init call or something
(patch-known-arrayish-types)
