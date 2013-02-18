(defproject voronoi-example "0.0.1-SNAPSHOT"
  :description "voronoi-example"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [net.drib/strokes "0.3.2-SNAPSHOT"]]
  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs"]

  :plugins [[lein-cljsbuild "0.3.0"]]

  :cljsbuild {:builds [{:source-paths ["src/cljs"]
                        :compiler { :output-to "public/out/voronoi-clipping.js"
                                    :pretty-print true 
                                    :optimizations :simple}}]})
