(defproject scratch "0.0.1-SNAPSHOT"
  :description "scratch"
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs"]

  :plugins [[lein-cljsbuild "0.3.0"]]

  :cljsbuild {:builds [{:source-paths ["src/cljs" "src/clj"]
                        :compiler { :output-to "public/out/tester.js"
                                    :pretty-print true 
                                    :optimizations :whitespace}}]})
