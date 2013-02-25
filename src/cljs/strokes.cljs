(ns strokes
  (:require [mrhyde.typepatcher
               :refer [patch-known-vector-types
                       patch-known-sequential-types 
                       patch-known-mappish-types]]
            [mrhyde.funpatcher
               :refer [patch-tostring-hydearray-is-array
                       patch-return-value-to-clj
                       patch-args-recurse-from-cache
                       patch-args-map-to-obj
                       patch-args-keyword-to-fn]]
            [clojure.string :refer [join]]
            [cljs.reader :refer [read-string]]))

(def d3 (this-as ct (aget ct "d3")))

(defn edn-parser-callback [request]
  (read-string (.-responseText request)))

(defn fetch-edn [url callback]
  (-> d3 (.xhr url "application/octet-stream" callback) (.response edn-parser-callback)))

(defn ^:export bootstrap [& opts]
  ; patch all seqs to also be read-only arrays for javascript interop
  (patch-known-vector-types)
  ; (patch-known-sequential-types)
  ; patch maps to include key based accessors on js object
  (patch-known-mappish-types)

  (if d3 (do
    ; filter d3.selection.attr inputs: v might be keyword function
    (patch-args-keyword-to-fn (-> d3 .-selection .-prototype) "attr" 1)
    ; filter d3.selection.text inputs: argument mighe be a keyword function
    (patch-args-keyword-to-fn (-> d3 .-selection .-prototype) "text" 0)
    ; filter d3.layout.pack.value inputs: v might be keyword function
    (patch-args-keyword-to-fn (-> d3 .-layout .-pack .-prototype) "value" 0)
    ; filter d3.layout.pack.value inputs: v might be keyword function
    (patch-args-keyword-to-fn (-> d3 .-layout .-pack .-prototype) "children" 0)

    ; have mouse return cljs data structure
    ; (patch-return-value-to-clj d3 "mouse")
  ))
)

; let's add a bunch of handy d3 *fields* here
(if d3 (do 
  (def timer (.. d3 -timer))
  (def arc (.. d3 -svg -arc))
  (def polygon (.. d3 -geom -polygon))
  (def voronoi (.. d3 -geom -voronoi))
  (def category10 (.. d3 -scale -category10))
  (def category20 (.. d3 -scale -category20))
  (def category20b (.. d3 -scale -category20b))
  (def category20c (.. d3 -scale -category20c))
))
