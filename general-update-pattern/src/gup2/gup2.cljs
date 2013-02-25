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
  (let [text (-> svg (.selectAll "text") (.data data identity))]
    ; UPDATE
    ; Update old elements as needed
    (-> text (.attr {:class "update"}))

    ; ENTER
    ; Create new elments as needed
    (-> text (.enter) (.append "text")
      (.attr {:class "enter"
              :dy    ".35em"})
      (.text identity))

    ; ENTER + UPDATE
    ; Appending to the enter selection expands the update selection to include
    ; entering elements; so, operations on the update selection after appending to
    ; the enter selection will apply to both entering and updating nodes.
    (-> text (.attr {:x #(* %2 32)}))

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
