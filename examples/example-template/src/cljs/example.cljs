(ns example
  (:require [strokes :refer [d3]]))

(strokes/bootstrap)

(this-as t (.log js/console (str "hello " t)))
