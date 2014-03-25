# strokes

*Now, the world don't move to the beat of just one drum,  
What might be right for you, may not be right for some.*

**strokes is an interop library for using [d3.js](http://d3js.org/) from clojurescript**

D3 is a wonderfully functional library and seems well suited for
playing nice with clojurescript, provided the usual data wrangling
issues can be made to go away. This is achieved in strokes via
JavaScript metaprogramming that provides a language polyfill for the
clojure container classes so that they can be accessed natively by D3.
This interop functionality is provided by the separate
and growing [mrhyde](https://github.com/dribnet/mrhyde) library.

This repo also currently includes a number of standalone examples
in the [examples](examples) subdirectory.
The [venn-simple](examples/venn-simple)
example is probably the easiest 'hello-world' program to use as
template to start hacking your own projects.

Online examples:

 * [General update pattern tutorial](http://s.trokes.org/5027817) progressing to [key functions](http://s.trokes.org/5027818) and [transitions](http://s.trokes.org/5027819)
 * [simple venn diagram](http://s.trokes.org/4994892)
 * [voronoi clipping](http://s.trokes.org/4760896)
 * [rotating voronoi](http://s.trokes.org/4744126)
 * [(your own example here?)](http://s.trokes.org/)

## Getting Started 

To use strokes from your ClojureScript project, 
add this dependency to your `project.clj`:

    [net.drib/strokes "0.5.1"]

Or to get started in this repo, try [venn-simple](examples/venn-simple) 
or any of the other examples. Simply 

```
cd examples/venn-simple
lein cljsbuild auto
```

Then open public/hello.html to see [three circles](http://s.trokes.org/4994892) in your web browser. Then to get hacking, you can then edit the [venn-simple source file](examples/venn-simple/src/cljs/venn-simple.cljs), save, and refresh your browser to see the changes.
