# #js tagged literal

The following is common in ClojureScript code when using JavaScript libraries:

```clojure
(def jso
  (clj->js {:a 1
            :b 2
            :c {:nested "key/value"}}))
```

As a general pattern, it seems like a very wasteful way to make a javascript object - build a clojurescript data structure, translate it at runtime, and then throw it away. I toyed around with some alternatives in this repo. My current opinion is that this idiom would be a perfect candidate for a tagged literal:

```clojure
(def jso
  #js {:a 1
       :b 2
       :c {:nested "key/value"}})
```

This would be a nice succinct syntax and would happen at compile-time. I've got this working for the cljs reader, but not the clj reader as I believe would be required to affect the source code directly (and is much trickier to implement).
