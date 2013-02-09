(ns mrhyde.guts)

; The following helpers patch individual js functions
; TODO: some (all?) of these require patch-args-clj-to-js to have been run, why?
(def hyde-proto-array-marker "$cljs$mrhyde$isarray")
(def hyde-proto-object-marker "$cljs$mrhyde$isobject")

(defn get-store-cur-js-fn [o field-name]
  (let [cur-fn (aget o field-name)
        js-fn-name (str "_js_" field-name)
        root-fn (aget o js-fn-name)]
    ; first store original (if we have not already done so)
    (if (= js/undefined root-fn)
      ; (.log js/console "storing: " js-fn-name)
      (aset o js-fn-name cur-fn))
    ; return cur-fn
    cur-fn))

(defn restore-original-js-fn [o field-name]
  (let [js-fn-name (str "_js_" field-name)
        root-fn (aget o js-fn-name)]
    ; first store original (if we have not already done so)
    (if (not= js/undefined root-fn)
      ; (.log js/console "storing: " js-fn-name)
      (aset o field-name root-fn))
    ; return cur-fn
    root-fn))

