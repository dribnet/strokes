(ns b3
  (:require [clojure.string]
            [strokes :refer [d3]]))

(strokes/bootstrap)

(def span (-> d3 (.select "#hostspan")))

(def titledata [{:name "title" :pos [0 0] :scale 1.0}])

(def pagedata (atom [titledata]))

; destructuring the parameter directly confuses d3's apply somehow
(defn pos-scale-to-str [d i]
  (let [{:keys [pos scale] :or {pos [0 0] scale 1.0}} d]
    ; (.log js/console (str "pos and scale: " pos "," scale))
    (str "-webkit-transform: translate3d(" (first pos) "px," 
      (second pos) "px,0px) scale(" scale ");z-index:" i ";")))

(def curpage (atom 0))
(def stage-exit 2000)
(def curflow (atom stage-exit))
(def timestr (str (.now js/Date)))

(defn pos-scale-to-str-birth [d i]
  (pos-scale-to-str (update-in d [:pos 0] + @curflow) i))

(defn pos-scale-to-str-death [d i]
  (pos-scale-to-str (update-in d [:pos 0] - @curflow) i))

(defn update [data]
  ; DATA JOIN
  ; Join new data with old elements, if any.
  (let [pages (-> span (.selectAll "iframe") (.data data #(:name %)))]
    ; UPDATE
    ; Update old elements as needed
    (-> pages
      (.transition)
        (.duration 750)
        (.attr {:style pos-scale-to-str}))

    ; ENTER
    ; Create new elments as needed
    (-> pages (.enter) (.append "iframe")
      (.attr {:id #(:name %)
              :width 1024
              :height 968
              :src #(str (:name %) ".html?" timestr)
              :style pos-scale-to-str-birth})
      (.transition)
        (.duration 750)
        (.attr {:style pos-scale-to-str}))

    ; EXIT
    ; Remove old elements as needed.
    (-> pages (.exit)
      (.transition)
        (.duration 750)
        (.attr {:style pos-scale-to-str-death})
        (.remove))))

(defn step [n]
  (if (> n 0) (reset! curflow stage-exit))
  (if (< n 0) (reset! curflow (- 0 stage-exit)))
  (swap! curpage #(mod (+ % n) (count @pagedata)))
  (-> js/window .-location .-hash (set! (str @curpage)))
  (update (nth @pagedata @curpage)))
  ;(.log js/console (str "page is " @curpage))

(defn key-fn []
  (let [key (-> d3 .-event .-keyCode)]
    (cond
      (or (= key 188) (= key 37)) (step -1) ; key , or <-
      (or (= key 190) (= key 39)) (step 1)  ; key . or ->
      )))
      ; :else
      ;   (.log js/console (str "The key is " key)))))

(-> d3 (.select "body") (.on "keyup" key-fn))

(update titledata)

(strokes/fetch-edn "slides.edn" (fn [error, root]
  (if-not (or (zero? error) (nil? error))
    (.log js/console (str "error: " error))
    (do 
      (swap! pagedata concat root)
      (let [pageid (clojure.string/replace (-> js/window .-location .-hash) "#" "")]
        (if pageid 
          (step (int pageid))
          (step 1)))))))

