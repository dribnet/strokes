(ns d3.core
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

;; example of a fetch callback in js
; d3.text = function(url, mime, callback) {
;   function ready(req) {
;     callback(req && req.responseText);
;   }
;   if (arguments.length < 3) {
;     callback = mime;
;     mime = null;
;   }
;   d3.xhr(url, mime, ready);
; };
