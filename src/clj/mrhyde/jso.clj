(ns mrhyde.jso)

; this macro is just here as a way of confirming dependencies
(defmacro js-test []
  `(.log js/console :js-test-macro-expansion))

(defmacro preprocess [f & args]
  (let [x# (apply (resolve f) args)]
    `~x#))

(declare ct-clj)
(declare ct-clj-key)

(defmacro ct-clj-key [k]
  (if (or (string? k)
          (number? k)
          (keyword? k)
          (symbol? k))
    (ct-clj k)
    (pr-str k)))

; (defmacro inwards [x]
;   (if (coll? x) 
;     (vec (concat ["col"] (map #(inwards %) x)) "x")))

(defn intest-fn [x]
  (if (coll? x)
    (vec (map #(intest-fn %) x))
    x))

(defmacro intest [x]
  (intest-fn x))

(defn inwards-fn [x]
  (if (coll? x)
    (concat (list 'cljs.core/array) (map #(inwards-fn %) x))
    x))

(defmacro inwards [x]
  (inwards-fn x))

(defn jso-fn [x]
  (cond
    (symbol? x) (str x)
    (keyword? x) (name x)
    (map? x) (concat (list 'cljs.core/js-obj) 
                (map #(jso-fn %) (reduce into [] x)))
    (coll? x) (concat (list 'cljs.core/array)
                (map #(jso-fn %) x))
    :else x))

(defmacro jso [x]
  (jso-fn x))

(defmacro ct-name [k]
  (name k))