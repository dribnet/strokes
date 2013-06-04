(defproject b3 "0.0.1-SNAPSHOT"
  :description "blah-blah-blah"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [net.drib/strokes "0.5.0-SNAPSHOT"]]
  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs"]

  :plugins [[lein-cljsbuild "0.3.2"]]

  :cljsbuild {:builds [{:source-paths ["src/cljs"]
                        :compiler { :output-to "public/out/b3.js"
                                    :pretty-print true 
                                    :optimizations :simple}}]})
