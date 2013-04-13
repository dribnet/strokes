# strokes

*Now, the world don't move to the beat of just one drum,  
What might be right for you, may not be right for some.*

**strokes is an interop library for using [d3.js](http://d3js.org/) from clojurescript**

The primary design decision is to remove as much of the interop glue
and clj->js->clj data marshalling from application code as possible.
D3 is a wonderfully functional library and seems well suited for
playing nice with clojurescript, provided the usual data wrangling
issues can be made to go away. This is achieved in strokes via
JavaScript metaprogramming that provides a language polyfill for the
clojure container classes so that they can be accessed natively by D3.

strokes is maturing and already supports most D3 functionality - I
welcome feedback and contributions. I am currently focusing on porting
a handful [existing D3
examples](https://github.com/mbostock/d3/wiki/Gallery) from JavaScript
to fairly idiomatic clojurescript in order to test the existing
interop strategy. strokes also has some support for methods that
attempt to modify clojure's persistent data structures (eg: layouts)
as demonstated in the circle packing example.

The strokes repo also currently includes a ClojureScript to JavaScript
data interop library called `mrhyde`. `mrhyde` allows cljs sequential
types to be treated as native JavaScript arrays by implementing the
[ArrayLike.js specification](https://github.com/dribnet/ArrayLike.js)
and then using the ArrayLikeIsArray polyfill. This technique has
proven useful on the leaflet and angular libraries. In addition,
`mrhyde` includes data interop glue so that cljs map types can be
treated as native JavaScript objects as well as several helpful
functions patching JavaScript functions to provide smoother interop.

## Getting Started 

To use strokes from your ClojureScript project, 
add this dependency to your `project.clj`:

    [net.drib/strokes "0.4.1"]

---

#### To see more examples, check out the [separate repo of strokes examples](https://github.com/dribnet/strokes-examples) to see several example projects. A great start is the [venn-simple](https://github.com/dribnet/strokes-examples/tree/master/venn-simple) project which generates this [simple self-contained sketch](http://s.trokes.org/dribnet/4994892) in 20 lines of ClojureScript.

---

Or just follow along with this hello world example below.

### Hello World example

The following example should be enough to get you started in your own project without needing to fork this repo:

    lein new trystrokes
    cd trystrokes

`project.clj`:

```clj
    (defproject trystrokes "0.0.1-SNAPSHOT"
      :description "FIXME: write description"
      :dependencies [[org.clojure/clojure "1.4.0"]
                     [net.drib/strokes "0.4.1"]]
      :min-lein-version "2.0.0"
      :source-paths ["src/clj" "src/cljs"]

      :plugins [[lein-cljsbuild "0.3.0"]]

      :cljsbuild {:builds [{:source-paths ["src/cljs"]
                            :compiler { :output-to "public/out/trystrokes.js"
                                        :pretty-print true 
                                        :optimizations :simple}}]})
```

`public/trystrokes.html`:

```html
    <!DOCTYPE html>
    <title>Hello strokes!</title>
    <style>
      circle {
        fill: firebrick;
        fill-opacity: 0.7;
      }
      circle:hover {
        fill-opacity: 1.0;
      }
    </style>
    <body>
      <script src="http://d3js.org/d3.v3.min.js"></script>
      <script src="out/trystrokes.js"></script>
    </body>
```

`src/cljs/trystrokes/core.cljs`:

```clj
    (ns trystrokes.core
      (:require [strokes :refer [d3]]))

    (strokes/bootstrap)

    (def width 960)
    (def height 500)

    ; create root svg element
    (defn gen-svg []
      (-> d3 (.select "body") (.append "svg")
        (.attr "width" width)
        (.attr "height" height)))

    (let [svg (gen-svg)]
      (-> svg
          (.append "circle")
          (.attr "r" 40)
          (.attr "cx" 50)
          (.attr "cy" 50)))
```

Then `lein cljsbuild once` and open `public/hello.html` in your browser.
You should see a circle appear in the upper left corner.

You can also use the command `lean cljsbuild auto` to rebuild on every
save of `core.cljs` - just reload in the browser to iteratively develop.
