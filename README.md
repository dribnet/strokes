# strokes

*Now, the world don't move to the beat of just one drum,  
What might be right for you, may not be right for some.*

**strokes is an interop library for using [d3.js](http://d3js.org/) from clojurescript**

The primary design decision is to remove as much of the interop glue and clj->js->clj data marshalling from application code as possible. d3 is a wonderfully functional library and seems well suited for playing nice with clojurescript, provided the usual data wrangling issues can be made to go away. This is achieved in strokes via light javascript metaprogramming that provides a language polyfill for the clojure container classes so that they can be accessed natively by d3.

strokes is in the early and active development, but I welcome feedback and contributions. I am currently focusing on porting a handful [existing d3 examples](https://github.com/mbostock/d3/wiki/Gallery) from javascript to fairly idiomatic clojurescript in order to test the existing interop strategy. strokes currently targets d3 applications that don't modify the input data (ie: not layouts), though full d3 support is planned for the future.

Current examples:

 * [quadtree](http://s.trokes.org/4409139)
 * [convex hull](http://s.trokes.org/4369073)
 * [clocky](http://s.trokes.org/4326896)
 
(more archeology is available by visiting [the upstream clocky repo](https://github.com/dribnet/clocky)...)

Try:

lein cljsbuild auto

Then open public/{foo}.html and start hacking.

