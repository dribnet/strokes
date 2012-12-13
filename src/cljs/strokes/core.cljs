(ns strokes.core
  (:use [cljs.reader :only [read-string]]))

(def d3 js/d3)
(def Tau (* 2 Math/PI))

; create and install data selection filter
(defn- datafilter [x]
  ;(.log js/console (str "type is: " (type x)))
  (if (and (re-find #"^function" (type x))
           (not (re-find #"^function Array()" (type x))))
    (fn [] (apply array (x)))
    (apply array x)))

(-> d3 .-selection .-prototype .-dataToArray (set! datafilter)) 

(defn- d3-edn
  ([url callback]
    (d3-edn url nil callback))
  ([url mime callback]
    (letfn [(ready [req] (callback (if req  (read-string (.-responseText req)) req)))]
      (.log js/console (str "loading: " url))
      (.xhr d3 url mime ready))))

(-> d3 .-edn (set! d3-edn)) 

(defn add-stragglers [x]
  "adds elements to map x that have been stuck onto the js object x"
  (let [empty-map {}
        new-keys (remove (set (js-keys empty-map)) (js-keys x))]
        ;main-keys (remove #(re-matches #"^:cljs\$.*" (str %)) new-keys)] <- idea for future?
    (into x
        (for [k new-keys]
          [(keyword k) (aget x k)]))))

(defn array-add-stragglers [a]
  "unpack an array of maps, add stragglers, and repack into array"
  (apply array (map add-stragglers a)))

