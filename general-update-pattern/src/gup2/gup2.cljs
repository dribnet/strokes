(ns gup2
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
  (let [text (-> svg (.selectAll "text") (.data data))]
    ; UPDATE
    ; Update old elements as needed
    (-> text (.attr {:class "update"}))

    ; ENTER
    ; Create new elments as needed
    (-> text (.enter) (.append "text")
      (.attr {:class "enter"
              :x     #(* %2 32)
              :dy    ".35em"}))

    ; ENTER + UPDATE
    ; Appending to the enter selection expands the update selection to include
    ; entering elements; so, operations on the update selection after appending to
    ; the enter selection will apply to both entering and updating nodes.
    (-> text (.text identity))

    ; EXIT
    ; Remove old elements as needed.
    (-> text (.exit) (.remove))))

; The initial display - all letters
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

; (-> svg (.append "circle")
;       (.attr {:cx 350 :cy 200 :r 200 :class "left"}))

; (-> svg (.append "circle")
;       (.attr {:cx 550 :cy 200 :r 200 :class "right"}))

; (-> svg (.append "circle")
;       (.attr {:cx 450 :cy 300 :r 200 :class "bottom"}))
