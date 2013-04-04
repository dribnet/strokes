(ns tester
  (:require-macros [reader.tags :as tags])
  (:require 
    [cljs.reader :as reader :refer [read-string]]))

; just warming up with the usual syntax
(def jso
  (clj->js {:a 1
            :b 2
            :c {:nested "key/value"}}))

; and a cheating alternative
(def jso2
  (js* "{a: 1,
         b: 2,
         c: {nested: \"key/value\"}}"))

; ok, so let's do the reader version
(reader/register-tag-parser! 'js clj->js)
(def jso3 (read-string "#js {:a 1
                             :b 2
                             :c {:nested \"key/value\"}}"))

; ok, just verifying that the macro is there...
(tags/js-test)

; // this is the goal... but it doesn't yet work
(def jso4 #js {:a 1
               :b 2
               :c {:nested "key/value"}})

; puts the objects on the page for all to see
(def to-json (.-stringify js/JSON))

(def body (str 
  (to-json jso)
  "<p>"
  (to-json jso2)
  "<p>"
  (to-json jso3)))

(aset (-> js/document .-body) "innerHTML" body)
