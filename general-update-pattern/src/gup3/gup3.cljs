(ns gup3
  (:require [strokes :refer [d3]]))

(strokes/bootstrap)

; 26 characters in a vec
(def alphabet (vec "abcdefghijklmnopqrstuwvxyz"))

(def width 960)
(def height 500)

(def svg (-> d3 (.select "body") (.append "svg")
      (.attr {:width width :height height})
    (.append "g")
      (.attr {:transform (str "translate(32," (/ height 2) ")")})))

(defn update [data]
  ; DATA JOIN
  ; Join new data with old elements, if any.
  (let [text (-> svg (.selectAll "text") (.data data identity))]
    ; UPDATE
    ; Update old elements as needed
    (-> text (.attr {:class "update"})
      (.transition)
        (.duration 750)
        (.attr {:x #(* %2 32)}))

    ; ENTER
    ; Create new elments as needed
    (-> text (.enter) (.append "text")
        (.attr {:class "enter"
                :x     #(* %2 32)
                :y     -60
                :dy    ".35em"})
        (.style {:fill-opacity 1e-6})
        (.text identity)
      (.transition)
        (.duration 750)
        (.attr {:y 0})
        (.style {:fill-opacity 1}))

    ; EXIT
    ; Remove old elements as needed.
    (-> text (.exit)
        (.attr {:class "exit"})
      (.transition)
        (.duration 750)
        (.attr {:y 60})
        (.style {:fill-opacity 1e-6})
        (.remove))))

; The initial display - all 26 letters
(update alphabet)

; Grab a random sample of letters from the alphabet, in alphabetical order.
(.setInterval js/window (fn []
  (-> alphabet
    shuffle
    (subvec (rand-int 26))
    sort
    vec
    update))
  ; 2 seconds between swaps
  2000)
