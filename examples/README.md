# strokes-examples

Howdy, we're open for business. Please watch your step.

This repo contains a handful set of examples of how to implement a new or existing [d3 visualization](https://github.com/mbostock/d3/wiki/Gallery) with the clojurescript [strokes library](https://github.com/dribnet/strokes). 

Current Contents:

* example-template for starting a new project
* venn-simple as a hello world (also a good template)
* general-update-pattern follows a d3 update tutorial
* simple-bar port of the bar graph from bostock's slide
* voronoi-clipping is a small voronoi program
* rotating-voronoi is a more complicated voronoi example
* histo-grow is an example of using a histogram
* zip-decode adapted version of nelson's d3 zipcode shower
* blah-blah-blah is the slide software for clojure/west
* highlighter is a slide utility for showing/hiding text
* shared - linked d3 lib for all samples (is there a better way?)

Online examples:

 * [General update pattern tutorial](http://s.trokes.org/5027817) progressing to [key funcitons](http://s.trokes.org/5027818) and [transitions!](http://s.trokes.org/5027819)
 * [simple venn diagram](http://s.trokes.org/4994892)
 * [voronoi clipping](http://s.trokes.org/4760896)
 * [rotating voronoi](http://s.trokes.org/4744126)
 * [(your own example here?)](http://s.trokes.org/)
 
To get started, clone this repo, copy your favorite example,

    lein cljsbuild auto

and then open public/index.html and start hacking.

Questions, feedback, and merge requests welcome.
