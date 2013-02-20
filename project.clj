(defproject net.drib/strokes "0.3.3-SNAPSHOT"
  :description "strokes: d3 from clojurescript"
  :url "https://github.com/dribnet/strokes"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs"]

  :plugins [[lein-cljsbuild "0.3.0"]]

  :cljsbuild {:builds [{:source-paths ["src/cljs"]
                        :jar true
                        :compiler { :output-to "public/out/strokes.js"
                                    :externs ["public/d3/d3-externs.js"]
                                   ;:optimizations :whitespace
                                   ;:pretty-print true 
                                    :optimizations :simple}}

                       ; tests
                       {:source-paths ["src/cljs" "test/cljs"]
                        :compiler  {:optimizations :whitespace
                                    :externs ["public/d3/d3-externs.js" "public/dummylib/dummylib-externs.js"]
                                    :pretty-print true
                                    :print-input-delimiter true
                                    :output-to "public/out/strokes_test_whitespace.js"}}
                       {:source-paths ["src/cljs" "test/cljs"]
                        :compiler  {:optimizations :simple
                                    :externs ["public/d3/d3-externs.js" "public/dummylib/dummylib-externs.js"]
                                    :pretty-print true
                                    ;:output-dir ".cljsbuild/simple"
                                    :output-to "public/out/strokes_test_simple.js"}}
                       {:source-paths ["src/cljs" "test/cljs"]
                        :compiler  {:optimizations :advanced
                                    :externs ["public/d3/d3-externs.js" "public/dummylib/dummylib-externs.js"]
                                    :pretty-print true
                                    ; :output-dir ".cljsbuild/advanced"
                                    :print-input-delimiter true
                                    :output-to "public/out/strokes_test_advanced.js"}}


                      ; strokes examples
                       {:source-paths ["examples/strokes/clocky"]
                        :compiler  {:optimizations :simple
                                    :externs ["public/d3/d3-externs.js"]
                                    :pretty-print false
                                    :output-to "public/out/strokes-clocky.js"}}
                       {:source-paths ["examples/strokes/hull"]
                        :compiler  {:optimizations :advanced
                                    :externs ["public/d3/d3-externs.js"]
                                    :pretty-print false
                                    :output-to "public/out/strokes-hull.js"}}
                       {:source-paths ["examples/strokes/quadtree"]
                        :compiler  {:optimizations :simple
                                    :externs ["public/d3/d3-externs.js"]
                                    :pretty-print false
                                    :output-to "public/out/strokes-quadtree.js"}}
                       {:source-paths ["examples/strokes/circle-pack"]
                        :compiler  {:optimizations :simple
                                    :externs ["public/d3/d3-externs.js"]
                                    :pretty-print false
                                    :output-to "public/out/strokes-circle-pack.js"}}

                      ; blade examples
                       {:source-paths ["examples/blade/tutorial1"]
                        :compiler  {:optimizations :simple
                                    :pretty-print true
                                    :output-to "public/out/blade-tutorial1.js"}}

                       ; {:source-path "test/cljs"
                       ;  :compiler  {:optimizations :advanced
                       ;              :externs ["public/d3/d3-externs.js"]
                       ;              :pretty-print false
                       ;              ;:output-dir ".cljsbuild/advanced"
                       ;              :output-to "public/out/strokes_test_min.js"}}
                                    ]})
