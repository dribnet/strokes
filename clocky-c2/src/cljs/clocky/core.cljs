(ns clocky.core
  (:use-macros [c2.util :only [bind!]])
  (:use [c2.core :only [unify]]
        [c2.maths :only [Tau Pi]]
        [c2.svg :only [arc]])
  (:require [c2.scale :as scale]
            [goog.dom :as dom]))

(def clock-atom
  "clock data bound to atom. each number is a percentage."
  (atom {:hours 50, :minutes 60, :seconds 70, :millis 80}))

(def radii {:hours 275, :minutes 200, :seconds 110, :millis, 30})

(bind! "#clocky" 
  [:svg
    [:g {:transform "translate(300,300)rotate(-90)"}
      (doall (for [k [:hours :minutes :seconds :millis]]
        (let [v      (get @clock-atom k)
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
                            :end-angle   angle3)}]])))]])

(defn nextloop []
  (let [d (js/Date.)]
    (reset! clock-atom 
      {:hours   (/ (* (rem (.getHours d) 12) 100) 12)
       :minutes (/ (* (.getMinutes d) 100) 60)
       :seconds (/ (* (.getSeconds d) 100) 60)
       :millis  (/ (* (.getMilliseconds d) 100) 1000)})))

(defn animation-loop []
  (.requestAnimationFrame (dom/getWindow) animation-loop)
  (nextloop))

(animation-loop)