(ns blade
  (:require [mrhyde.core :as mrhyde]))

(def L (this-as ct (aget ct "L")))

(defn ^:export bootstrap [& opts]
  (mrhyde/bootstrap))
