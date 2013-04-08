(ns strokes
  (:require [mrhyde.core :as mrhyde]
            [mrhyde.funpatcher
               :refer [patch-tostring-hydearray-is-array
                       patch-return-value-to-clj
                       patch-args-recurse-from-cache
                       patch-args-map-to-obj
                       patch-args-keyword-to-fn]]
            [cljs.reader :refer [read-string]]))

(def d3 (this-as ct (aget ct "d3")))

(defn edn-parser-callback [request]
  (read-string (.-responseText request)))

(defn fetch-edn [url callback]
  (-> d3 (.xhr url "application/octet-stream" callback) (.response edn-parser-callback)))

(defn ^:export bootstrap [& opts]
  (mrhyde/bootstrap)

  (if d3 (do
    ; filter d3.selection.attr inputs: v might be keyword function
    (patch-args-keyword-to-fn (-> d3 .-selection .-prototype) "attr" 1)
    ; filter d3.selection.text inputs: argument mighe be a keyword function
    (patch-args-keyword-to-fn (-> d3 .-selection .-prototype) "text" 0)
    ; filter d3.layout.pack.value inputs: v might be keyword function
    (patch-args-keyword-to-fn (-> d3 .-layout .-pack .-prototype) "value" 0)
    ; filter d3.layout.pack.value inputs: v might be keyword function
    (patch-args-keyword-to-fn (-> d3 .-layout .-pack .-prototype) "children" 0)
    ; filter d3.selection.data inputs: keyfn might be keyword function
    (patch-args-keyword-to-fn (-> d3 .-selection .-prototype) "data" 1)
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
