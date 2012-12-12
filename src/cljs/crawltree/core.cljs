(ns crawltree.core
  (:use [strokes.core :only [d3]]))

(def w 1280)
(def h 800)
(def r 720)
(def x 
  (-> d3 .-scale (.linear) (.range 0 r)))
(def y
  (-> d3 .-scale (.linear) (.range 0 r)))
(def znode)
(def zroot)

(.log js/console "----")

(def pack-edn
  (-> d3 .-layout .pack 
    (.children #(apply array (:children %)))
     ; (.children (fn [x] 
     ;      (apply array (:children x))
     ;  ))
    (.size (array r r))
    (.value #(:size %))))

; (def pack
;   (-> d3 .-layout .pack
;     (.size (array r r))
;     (.value #(.-size %))))

(.log js/console "----")

(def vis
  (-> d3 (.select "body") (.insert "svg:svg" "h2")
    (.attr "width" w)
    (.attr "height" h)
    (.append "svg:g")
    (.attr "transform" (str "translate(" (/ (- w r) 2) "," (/ (- h r) 2) ")"))))

; (defn js->clj
;   "Recursively transforms JavaScript arrays into ClojureScript
;   vectors, and JavaScript objects into ClojureScript maps.  With
;   option ':keywordize-keys true' will convert object fields from
;   strings to keywords."
;   [x & options]
;   (let [{:keys [keywordize-keys]} options
;         keyfn (if keywordize-keys keyword str)
;         f (fn thisfn [x]
;             (cond
;              (seq? x) (doall (map thisfn x))
;              (coll? x) (into (empty x) (map thisfn x))
;              (goog.isArray x) (vec (map thisfn x))
;              (identical? (type x) js/Object) (into {} (for [k (js-keys x)]
;                                                         [(keyfn k)
;                                                          (thisfn (aget x k))]))
;              :else x))]
;     (f x)))

; ; TODO: filter things in an empty {} like __hash, cnt, has_nil_QMARK_, etc
; (defn enfatten [x]
;   (into x (for [k (js-keys x)]
;     [(keyword k)
;      (aget x k)])))

(defn enfatten [x]
  (apply array (map #(into %
    {:x (.-x %)
     :y (.-y %)
     :r (.-r %)}) x)))

(-> d3 (.edn "flare.edn" 
  (fn [data]
    (let [zroot data
          nodes1 ((.-nodes pack-edn) zroot)
          nodes (enfatten nodes1)]

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
        (.text #(:name %)))))))

; (-> d3 (.json "flare.json"
;   (fn [data]
;     (.log js/console data)
;     (let [zroot data
;           nodes ((.-nodes pack) zroot)]

;       (.log js/console nodes)
;       (-> vis (.selectAll "circle")
;         (.data nodes)
;         (.enter) (.append "svg:circle")
;         (.attr "class" "parent")
;         (.attr "cx" #(.-x %))
;         (.attr "cy" #(.-y %))
;         (.attr "r" #(.-r %)))

;       (-> vis (.selectAll "text")
;         (.data nodes)
;         (.enter) (.append "svg:text")
;         (.attr "class" "parent")
;         (.attr "x" #(.-x %))
;         (.attr "y" #(.-y %))
;         (.attr "dy" ".35em")
;         (.attr "text-anchor" "middle")
;         (.style "opacity" #(if (> (.-r %) 20) 1 0))
;         (.text #(.-name %)))))))


(defn ^:export zoom [d i]
  (let [k (/ (/ r (.-r d)) 2)
        t (-> vis (.transition) (.duration 
            (if (-> d3 .-event .-altKey) 7500 750)))]

    (.domain x (array [(- (.-x d) (.-r d)), (+ (.-x d) (.-r d))]))
    (.domain y (array [(- (.-y d) (.-r d)), (+ (.-y d) (.-r d))]))

    (-> t (.selectAll "circle")
      (.attr "cx" #(x (.-x %)))
      (.attr "cy" #(y (.-y %)))
      (.attr "r" #(* k (.-r %))))

    (-> t (.selectAll "text")
      (.attr "x" #(x (.-x %)))
      (.attr "y" #(y (.-y %)))
      (.attr "opacity" #(if (> (* k (.-r %)) 20) 1 0)))))

; (def radii
;   "radius of each concentric circle"
;   {:hours 275, :minutes 270, :seconds 265, :millis, 260})

; (def arc
;   "returns function capabable of generating arcs from datums"
;   (-> d3 .-svg .arc
;     (.startAngle #(/ (* (:value %) Tau) 100))
;     (.endAngle #(/ (* (+ 50 (:value %)) Tau) 100))
;     (.innerRadius 0)
;     (.outerRadius #((keyword (:key %)) radii))))

; (defn curClockData []
;   "generates an array of 8 datums, one for each arc to draw"
;   (let [d       (js/Date.)
;         hours   (/ (* (rem (.getHours d) 12) 100) 12)
;         minutes (/ (* (.getMinutes d) 100) 60)
;         seconds (/ (* (.getSeconds d) 100) 60)
;         millis  (/ (* (.getMilliseconds d) 100) 1000)]
;     [ {:value hours, :key "hours", :which 1},
;       {:value (+ hours 50), :key "hours", :which 2},
;       {:value minutes, :key "minutes", :which 1},
;       {:value (+ minutes 50), :key "minutes", :which 2},
;       {:value seconds, :key "seconds", :which 1},
;       {:value (+ seconds 50), :key "seconds", :which 2},
;       {:value millis, :key "millis", :which 1},
;       {:value (+ millis 50), :key "millis", :which 2} ]))

; d3.json("us-states.json", function(collection) {
;   states.selectAll("path")
;       .data(collection.features)
;     .enter().append("svg:path")
;       .attr("d", path);
; });

; (.edn d3 "namecrawl.edn" 
;   (fn [c]
;     (.log js/console c)))

; (defn ^:export launch []
;   "call this to create top level svg and initiate animation"
;   (let [root 
;           (-> d3 (.select "#clocky") (.append "svg")
;               (.attr "width" 600)
;               (.attr "height" 600)
;               (.append "g")
;               (.attr "transform" "translate(300,300)rotate(-90)"))
;         rings
;           (-> root (.selectAll "g") (.data curClockData))]

;       ; side effect = a path for each data element returned from curClockData
;       (-> (.enter rings) (.append "g") (.append "path"))

;       ; this timer loops forever, rebinding via curClockData
;       (.timer d3 (fn []
;         ;(.log js/console root)
;         (-> root (.selectAll "g") (.data curClockData)
;             (.select "path")
;             (.attr "class" #(str (:key %) (:which %)))
;             (.attr "d", arc))
;         ; our work is never done
;         false))))

