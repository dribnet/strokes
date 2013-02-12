(ns clocky.clocky
  (:require [strokes :refer [d3]]))

(strokes/bootstrap)

(def Tau (* 2 Math/PI))

(def radii
  "radius of each concentric circle"
  {:hours 275, :minutes 200, :seconds 110, :millis, 30})

(def arc
  "returns function capabable of generating arcs from datums"
  (.. d3 -svg arc
    (startAngle #(/ (* (:value %) Tau) 100))
    (endAngle #(/ (* (+ 50 (:value %)) Tau) 100))
    (innerRadius 0)
    (outerRadius #((keyword (:key %)) radii))))

(defn curClockData []
  "generates an array of 8 datums, one for each arc to draw"
  (let [d       (js/Date.)
        hours   (/ (* (rem (.getHours d) 12) 100) 12)
        minutes (/ (* (.getMinutes d) 100) 60)
        seconds (/ (* (.getSeconds d) 100) 60)
        millis  (/ (* (.getMilliseconds d) 100) 1000)]
    [ {:value hours, :key "hours", :which 1},
      {:value (+ hours 50), :key "hours", :which 2},
      {:value minutes, :key "minutes", :which 1},
      {:value (+ minutes 50), :key "minutes", :which 2},
      {:value seconds, :key "seconds", :which 1},
      {:value (+ seconds 50), :key "seconds", :which 2},
      {:value millis, :key "millis", :which 1},
      {:value (+ millis 50), :key "millis", :which 2} ]))

(let [root 
        (.. d3 (select "#clocky") (append "svg")
            (attr "width" 600)
            (attr "height" 600)
            (append "g")
            (attr "transform" "translate(300,300)"))
      rings
        (.. root (selectAll "g") (data curClockData))]

    ; side effect = a path for each data element returned from curClockData
    (.. rings (enter) (append "g") (append "path"))

    ; this timer loops forever, rebinding via curClockData
    (strokes/timer (fn []
      (.. root (selectAll "g") (data curClockData)
          (select "path")
          (attr "class" #(str (:key %) (:which %)))
          (attr "d", arc))
      ; our work is never done
      false)))
