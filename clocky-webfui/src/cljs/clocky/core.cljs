(ns clocky.core
  (:use [webfui.framework :only [launch-app]]
        [c2.maths :only [Tau Pi]]
        [c2.svg :only [arc]])
  (:use-macros [webfui.framework.macros :only [add-dom-watch]])
  (:require [goog.dom :as dom]))

(def radii
  "radius of each concentric circle"
  {:hours 275, :minutes 200, :seconds 110, :millis, 30})

(defn render-all [state]
  "webfui callback to rerender clock when global state changes"
  [:svg
    [:g {:transform "translate(300,300)rotate(-90)"}
      ; (state is map where each field is a percentage)
      (for [k [:hours :minutes :seconds :millis]]
        (let [v      (get state k)
              angle1 (/ (* Tau v) 100)
              angle2 (+ angle1 Pi)
              angle3 (+ angle2 Pi)
              radius (get radii k)]
          [:g.slice
            [:path {:class (str (name k) "1")
                    :d (arc :outer-radius radius
                            :start-angle angle1
                            :end-angle   angle2)}]
            [:path {:class (str (name k) "2")
                    :d (arc :outer-radius radius
                            :start-angle angle2
                            :end-angle   angle3)}]]))]])

(defn update-clock [clock-atom]
  "updates atom with new percentage values based on js/Date fields"
  (let [d (js/Date.)]
    (reset! clock-atom
      {:hours   (/ (* (rem (.getHours d) 12) 100) 12)
       :minutes (/ (* (.getMinutes d) 100) 60)
       :seconds (/ (* (.getSeconds d) 100) 60)
       :millis  (/ (* (.getMilliseconds d) 100) 1000)})))

; make a clock-atom, monitor it with svg, and then keep updating it
; (using let just to emphasize locality of atom here)
(let [clock-atom (atom {:hours 0, :minutes 25, :seconds 50, :millis 75})]
  ; this kicks off monitoring the clock-atom to trigger re-rendering
  (launch-app clock-atom render-all)
  ; and this is what triggers infinite updates of the atom
  ;; TODO: - determine if this style is more idiomatic than idiotic...
  ((fn animation-loop[]
    (.requestAnimationFrame (dom/getWindow) animation-loop)
    (update-clock clock-atom))))
