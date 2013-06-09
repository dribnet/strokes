(defproject general-update-pattern "0.0.1-SNAPSHOT"
  :description "general-update-pattern"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [net.drib/strokes "0.5.0"]]
  :plugins [[lein-cljsbuild "0.3.2"]]
  :min-lein-version "2.0.0"

  :source-paths ["src/gup1" "src/gup2" "src/gup3"]

  :cljsbuild {:builds [
                       ; update
                       {:source-paths ["src/gup1"]
                        :compiler  {:optimizations :simple
                                    :pretty-print true
                                    :output-to "public/out/gup1.js"}}
                       ; with key functions
                       {:source-paths ["src/gup2"]
                        :compiler  {:optimizations :simple
                                    :pretty-print true
                                    :output-to "public/out/gup2.js"}}
                       ; with transitions
                       {:source-paths ["src/gup3"]
                        :compiler  {:optimizations :simple
                                    ; :output-dir ".cljsbuild/advanced"
                                    :output-to "public/out/gup3.js"}}]})
