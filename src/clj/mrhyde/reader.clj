(ns mrhyde.reader
  (:require [cljs.tagged-literals :as t]))

; runtime translation via clj->js
(alter-var-root #'t/*cljs-data-readers* assoc 'jsr
                (fn [form]
                  (list 'cljs.core/clj->js form)))

; this macro is just here as a way of confirming dependencies
; (defmacro js-test []
;   `(.log js/console :js-test-macro-expansion))

