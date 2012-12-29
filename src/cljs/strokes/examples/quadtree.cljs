(ns strokes.examples.quadtree
  (:use [clojure.string :only [join]]
        [strokes.core :only [d3]]
        [strokes.mrhyde :only [patch-map]]))

(def width 960)
(def height 500)

; create root svg element
(defn gen-svg []
  (-> d3 (.select "body") (.append "svg")
    (.attr "width" width)
    (.attr "height" height)))

; generate some random data for the quadtree
(defn gen-data []
  (for [x (range 2500)]
    (patch-map {:x (rand width), :y (rand height)})))

; this works, but is crazy-slow
; (defn nodes [quadtree]
;   (let [sidecar (atom [])]
;     (.visit quadtree (fn [n, x1, y1, x2, y2]
;       (swap! sidecar conj {:x x1, :y y1, :width (- x2 x1), :height (- y2 y1)})
;       ; return false to keep processing...
;       false))
;     ; return the accumulated side effects
;     @sidecar))

; gets a seq of all nodes. (the quadtree.visit api seems to beg for this side effecty impl)
(defn nodes [quadtree]
  (let [sidecar (array)]
    (.visit quadtree (fn [n, x1, y1, x2, y2]
      (.push sidecar {:x x1, :y y1, :width (- x2 x1), :height (- y2 y1)})
      ; return false to keep processing...
      false))
    ; return the accumulated side effects
    (seq sidecar)))

; todo
(defn search [quadtree, x0, y0, x3, y3]
  nil)

; external hook to launch this demo
(defn ^:export launch []
  ;(.log js/console (vert-array))
  (let [svg (gen-svg)
        data (gen-data)
        point (atom nil)
        quadtree (atom nil)]

    (reset! quadtree 
      (-> d3 .-geom 
        (.quadtree data -1 -1 (+ width 1) (+ height 1))))

    (-> svg (.selectAll ".node")
        (.data (nodes @quadtree))
      (.enter) (.append "rect")
        (.attr "class" "node")
        (.attr "x" #(:x %))
        (.attr "y" #(:y %))
        (.attr "width" #(:width %))
        (.attr "height" #(:height %)))

    (reset! point (-> svg (.selectAll ".point")
        (.data data)
      (.enter) (.append "circle")
        (.attr "class" "point")
        (.attr "cx" #(:x %))
        (.attr "cy" #(:y %))
        (.attr "r" 4)))
))