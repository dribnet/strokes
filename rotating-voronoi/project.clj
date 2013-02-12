(defproject example "0.0.1-SNAPSHOT"
  :description "strokes rotating voronoi example"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [net.drib/strokes "0.3.0"]]
  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs"]

  :plugins [[lein-cljsbuild "0.3.0"]]

  :cljsbuild {:builds [{:source-paths ["src/cljs"]
                        :compiler { :output-to "public/out/rotating-voronoi.js"
                                    :pretty-print true 
                                    :optimizations :whitespace}}]})
