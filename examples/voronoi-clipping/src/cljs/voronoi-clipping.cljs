(ns voronoi-example
  (:require [clojure.string :refer [join]]
            [strokes :refer [d3 voronoi polygon category10]]))

(strokes/bootstrap)

(def padding 10)
(def width 960)
(def height 500)

(def points [[200 200]
             [760 300]])

(def vor-obj (-> (voronoi) (.clipExtent [
                [padding, padding]
                [(- width padding), (- height padding)
              ]])))

(def colorfn (category10))

(def svg (-> d3 (.select "body") (.append "svg")
      (.attr {:width width :height height})))

(-> svg (.selectAll "path")
      (.data (vor-obj points))
    (.enter)
      (.append "path")
      (.style "fill" #(colorfn %2))
      (.attr "d" #(str "M" (join "L" %) "Z")))

(-> svg (.selectAll "circle")
      (.data points)
    (.enter)
      (.append "circle")
      (.style "fill" #(colorfn %2))
      (.attr {:transform #(str "translate(" % ")")
              :r 5}))
