(ns strokes
  (:require [mrhyde :refer [patch-known-arrayish-types 
                            patch-known-mappish-types 
                            patch-return-value-to-clj
                            patch-args-keyword-to-fn]]
            [clojure.string :refer [join]]
            [cljs.reader :refer [read-string]]))

(def d3 (this-as ct (aget ct "d3")))

; add a new d3.edn call to pull edn data over the network (like d3.csv and d3.json)
(defn- d3-edn
  ([url callback]
    (d3-edn url nil callback))
  ([url mime callback]
    (letfn [(ready [req] (callback (if req  (read-string (.-responseText req)) req)))]
      (.log js/console (str "loading: " url))
      (.xhr d3 url mime ready))))

(if d3 (do
  ; patch all seqs to also be read-only arrays for javascript interop
  (patch-known-arrayish-types)
  ; patch maps to include key based accessors on js object
  (patch-known-mappish-types)
  ; filter d3.selection.attr inputs: v might be keyword function
  (patch-args-keyword-to-fn (-> d3 .-selection .-prototype) "attr" 1)
  ; filter d3.layout.pack.value inputs: v might be keyword function
  (patch-args-keyword-to-fn (-> d3 .-layout .-pack .-prototype) "value" 0)
  ; have mouse return cljs data structure
  (patch-return-value-to-clj d3 "mouse")

  ; todo: new patch here - something like:
  ; (patch-return-value-recurse-from-cache (-> d3 .-layout .-pack .-prototype) "nodes" 0)

  (-> d3 .-edn (set! d3-edn)) 
))

; stragglers is still a work in progress...

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
