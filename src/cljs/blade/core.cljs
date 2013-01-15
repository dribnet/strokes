(ns blade.core
  (:use [mrhyde.core :only [patch-known-arrayish-types 
                            patch-known-mappish-types 
                            patch-js-with-key-lookup
                            patch-args-seq-to-array
                            patch-args-clj-to-js]]
        [clojure.string :only [join]]
        [cljs.reader :only [read-string]]))

(def L (this-as ct (aget ct "L")))

(if L (do
  ; patch all seqs to also be read-only arrays for javascript interop
  (patch-known-arrayish-types)
  ; patch maps to include key based accessors on js object
  (patch-known-mappish-types)

  ; experimental - all objects can lookup by key (allows obj destructuring, etc)
  (patch-js-with-key-lookup)

  ; filter L.latLng inputs: need to force seqs to arrays
  ; (called indirectly many places, including map.setView)
  (patch-args-seq-to-array L "latLng")

  (patch-args-clj-to-js (-> L .-control) "layers")  
))
