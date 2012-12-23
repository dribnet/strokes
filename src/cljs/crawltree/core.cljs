(ns crawltree.core
  (:use [strokes.core :only [d3 array-add-stragglers]]))

(def w 1280)
(def h 800)
(def r 720)
(def x 
  (-> d3 .-scale (.linear) (.range 0 r)))
(def y
  (-> d3 .-scale (.linear) (.range 0 r)))
(def znode)
(def zroot)

(def pack
  (-> d3 .-layout .pack 
    (.children #(apply array (:children %)))
    (.size (array r r))
    (.value #(:size %))))

(defn genviz []
  (-> d3 (.select "body") (.insert "svg:svg" "h2")
    (.attr "width" w)
    (.attr "height" h)
    (.append "svg:g")
    (.attr "transform" (str "translate(" (/ (- w r) 2) "," (/ (- h r) 2) ")"))))

(defn ^:export launch []
  (-> d3 (.edn "flare.edn" 
    (fn [data]
      (let [zroot data
            jsnodes ((.-nodes pack) zroot)
            vis (genviz)
            nodes (array-add-stragglers jsnodes)]

        ; (.log js/console "---aaa---")
        ; (.log js/console nodes)
        ; (.log js/console (str nodes))
        ; (.log js/console "---bbb---")
        (-> vis (.selectAll "circle")
          (.data nodes)
          (.enter) (.append "svg:circle")
          (.attr "class" "parent")
          (.attr "class", #(if (:children %) "parent" "child"))
          (.attr "cx" #(:x %))
          (.attr "cy" #(:y %))
          (.attr "r" #(:r %)))

        (-> vis (.selectAll "text")
          (.data nodes)
          (.enter) (.append "svg:text")
          (.attr "class", #(if (:children %) "parent" "child"))
          (.attr "x" #(:x %))
          (.attr "y" #(:y %))
          (.attr "dy" ".35em")
          (.attr "text-anchor" "middle")
          (.style "opacity" #(if (> (:r %) 20) 1 0))
          (.text #(:name %))))))))

; need an atom for the genviz?
; (defn ^:export zoom [d i]
;   (let [k (/ (/ r (.-r d)) 2)
;         t (-> vis (.transition) (.duration 
;             (if (-> d3 .-event .-altKey) 7500 750)))]

;     (.domain x (array [(- (.-x d) (.-r d)), (+ (.-x d) (.-r d))]))
;     (.domain y (array [(- (.-y d) (.-r d)), (+ (.-y d) (.-r d))]))

;     (-> t (.selectAll "circle")
;       (.attr "cx" #(x (.-x %)))
;       (.attr "cy" #(y (.-y %)))
;       (.attr "r" #(* k (.-r %))))

;     (-> t (.selectAll "text")
;       (.attr "x" #(x (.-x %)))
;       (.attr "y" #(y (.-y %)))
;       (.attr "opacity" #(if (> (* k (.-r %)) 20) 1 0)))))

