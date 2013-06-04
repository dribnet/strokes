(defproject example "0.0.1-SNAPSHOT"
  :description "strokes rotating voronoi example"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [net.drib/strokes "0.5.0-SNAPSHOT"]]
  :plugins [[lein-cljsbuild "0.3.2"]]
  :min-lein-version "2.0.0"

  :source-paths ["src/clj" "src/cljs"]

  :cljsbuild {:builds [{:source-paths ["src/cljs"]
                        :compiler { :output-to "public/out/rotating-voronoi.js"
                                    :pretty-print true 
                                    :optimizations :whitespace}}]})
