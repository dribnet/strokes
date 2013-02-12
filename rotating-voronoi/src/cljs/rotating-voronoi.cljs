(ns rotating-voronoi
  (:require [clojure.string :refer [join]]
            [strokes :refer [d3 timer polygon voronoi]]))

(strokes/bootstrap)

; debug-print
(defn dp [& args]
  (.log js/console (apply str args)))

(def width 960)
(def height 500)

; points is an atom holding a vector of atoms of vectors
; (it's not as bad as it sounds, see get-points below)
(def points (atom []))

; take a snapshot of the state of points into an array
(defn get-points [& args]
  ; todo: how can we force vector not to be lazy?
  ; (apply vector (doall (map deref @points))))
  (apply array (map deref @points)))

(def bounds (polygon [
      [(/ (- width) 2) (/ (- height) 2)]
      [(/ (- width) 2) (/ (+ height) 2)]
      [(/ (+ width) 2) (/ (+ height) 2)]
      [(/ (+ width) 2) (/ (- height) 2)] ]))

(def circles [
    [  0      0 120 46 (- 0.001)]
    [  0      0  30 10 (+ 0.03)]
    [  0      0  60  3 (- 0.05)]
    [  0      0  15  4 (- 0.02)]
    [  0      0   0  1 (- 0.02)]
    [240 (- 120) 80  4 (- 0.02)]
    [240 (- 120)  0  1 (- 0.02)]
    [280 (+ 120) 40  8 (+ 0.02)]
    [280 (+ 120) 20  8 (- 0.02)]
    ; [280 (+ 120)  0  1 (+ 0.02)]
  ])

(def τ (* 2 Math/PI))

(defn launch-circle [[cx cy r n δθ]]
  ; internal launch-pt fn with closure around args
  (let [launch-pt 
      (fn [θ]
        (let [init-pt [(+ cx (* r (Math/cos θ))) (+ cy (* r (Math/sin θ)))]
              cur-pt (atom init-pt)
              cur-θ (atom θ)]
          (-> d3 (.timer (fn []
            (swap! cur-θ + δθ)
            (reset! cur-pt [(+ cx (* r (Math/cos @cur-θ))) (+ cy (* r (Math/sin @cur-θ)))])
            false)))
          (swap! points conj cur-pt)))]
    (dorun (map launch-pt (range 1e-6 τ (/ τ n))))))

; kick off those circles already
(dorun (map launch-circle circles))

; function to make an svg line
; (def line (.. d3 -svg line (interpolate "linear-closed")))
(def line (.. d3 -svg line (interpolate "basis-closed")))

(def svg (.. d3 (select "body") (append "svg")
      (attr "width" width)
      (attr "height" height)
    (append "g")
      (attr "transform" #(str "translate(" (/ width 2) "," (/ height 2) ")"))))

(def path (.. svg (selectAll "path")
      (data get-points)
    (enter)
      (append "path")))

(def circles (.. svg (selectAll "circle")
      (data get-points)
    (enter)
      (append "circle")
      (attr "transform" #(str "translate(" % ")"))
      (attr "r" 1.2)))

(defn two-polate [[[x0 y0] [x1 y1]]]
  [ [x0 y0]
    [(/ (+ x0 x1) 2), (/ (+ y0 y1) 2)] ])

(defn three-polate [[[x0 y0] [x1 y1]]]
  [ [x0 y0]
    [(/ (+ (* 2 x0) x1) 3), (/ (+ (* 2 y0) y1) 3)] 
    [(/ (+ (* 2 x1) x0) 3), (/ (+ (* 2 y1) y0) 3)] ]) 

(defn resample [pts]
  (let [pairs (map vector (cons (last pts) pts) pts)]
    (mapcat three-polate pairs)))

(timer (fn []
  (let [pts (get-points)
        vnoi (map #(.clip bounds %) (voronoi pts))]
    (-> circles
      (.attr "transform" #(str "translate(" (nth pts %2) ")")))
    (-> path (.attr "d" (fn [pt i] (line (resample (nth vnoi i)))))))
  false))
