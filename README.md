# strokes

*Now, the world don't move to the beat of just one drum,  
What might be right for you, may not be right for some.*

**strokes is an interop library for using [d3.js](http://d3js.org/) from clojurescript**

The primary design decision is to remove as much of the interop glue and clj->js->clj data marshalling from application code as possible. d3 is a wonderfully functional library and seems well suited for playing nice with clojurescript, provided the usual data wrangling issues can be made to go away. This is achieved in strokes via javascript metaprogramming that provides a language polyfill for the clojure container classes so that they can be accessed natively by d3.

strokes is still young and under active development, but most of the core framework is in place and I'm eager to get feedback and contributions. I am currently focusing on porting a handful [existing d3 examples](https://github.com/mbostock/d3/wiki/Gallery) from javascript to fairly idiomatic clojurescript in order to test the existing interop strategy. strokes also has some support for methods that attempt to modify clojure's persistent data structures (eg: layouts) as demonstated in the circle packing example.

The strokes repo also currently includes a ClojureScript to JavaScript data interop
library called mrhyde. mrhyde allows cljs sequential types to be treated as native
JavaScript arrays by implementing the [isArray.js specification](https://github.com/dribnet/isArray.js) and the isArray-polyfill. This technique has proven useful
on the leaflet and angular libraries. In addition, mrhyde includes data interop glue
so that cljs map types can be treated as native JavaScript objects as well as
several helpful funtcions patching javascript functions to provide smoother interop.

---

### Check out the [separate repo](https://github.com/dribnet/strokes-examples) of strokes examples.

---

There are also a few examples in this repo:

 * [circle packing](http://s.trokes.org/4584997)
 * [quadtree](http://s.trokes.org/4409139)
 * [convex hull](http://s.trokes.org/4369073)
 * [clocky](http://s.trokes.org/4326896)
 
To use from Clojure/ClojureScript add this to your `project.clj`:

    [net.drib/strokes "0.3.1"]]

Or warmup by hacking on the above examples which currently live in this repo:

    lein cljsbuild auto

Then open public/strokes-{foo}.html and play around with cljs file in examples.
