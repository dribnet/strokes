(ns voronoi-example
  (:require [clojure.string :refer [join]]
            [strokes :refer [d3]]))

(strokes/bootstrap)

(def padding 10)
(def width 960)
(def height 500)

(def points [[200 200]
             [760 300]])

(def bounds (-> d3 .-geom (.polygon [
      [padding, padding]
      [padding, (- height padding)]
      [(- width padding), (- height padding)]
      [(- width padding), padding]])))

(def colorfn (-> d3 .-scale .category10))

(def svg (-> d3 (.select "body") (.append "svg")
      (.attr "width" width)
      (.attr "height" height)))

(-> svg (.selectAll "path")
      ; todo: could probably replace with clj map call
      (.data (-> d3 .-geom (.voronoi points) (.map #(.clip bounds %))))
    (.enter)
      (.append "path")
      (.style "fill" #(colorfn %2))
      (.attr "d" #(str "M" (join "L" %) "Z")))

(-> svg (.selectAll "circle")
      (.data points)
    (.enter)
      (.append "circle")
      (.style "fill" #(colorfn %2))
      (.attr "transform" #(str "translate(" % ")"))
      (.attr "r" 5))
