; providing clojure data has a split personality: don't be afraid to let it all out
(ns strokes.mrhyde
  (:use [clojure.string :only [join]]
        [cljs.reader :only [read-string]]))

(defn- patch-persistent-vector-prototype []
  (let [v [1, 2]
        p (.-__proto__ v)]

    (.__defineGetter__ p "length" #(this-as t (count t)))
    (dotimes [n 100] 
      (.__defineGetter__ p n #(this-as t (nth t n js/undefined))))
    (-> p .-toString (set! #(this-as t (clojure.string/join ", " t))))))

(patch-persistent-vector-prototype)

; note: this goal is not to use this ... but we can peek at it for inspiration :)
; http://stackoverflow.com/questions/10157447/how-do-i-create-a-json-in-clojurescript
; (defn clj->js
;   "Recursively transforms ClojureScript maps into Javascript objects,
;    other ClojureScript colls into JavaScript arrays, and ClojureScript
;    keywords into JavaScript strings."
;   [x]
;   (cond
;     (string? x) x
;     (keyword? x) (name x)
;     (map? x) (.strobj (reduce (fn [m [k v]]
;                (assoc m (clj->js k) (clj->js v))) {} x))
;     (coll? x) (apply array (map clj->js x))
;     :else x))


