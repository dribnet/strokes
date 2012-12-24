(ns strokes.core
  (:use [clojure.string :only [join]]
        [cljs.reader :only [read-string]]))

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

; patch a vector with accessors length
; js example: arr.__defineGetter__(0, function() { return 0; });
(defn patch-vector [v]
  (.__defineGetter__ v "length" #(count v))
  (.__defineGetter__ v 0 #(nth v 0 nil))
  (.__defineGetter__ v 1 #(nth v 1 nil))
  (-> v .-toString (set! #(clojure.string/join ", " v)))
  v)
