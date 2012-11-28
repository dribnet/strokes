(ns d3.core)

(def d3 js/d3)
(def Tau (* 2 Math/PI))

; create and install data selection filter
(defn- datafilter [x]
  (if (re-find #"^function" (type x))
    (fn [] (apply array (x)))
    (apply array x)))

(-> d3 .-selection .-prototype .-dataToArray (set! datafilter)) 

; -- debug stuff --
; (.log js/console (type x)))

; (fn []
;     (apply array 
;       [ {:value 10, :key "hours", :which 1},
;         {:value (+ 10 50), :key "hours", :which 2},
;         {:value 25, :key "minutes", :which 1},
;         {:value (+ 25 50), :key "minutes", :which 2},
;         {:value 50, :key "seconds", :which 1},
;         {:value (+ 50 50), :key "seconds", :which 2},
;         {:value 75, :key "millis", :which 1},
;         {:value (+ 75 50), :key "millis", :which 2} ])
;     )

; -- aborted smarts version --
; ((fn []
;   ; save original function and define replacement
;   (let [orig (-> d3 .-selection .-prototype .-data)
;         repl (fn []
;             ; probe args. if has a first argument and that argument is not a functino, apply array
;             (let [arguments js/arguments
;                   length    (.length arguments)
;                   dofirst   (and (> length 0) (!= (type (first arguments)) "function"))]
;               (if dofirst
;                 (aset arguments 0 (apply array (first arguments))))

              ; in all cases call (and return) the chained original (maybe with new arguments)
              ; return orig.apply(this, Array.prototype.slice.call(arguments));




          ;     (if (> length 0)
          ;       let [front (first 1)
          ;            native-type (type front)
          ;            deep-type (type (-> Object .-prototype .-toString (.call front)))

          ; )
