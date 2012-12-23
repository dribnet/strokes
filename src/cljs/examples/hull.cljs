(ns examples.hull
  (:use [clojure.string :only [join]]
        [strokes.core :only [d3 Tau]]))

(def width 960)
(def height 500)

; RandThought: Why can't .random return .-random? (autopopulate)
(def randomX (-> d3 .-random (.normal (/ width 2) 60)))
(def randomY (-> d3 .-random (.normal (/ height 2) 60)))

; this holds on to the mutable state of the vertex array
(def vert-atom
  (atom
    (for [x (range 100)]
      [(randomX) (randomY)])))

; drefs and converts the vector of vector to an array of arrays for d3 consumption
(defn vert-array []
  (apply array (map #(apply array %) @vert-atom)))

; forward declaration of redraw function, which sadly can't be called 'redraw'
(def redrawhull)

(def svg
  (-> d3 (.select "body") (.append "svg")
    (.attr "width" width)
    (.attr "height" height)
;    (.on "click" #(.log js/console (.mouse d3 js/this)))
    (.on "click" #(do (swap! vert-atom conj [100 100]) (redrawhull)))
    ))

; draw a border
(-> svg (.append "rect")
    (.attr "width" width)
    (.attr "height" height))

(def hull
  (-> svg (.append "path")
      (.attr "class" "hull")))

(def circle 
  (atom (.selectAll svg "circle")))

(defn redrawhull[]
  (let [verts (vert-array)]
    (-> (.datum hull 
      (-> d3 .-geom (.hull verts)))
        (.attr "d" #(str "M" (join "L" %) "Z")))
    (swap! circle #(.data % verts))
    (-> @circle .enter (.append "circle")
      (.attr "r" 3))
    (-> @circle
      (.attr "transform" #(str "translate(" % ")")))))

(defn ^:export launch []
  (redrawhull)
  (.log js/console (vert-array))
  ; (redrawhull)
  true)
