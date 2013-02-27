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
(.focus (.getElementById js/document "input"))
(def input-node (-> d3 (.select "#input")))

(def zipdots-sel (atom nil))

(defn update-selection [s]
  (let [len (count s)]
    (-> @zipdots-sel (.attr "class" (fn [d]
      (let [zip (aget d "zip")
            sub (.substr zip 0 len)]
        (if (and (> len 0) (= s sub))
          "selected"
          "unselected")))))))

(defn key-fn []
  (if @zipdots-sel
    (update-selection (-> input-node (.text)))))

(-> input-node (.on "keyup" key-fn))

(def svg (-> d3 (.select "#map") (.append "svg")
    (.attr {:width width
            :height height})))

(defn first-render [maproot ziproot]
  ; clear "loading" text
  (-> status-node (.remove))

  (-> svg (.append "g") (.attr "id" "states") (.selectAll "path")
      (.data (aget maproot "features"))
    (.enter)
      (.append "path")
      (.attr "d" path-fn))

  (reset! zipdots-sel
    (-> svg (.append "g") (.attr "id" "zipdots") (.selectAll "text")
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
