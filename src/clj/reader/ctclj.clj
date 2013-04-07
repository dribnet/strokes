(ns reader.ctclj)

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

(defmacro intest [x]
  (if (coll? x)
    (vec (map #(intest %) x))
    "x"))

(defmacro inwards [x]
  (if (coll? x)
    (concat (list 'cljs.core/array) (map #(inwards %) x))
    "x"))

(defmacro ct-clj [x]
  (cond
    (keyword? x) (name x)
    (symbol? x) (str x)
    ; (map? x) (let [m (js-obj)]
    ;            (doseq [[k v] x]
    ;              (aset m (-key->js k) (-clj->js v)))
    ;            m)
    ; (coll? x) (concat (list 'cljs.core/array) x)
    (coll? x) (concat (list 'cljs.core/array) (map #(ct-clj %) x))
    :else x))

(defmacro ct-name [k]
  (name k))