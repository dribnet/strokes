(defproject strokes "0.0.1-SNAPSHOT"
  :description "strokes: d3 from clojurescript"
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :min-lein-version "2.0.0"
  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "0.2.7"]]

  :cljsbuild {
    :builds [{
      :source-path "src/cljs"
      :compiler {
        :output-to "public/out/strokes-main.js"
        :optimizations :whitespace
        :pretty-print true 
        ;:optimizations :advanced
        }
      :jar true}]})
