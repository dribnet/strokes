(ns strokes.examples.hull
  (:use [clojure.string :only [join]]
        [strokes.core :only [d3 Tau patch-vector]]))

(def width 960)
(def height 500)
(def centerpoint (patch-vector [(/ width 2) (/ height 2)]))

; RandThought: Why can't .random return .-random? (autopopulate)

; two functions that return random normal distributions
(def randomX (-> d3 .-random (.normal (/ width 2) 60)))
(def randomY (-> d3 .-random (.normal (/ height 2) 60)))

; this holds on to the (global) mutable state of the vertex array
(def vert-atom
  (atom
    ; init to 20 random points with the centerpoint at the end
    (conj
      (for [x (range 20)]
        (patch-vector [(randomX) (randomY)]))
      centerpoint)))

; derefs and converts the vector of vector to an array of arrays for d3 consumption
(defn vert-array []
  (apply array @vert-atom))

; create root svg element
(defn gen-svg []
  (-> d3 (.select "body") (.append "svg")
    (.attr "width" width)
    (.attr "height" height)))

; draw a border
(defn draw-border [svg]
  (-> svg (.append "rect")
      (.attr "width" width)
      (.attr "height" height)))

; create hull element
(defn gen-hull [svg]
  (-> svg (.append "path")
      (.attr "class" "hull")))

; redraw hull and points. called after any changes to vert-atom
(defn redraw-hull[hull circle]
  (let [verts (vert-array)]
    (-> (.datum hull (-> d3 .-geom (.hull verts)))
        (.attr "d" #(str "M" (join "L" %) "Z")))
    (swap! circle #(.data % verts))
    (-> @circle .enter (.append "circle")
      (.attr "r" 3))
    (-> @circle
      (.attr "transform" #(str "translate(" % ")")))))

; memo: http://en.wikipedia.org/wiki/DOM_events
(defn add-mouse-callbacks [svg hull circle]
  (-> svg
    ; with mousemove, replace last element with mouse position
    (.on "mousemove" (fn [] 
      (this-as t (let [pt (patch-vector (js->clj (.mouse d3 t)))]
        (swap! vert-atom #(conj (rest %) pt))
        (redraw-hull hull circle)))))
    ; with click, replace last element with mouse posistion x2
    (.on "click" (fn [] 
      (this-as t (let [pt (patch-vector (js->clj (.mouse d3 t)))]
        (swap! vert-atom #(conj (rest %) pt pt))
        (redraw-hull hull circle)))))
    ; when exiting, replace last element with centerpoint
    (.on "mouseout" (fn [] 
      (swap! vert-atom #(conj (rest %) centerpoint))
      (redraw-hull hull circle)))))

; external hook to launch this demo
(defn ^:export launch []
  ;(.log js/console (vert-array))
  (let [svg (gen-svg)
        hull (gen-hull svg)
        circle (atom (.selectAll svg "circle"))]
    (draw-border svg)
    (add-mouse-callbacks svg hull circle)
    (redraw-hull hull circle)))
