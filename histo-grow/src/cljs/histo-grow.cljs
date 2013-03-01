(ns histo-grow
  (:require [strokes :refer [d3]]))

; debug helper
(defn dp [& args]
  (.log js/console (apply str args)) )

(strokes/bootstrap)

(defn values[]
  (vec (map (partial * 100)
    (take 1000 (repeatedly 
      ; (-> d3 .-random (.logNormal)))))))
      #(.random js/Math))))))
      ; (-> d3 .-random (.normal)))))))
      ; (-> d3 .-random (.irwinHall 10)))))))

(def format-count (-> d3 (.format ",.0f")))

(def margin {:top 10 :right 30 :bottom 30 :left 30})
(def width  (- 960 (:left margin) (:right margin)))
(def height (- 500 (:top margin)  (:bottom margin)))

(def svg (-> d3 (.select "body") (.append "svg")
      (.attr {:width  (+ width (:left margin) (:right margin))
              :height (+ height (:top margin) (:bottom margin))})
    (.append "g")
      (.attr "transform" (str "translate(" (:left margin) "," 
                            (:top margin) ")"))))

(-> svg (.append "g")
  (.attr {:class "x axis"
          :transform (str "translate(0," height ")")}))

(def highest-y (atom 0))

(defn update [values]
  (let [[x1 x2] ((juxt #(apply min %) #(apply max %)) values)
        slop  (* (- x2 x1) 0.1)
        [xmin xmax] [(- x1 slop) (+ x2 slop)]
        x     (-> d3 .-scale (.linear)
                (.domain [xmin xmax])
                (.range  [0 width]))

        x-axis (-> d3 .-svg (.axis)
                 (.scale x)
                 (.orient "bottom"))

        data  ((-> d3 .-layout (.histogram)
                    (.bins 50))
                values) ; tricky function call

        ymax  (apply max (map #(aget % "y") data))
        ystop (if (> ymax @highest-y) (reset! highest-y ymax) @highest-y)
        y     (-> d3 .-scale (.linear)
                (.domain [0 ystop])
                (.range [height 0]))

        bar   (-> svg (.selectAll ".bar")
                  (.data data #(identity %2)))

        newbs (-> bar (.enter) (.append "g"))

        bar-map {:class "bar"
                 :transform 
                   #(str "translate(" (x (aget % "x")) "," 
                                      (y (aget % "y")) ")")}

        bar-width (x (+ xmin (aget (first data) "dx")))

        rect-map {:x 1
                  :width (- bar-width 1)
                  :height #(- (+ 0 height) (y (aget % "y")))}]

    ; only append on the enter selection
    (-> newbs (.attr bar-map))
    (-> newbs (.append "rect")
      (.transition)
        (.duration 250) 
        (.attr rect-map))
    (-> newbs (.append "text"))

    (-> bar
      (.transition)
        (.duration 250) 
        (.attr bar-map))

    ; update others
    (-> bar (.select "rect")
      (.transition)
        (.duration 250)
        (.attr rect-map))

    (-> bar (.select "text")
      (.attr {:dy ".75em"
              :y  6
              :x  (/ bar-width 2)
              :text-anchor "middle"})
      (.text #(format-count (aget % "y"))))

    (-> bar (.exit)
      (.transition)
        (.duration 250)
        (.style {:fill-opacity 1e-6})
        (.remove))

    (-> svg (.select ".x.axis")
      (.transition)
        (.duration 250)
        (.call x-axis))
))

(def summed-values (atom (values)))
(update @summed-values)

(.setInterval js/window (fn []
  (swap! summed-values #(vec (map + % (values))))
  ; (swap! summed-values #(vec (concat % (values))))
  (update @summed-values))
  ; 2 seconds between swaps
  2000)