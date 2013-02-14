(ns mrhyde.funpatcher
  (:require [mrhyde.mrhyde :refer [hyde-array? recurse-from-hyde-cache IHyde]]
            [mrhyde.guts :refer [get-store-cur-js-fn restore-original-js-fn
                                 hyde-proto-array-marker hyde-proto-object-marker]]
            ))

; patch a js function to return a clj-ish value
(defn patch-return-value-to-clj [o field-name]
  (let [orig-fn (get-store-cur-js-fn o field-name)]
    (aset o field-name (fn [& args] 
      (js->clj (this-as ct (.apply orig-fn ct args)))))))

; patch a js function to return a clj-ish value
(defn patch-return-value-recurse-from-cache [o field-name]
  (let [orig-fn (get-store-cur-js-fn o field-name)]
    (aset o field-name (fn [& args] 
      (recurse-from-hyde-cache (this-as ct (.apply orig-fn ct args)))))))

; patch a js fn returning a fn to return a fn returning a clj-ish value of the other fn
(defn patch-return-value-recurse-from-cache-as-function [o field-name]
  (let [orig-fn (get-store-cur-js-fn o field-name)]
    (aset o field-name (fn [& args]
      (let [dyn-fun (this-as ct (.apply orig-fn ct args))]
        ; dyn-fun)))))
        (fn [& nargs]
          (this-as ct (.apply dyn-fun ct nargs))))))))

(defn recurse-from-hyde-cache-maybe-fn [x]
  (if (goog.isFunction x)
    ; return funciton for execution-later
    (fn [& args]
      (recurse-from-hyde-cache (this-as ct (.apply x ct args))))
    ; else - execute here and now
    (recurse-from-hyde-cache x)))

; patch a js function convert specified keyword args to functions
(defn patch-args-recurse-from-cache [o field-name & fields]
  (let [orig-fn (get-store-cur-js-fn o field-name)
        arg-filter (if (empty? fields) #(identity true) (set fields))]
    (aset o field-name
      (fn [& args]
        ; (.log js/console (str "patching: " (count args)))
        (let [nargs (map #(if (arg-filter %1) (recurse-from-hyde-cache-maybe-fn %2) %2) (range) args)]
          ; (.log js/console (str "patched: " (type (nth nargs 0))))
          (this-as ct (.apply orig-fn ct nargs)))))))

; patch a js function convert specified keyword args to functions
(defn patch-args-keyword-to-fn [o field-name & fields]
  (let [orig-fn (get-store-cur-js-fn o field-name)
        arg-filter (if (empty? fields) #(identity true) (set fields))]
    (aset o field-name
      (fn [& args]
        ; (.log js/console (str "patching: " (count args)))
        (let [nargs (map (fn [c x] 
                           (if (and (arg-filter c) (keyword? x)) #(x %) x))
                         (range) args)]
          ; (.log js/console (str "patched: " (type (nth nargs 0))))
          (this-as ct (.apply orig-fn ct nargs)))))))

; patch a js function, converting specified seqs to js arrays
(defn patch-args-seq-to-array [o field-name & fields]
  (let [orig-fn (get-store-cur-js-fn o field-name)
        arg-filter (if (empty? fields) #(identity true) (set fields))]
    (aset o field-name
      (fn [& args]
        ; (.log js/console (str "patching: " (count args)))
        (let [nargs (map #(if (and (arg-filter %1) (sequential? %2))
                            (apply array %2) %2) (range) args)]
          ; (.log js/console (str "patched: " (type (nth nargs 0))))
          (this-as ct (.apply orig-fn ct nargs)))))))

; the big hammer: patch a js function, converting specified args from clj to js
(defn patch-args-clj-to-js [o field-name & fields]
  (let [orig-fn (get-store-cur-js-fn o field-name)
        arg-filter (if (empty? fields) #(identity true) (set fields))]
    (aset o field-name
      (fn [& args]
        ; (.log js/console (str "patching: " (count args)))
        (let [nargs (map #(if (arg-filter %1) (clj->js %2) %2) (range) args)]
          ; (.log js/console (str "patched: " (type (nth nargs 0))))
          (this-as ct (.apply orig-fn ct (apply array nargs))))))))


; WARNING: THIS HAS BEEN DEEMED A BAD IDEA (though it did work once)
(defn patch-tostring-sequential-isarray [o field-name]
  ; (.log js/console (str "installing " o "," field-name))
  (let [orig-fn (get-store-cur-js-fn o field-name)]
    (aset o field-name
      (fn [& args]
        (this-as ct
          (if (hyde-array? ct) "[object Array]"
            ;else
            (.apply orig-fn ct args)))))))

; WARNING: THIS HAS BEEN DEEMED A BAD IDEA (though it did work once)
(defn patch-tostring-hydearray-is-array []
  (patch-tostring-sequential-isarray (-> js/Object .-prototype) "toString"))

