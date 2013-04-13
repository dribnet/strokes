(ns tester
  (:require-macros [reader.ctclj :as tags :refer [inwards ct-name ct-clj ct-clj-key]])
  (:require 
    [cljs.reader :as reader :refer [read-string]]))

; just warming up with the usual syntax
(def jso
  (clj->js {:a 1
            :b [2 4 [6 6]]
            :c {:nested [nil]}}))

; and a cheating alternative
(def jso2
  (js* "{a: 1,
         b: [2, 4, [6, 6]],
         c: {nested: [null]}}"))

; ok, so let's do the reader version
(reader/register-tag-parser! 'js clj->js)
(def jso3 (read-string "#js {:a 1
                             :b [2 4 [6 6]]
                             :c {:nested [nil]}}"))

; ok, just verifying that the macro is there...
; (tags/js-test)

; // this version of the tagged literal works at runtime
(def jso4 #jsr {:a 1
                :b [2 4 [6 6]]
                :c {:nested [nil]}})

; here is an example of how to recursively break down this
; initialization into nested forms that resolve at compile time
(def jso5 (js-obj 
            (ct-name :a) 1
            (ct-name :b) (array 2 4 (array 6 6))
            (ct-name :c) (js-obj
                  (ct-name :nested) (array nil))))

(def jso6 (inwards [2 4 [6 6]]))

; puts the objects on the page for all to see
(def to-json (.-stringify js/JSON))

(def body (str 
  (to-json jso)
  "<p>"
  (to-json jso2)
  "<p>"
  (to-json jso3)
  "<p>"
  (to-json jso4)
  "<p>"
  (to-json jso5)
  "<p>"
  (to-json jso6)
  ))

(aset (-> js/document .-body) "innerHTML" body)
