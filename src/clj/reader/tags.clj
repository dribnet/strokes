(ns reader.tags
  (:require [cljs.tagged-literals :as t]))

; identity is just here for testing
; (not sure yet what I will replace it with)
(alter-var-root #'t/*cljs-data-readers* assoc 'js identity)

; this macro is just here as a way of confirming dependencies
(defmacro js-test [] ())
