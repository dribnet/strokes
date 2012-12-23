(ns examples.hull
  (:use [clojure.string :only [join]]
        [strokes.core :only [d3 Tau]]))

(def width 960)
(def height 500)

; RandThought: Why can't .random return .-random? (autopopulate)
(def randomX (-> d3 .-random (.normal (/ width 2) 60)))
(def randomY (-> d3 .-random (.normal (/ height 2) 60)))
(def vertices
  (apply array 
    (for [x (range 100)]
      (apply array [(randomX) (randomY)]))))

(def svg
  (-> d3 (.select "body") (.append "svg")
    (.attr "width" width)
    (.attr "height" height)))

(-> svg (.append "rect")
    (.attr "width" width)
    (.attr "height" height))

(def hull
  (-> svg (.append "path")
      (.attr "class" "hull")))

(def circle 
  (atom (.selectAll svg "circle")))

(defn redrawhull[]
  (-> (.datum hull 
    (-> d3 .-geom (.hull vertices)))
      (.attr "d" #(str "M" (join "L" %) "Z")))
  (swap! circle #(.data % vertices))
  ;(swap! circle .data (apply array vertices))
  ;(swap! circle .data vertices)
  ;(swap! circle #(.data %))
  ;(-> d3 (.selectAll "circle") (.data vertices))
  (-> @circle .enter (.append "circle")
    (.attr "r" 3))
  (-> @circle
    (.attr "transform" #(str "translate(" % ")"))))

(defn ^:export launch []
  (redrawhull)
  (.log js/console (apply array vertices))
  ; (redrawhull)
  true)
