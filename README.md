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

---

### Check out the [separate repo of strokes examples](https://github.com/dribnet/strokes-examples).

---

There are also a few examples in this repo:

 * [circle packing](http://s.trokes.org/4584997)
 * [quadtree](http://s.trokes.org/4409139)
 * [convex hull](http://s.trokes.org/4369073)
 * [clocky](http://s.trokes.org/4326896)
 
To use from Clojure/ClojureScript add this to your `project.clj`:

    [net.drib/strokes "0.4.0"]

For more information see the Hello World example, below.

Or warmup by hacking on the above examples which currently live in this repo:

    lein cljsbuild auto

Then open public/strokes-{foo}.html and play around with cljs file in examples.

## A Hello World example

The following example should be enough to get you started in your own project (without forking this repo):

    lein new trystrokes
    cd trystrokes


`project.clj`:

     ;; ...
     :dependencies [[org.clojure/clojure "1.4.0"]
                    [net.drib/strokes "0.4.0"]]
     :min-lein-version "2.0.0"
     :source-paths ["src/clj" "src/cljs"]
     :plugins [[lein-cljsbuild "0.3.0"]]

     :cljsbuild {:builds [{:source-paths ["examples/strokes/hello"]
                           :compiler  {:optimizations :simple
                                       :externs ["public/d3/d3-externs.js"]
                                       :pretty-print false
                                       :output-to "public/out/hello.js"}}]})


`public/hello.html`:

    <!DOCTYPE html>
    <title>Hello!</title>
    <body>
    <script src="d3/d3.js"></script>
    <script type="text/javascript" src="out/hello.js"></script>
    </body>

`examples/strokes/hello/hello.cljs`:

    (ns strokes.examples.hello
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
          (.style "stroke" "grey")
          (.style "fill" "white")
          (.attr "r" 40)
          (.attr "cx" 50)
          (.attr "cy" 50)))

**NOTE**: The externs for the D3 library is also needed and can be copied e.g. from this repository to `public/d3/d3-externs.js`.  You should also copy the D3 source (FIXME: can one use a CDN instead?) to `public/d3/d3.js`.

Then `lein cljsbuild once` and view `public/hello.html` in your browser.  You should see a circle.


Several more complex examples can be found in this repository.
