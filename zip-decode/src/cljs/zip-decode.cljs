(ns zip-decode
  (:require [strokes :refer [d3]]))

(strokes/bootstrap)

; (def width 960)
; (def height 500)
(def width 1200)
(def height 600)

(def svg (-> d3 (.select "#map") (.append "svg")
    (.attr {:width width
            :height height})))

(def draw-root (-> svg (.append "g")))
(def debug-rect (-> draw-root (.append "rect") (.attr "class" "debug")))

; debug helper
(defn dp [& args]
  (.log js/console (apply str args)) )

(def proj-fn (-> d3 .-geo .albersUsa (.scale 1200)
  (.translate [ (/ width 2) (/ height 2)])))
(def path-fn (-> d3 .-geo .path (.projection proj-fn)))

(def status-node (-> d3 (.select "#status")))
(.focus (.getElementById js/document "input"))
(def input-node (-> d3 (.select "#input")))

(def zipdots-sel (atom nil))

(defn reset-zoom []
 (-> draw-root (.transition) (.duration 1500) 
          (.attr "transform" ""))) 

(defn re-zoom []
  (let [dots (-> draw-root (.selectAll "text.selected"))
        lats   (array)
        lons   (array)]

    (-> dots (.each (fn [d]
      ; (.log js/console d)
        (.push lats (aget d "lat"))
        (.push lons (aget d "lon"))
      )))

    (if-let [ct (count lats)]
      (let [min-lat (reduce min lats)
            max-lat (reduce max lats)
            min-lon (reduce min lons)
            max-lon (reduce max lons)
            [x1 y1] (proj-fn [min-lon min-lat])
            [x2 y2] (proj-fn [max-lon max-lat])
            [xmin xmax] (if (< x1 x2) [x1 x2] [x2 x1])
            [ymin ymax] (if (< y1 y2) [y1 y2] [y2 y1])]

        (dp "bounds: " min-lat "," min-lon " - " max-lat "," max-lon)
        (dp "proj: " xmin "," ymin " - " xmax "," ymax)

        ; (-> debug-rect (.attr {
        ;   :x xmin
        ;   :y ymin
        ;   :width (- xmax xmin)
        ;   :height (- ymax ymin)
        ;   }))
        ; ; TODO: zoom!
        ; (-> draw-root (.transition) (.duration 1500) 
        ;   (.attr "transform"
        ;     (str "translate(" (.translate proj-fn) ")"
        ;       "scale(" (/ 0.95 (max (/ (- xmax xmin) width)
        ;                             (/ (- ymax ymin) height))) ")"
        ;       "translate(" (/ (+ xmax xmin) 2) ","
        ;                    (/ (+ ymax ymin) 2) ")")
        ;     ))
      ))

    ; return true to stop
    true))

(defn update-selection [s]
  (let [len (count s)]
    (if (> len 0)
      (-> d3 (.timer re-zoom))
      (-> d3 (.timer reset-zoom)))
    (-> @zipdots-sel (.attr "class" (fn [d]
      (let [zip (aget d "zip")
            sub (.substr zip 0 len)]
        (if (and (> len 0) (= s sub))
          "selected"
          "unselected")))))))

; for another day - why is this slow and broken?
; (defn update-selection [s]
;   (let [len (count s)]
;     (-> @zipdots-sel (.each (fn [d]
;       (this-as t
;         (let [zip (aget d "zip")
;               sub (.substr zip 0 len)
;               hit (and (> len 0) (= s sub))]
;           (-> d3 (.select t) (.attr "class"
;             (if hit)
;               "selected"
;               "unselected")))))))))

(defn key-fn []
  (if @zipdots-sel
    (update-selection (-> input-node (.text)))))

(-> input-node (.on "keyup" key-fn))

(defn first-render [maproot ziproot]
  ; clear "loading" text
  (-> status-node (.remove))

  (-> draw-root (.append "g") (.attr "id" "states") (.selectAll "path")
      (.data (aget maproot "features"))
    (.enter)
      (.append "path")
      (.attr "d" path-fn))

  (reset! zipdots-sel
    (-> draw-root (.append "g") (.attr "id" "zipdots") (.selectAll "text")
        (.data ziproot)
      (.enter)
        (.append "text")
        (.text ".")
        (.attr {:x #(first  (proj-fn [(.-lon %) (.-lat %)]))
                :y #(second (proj-fn [(.-lon %) (.-lat %)]))
                :class "unselected"}))))


(-> d3 (.json "us-states.geojson" (fn [error1, maproot]
  (-> d3 (.tsv "zips.tsv" (fn [error2, ziproot]
    (if-let [error (or error1 error2)]
      (-> status-node (.html (aget error "response")))
        (first-render maproot ziproot)
      )))))))
