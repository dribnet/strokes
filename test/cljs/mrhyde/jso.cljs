(ns mrhyde.jso
  (:require-macros 
    [mrhyde.jso :refer [inwards ct-name ct-clj ct-clj-key]]
    [mrhyde.reader])
  (:require 
    [cljs.reader :as reader :refer [read-string]]
    [mrhyde.extend-js]))

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

(defn dp [& args]
  (.log js/console (apply str args)))

(defn do-result [jso]

  (dp "by key:" (:b jso)) ;;=> 2

  (dp "get-in: " (to-json (get-in jso [:c :nested]))) ;;=> "[null]"

  (let [{:keys [a b]} jso]
    (dp a " and " b)
    ;;values corresponding to a and b avaliable as locals...
    )

  (doall (for [[k v] jso]
    (dp k " -> " (to-json v))
    ;;list comprehension over our JavaScript object...
    )))

(defn ^:export runtest []
  (aset (-> js/document .-body) "innerHTML" body)
  (dp "jso:")
  (do-result jso)
  (dp "jso2:")
  (do-result jso2)
  (dp "jso3:")
  (do-result jso3)
  (dp "jso4:")
  (do-result jso4)
  (dp "jso5:")
  (do-result jso5)
  (dp "jso6:")
  (do-result jso6))

