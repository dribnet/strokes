(ns mrhyde.core
  (:require [mrhyde.typepatcher
               :refer [patch-known-vector-types
                       patch-known-mappish-types]]))

(defn ^:export bootstrap [& opts]
  ; patch all seqs to also be read-only arrays for javascript interop
  (patch-known-vector-types)
  ; patch maps to include key based accessors on js object
  (patch-known-mappish-types))
