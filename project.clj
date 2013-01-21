(defproject net.drib/strokes "0.0.2-SNAPSHOT"
  :description "strokes: d3 from clojurescript"
  :dependencies [
    [org.clojure/clojure "1.4.0"]
    [domina "1.0.1"]] ; <-- currently used only for unit test framework
  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs"]

  :plugins [[lein-cljsbuild "0.2.10"]]

  :cljsbuild {:builds [{:source-path "src/cljs"
                        :jar true
                        :compiler { :output-to "public/out/strokes.js"
                                   ;:optimizations :whitespace
                                   ;:pretty-print true 
                                    :optimizations :simple}}
                       {:source-path "test/cljs"
                        :compiler { :output-to "public/out/strokes_test.js"
                                   ;:optimizations :whitespace
                                   ;:pretty-print true 
                                    :optimizations :simple}}]})
