(ns b3
  (:require [strokes :refer [d3]]))

(strokes/bootstrap)

(def span (-> d3 (.select "#hostspan")))

(def pagedata [
  {:name "A" :pos [-411 -296] :scale 0.1}
  {:name "B" :pos [-261 -146] :scale 0.3}
  {:name "C" :pos [ 261 -146] :scale 0.4}
])

(def fakedata ["A" "B" "C"])

; destructuring the parameter directly confuses d3's apply somehow
(defn pos-scale-to-str [d]
  (let [{:keys [pos scale]} d]
    (str "-webkit-transform: translate3d(" (first pos) "px," (second pos) "px,0px) scale(" scale ");")))

(let [pages (-> span (.selectAll "iframe") (.data pagedata #(:name %)))]
  ; UPDATE
  ; Update old elements as needed
  ; (-> text (.attr {:style "update"}))

  ; ENTER
  ; Create new elments as needed
  (-> pages (.enter) (.append "iframe")
    (.attr {:id #(:name %)
            :width 1024
            :height 968
            :src #(str (:name %) ".html")
            :style pos-scale-to-str})))

  ; ENTER + UPDATE
  ; Appending to the enter selection expands the update selection to include
  ; entering elements; so, operations on the update selection after appending to
  ; the enter selection will apply to both entering and updating nodes.
  ; (-> text (.text identity))

  ; EXIT
  ; Remove old elements as needed.
  ; (-> text (.exit) (.remove)))



; (-> span (.append "iframe")
;   (.attr {:id "A"
;           :width 1024
;           :height 968
;           :src "A.html"
;           :style "-webkit-transform: translate3d(-411px,-296px,0px) scale(0.1);"}))

; (-> span (.append "iframe")
;   (.attr {:id "B"
;           :width 1024
;           :height 968
;           :src "B.html"
;           :style "-webkit-transform: translate3d(-261px,-146px,0px) scale(0.3);"}))

