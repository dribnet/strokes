(ns zip-decode
  (:require [strokes :refer [d3]]))

(strokes/bootstrap)

; (def width 960)
; (def height 500)
(def width 1200)
(def height 600)

; debug helper
(defn dp [& args]
  (.log js/console (apply str args)) )

(def proj-fn (-> d3 .-geo .albersUsa (.scale 1200)
  (.translate [ (/ width 2) (/ height 2)])))
(def path-fn (-> d3 .-geo .path (.projection proj-fn)))

(def status-node (-> d3 (.select "#status")))

(def svg (-> d3 (.select "#map") (.append "svg")
    (.attr {:width width
            :height height})))

(defn render [maproot ziproot]
  ; clear "loading" text
  (-> status-node (.remove))

  (-> svg (.append "g") (.attr "id" "states") (.selectAll "path")
      (.data (aget maproot "features"))
    (.enter)
      (.append "path")
      (.attr "d" path-fn))

  ; (-> svg (.append "g") (.attr "id" "zipdots") (.selectAll "line")
  ;     (.data ziproot)
  ;   (.enter)
  ;     (.append "line")
  ;     (.attr {:x #(first  (proj-fn [(.-lon %) (.-lat %)]))
  ;             :y #(second (proj-fn [(.-lon %) (.-lat %)]))
  ;             :class "unselected"}))

  ; (-> svg (.append "g") (.attr "id" "zipdots") (.selectAll "line")
  ;     (.data ziproot)
  ;   (.enter)
  ;     (.append "line")
  ;     (.each (fn [d i]
  ;       (this-as t
  ;         (let [[x y] (proj-fn [(.-lon d) (.-lat d)])]
  ;           (-> d3 (.select t)
  ;             (.attr 
  ;               {:x1 x
  ;                :y1 y
  ;                :x2 (+ x 1)
  ;                :y2 y
  ;                :class "unselected"})))))))

  (-> svg (.append "g") (.attr "id" "zipdots") (.selectAll "text")
      (.data ziproot)
    (.enter)
      (.append "text")
      (.text ".")
      (.attr {:x #(first  (proj-fn [(.-lon %) (.-lat %)]))
              :y #(second (proj-fn [(.-lon %) (.-lat %)]))
              :class "unselected"}))

  ; (-> svg (.append "g") (.attr "id" "zipdots") (.selectAll "rect")
  ;     (.data ziproot)
  ;   (.enter)
  ;     (.append "rect")
  ;     (.attr {:x #(first  (proj-fn [(.-lon %) (.-lat %)]))
  ;             :y #(second (proj-fn [(.-lon %) (.-lat %)]))
  ;             :width 1
  ;             :height 1
  ;             :class "unselected"}))
  )

(-> d3 (.json "us-states.geojson" (fn [error1, maproot]
  (-> d3 (.tsv "zips.tsv" (fn [error2, ziproot]
    (if-let [error (or error1 error2)]
      (-> status-node (.html (aget error "response")))
        (render maproot ziproot)
      )))))))
