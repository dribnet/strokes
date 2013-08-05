/* sweet jesus: http://www.dotnetwise.com/Code/Externs/ */

// javascript file: http://d3js.org/d3.v3.js
// run: d3
// run and reformat: d3.selection.prototype
// and manually append: d3.selection.enter, d3.geom.quadtree
// and include the d3 version up here at the top for good measure

var d3 = {
    "version": "3.0.6",
    "map": function () {},
    "functor": function () {},
    "rebind": function () {},
    "ascending": function () {},
    "descending": function () {},
    "mean": function () {},
    "median": function () {},
    "min": function () {},
    "max": function () {},
    "extent": function () {},
    "random": {
        "normal": function () {},
        "logNormal": function () {},
        "irwinHall": function () {}
    },
    "sum": function () {},
    "quantile": function () {},
    "shuffle": function () {},
    "transpose": function () {},
    "zip": function () {},
    "bisector": function () {},
    "bisectLeft": function () {},
    "bisectRight": function () {},
    "bisect": function () {},
    "nest": function () {},
    "keys": function () {},
    "values": function () {},
    "entries": function () {},
    "permute": function () {},
    "merge": function () {},
    "range": function () {},
    "requote": function () {},
    "round": function () {},
    "xhr": function () {},
    "text": function () {},
    "json": function () {},
    "html": function () {},
    "xml": function () {},
    "ns": {
        "prefix": {
            "svg": {},
            "xhtml": {},
            "xlink": {},
            "xml": {},
            "xmlns": {}
        },
        "qualify": function () {}
    },
    "dispatch": function () {},
    "format": function () {},
    "formatPrefix": function () {},
    "ease": function () {},
    "event": function () {},
    "transform": function () {},
    "interpolate": function () {},
    "interpolateNumber": function () {},
    "interpolateRound": function () {},
    "interpolateString": function () {},
    "interpolateTransform": function () {},
    "interpolateRgb": function () {},
    "interpolateHsl": function () {},
    "interpolateLab": function () {},
    "interpolateHcl": function () {},
    "interpolateArray": function () {},
    "interpolateObject": function () {},
    "interpolators": {
        "0": function () {},
        "1": function () {},
        "2": function () {},
        "3": function () {},
        "4": function () {}
    },
    "rgb": function () {},
    "hsl": function () {},
    "hcl": function () {},
    "lab": function () {},
    "selection": function () {},
    "select": function () {},
    "selectAll": function () {},
    "transition": function () {},
    "timer": function () {},
    "mouse": function () {},
    "touches": function () {},
    "scale": {
        "linear": function () {},
        "log": function () {},
        "pow": function () {},
        "sqrt": function () {},
        "ordinal": function () {},
        "category10": function () {},
        "category20": function () {},
        "category20b": function () {},
        "category20c": function () {},
        "quantile": function () {},
        "quantize": function () {},
        "threshold": function () {},
        "identity": function () {}
    },
    "svg": {
        "arc": function () {},
        "line": function () {},
        "area": function () {},
        "chord": function () {},
        "diagonal": function () {},
        "symbol": function () {},
        "symbolTypes": {
            "0": {},
            "1": {},
            "2": {},
            "3": {},
            "4": {},
            "5": {}
        },
        "axis": function () {},
        "brush": function () {}
    },
    "behavior": {
        "drag": function () {},
        "zoom": function () {}
    },
    "layout": {
        "bundle": function () {},
        "chord": function () {},
        "force": function () {},
        "partition": function () {},
        "pie": function () {},
        "stack": function () {},
        "histogram": function () {},
        "hierarchy": function () {},
        "pack": function () {},
        "cluster": function () {},
        "tree": function () {},
        "treemap": function () {}
    },
    "csv": function () {},
    "tsv": function () {},
    "geo": {
        "stream": function () {},
        "albersUsa": function () {},
        "albers": function () {},
        "azimuthalEqualArea": function () {},
        "azimuthalEquidistant": function () {},
        "bounds": function () {},
        "centroid": function () {},
        "circle": function () {},
        "equirectangular": function () {},
        "gnomonic": function () {},
        "graticule": function () {},
        "interpolate": function () {},
        "greatArc": function () {},
        "mercator": function () {},
        "orthographic": function () {},
        "path": function () {},
        "area": function () {},
        "projection": function () {},
        "projectionMutator": function () {},
        "stereographic": function () {}
    },
    "geom": {
        "hull": function () {},
        "polygon": function () {},
        "voronoi": function () {},
        "delaunay": function () {},
        "quadtree": function () {}
    },
    "time": {
        "format": function () {},
        "second": function () {},
        "seconds": function () {},
        "minute": function () {},
        "minutes": function () {},
        "hour": function () {},
        "hours": function () {},
        "day": function () {},
        "days": function () {},
        "dayOfYear": function () {},
        "sunday": function () {},
        "sundays": function () {},
        "sundayOfYear": function () {},
        "monday": function () {},
        "mondays": function () {},
        "mondayOfYear": function () {},
        "tuesday": function () {},
        "tuesdays": function () {},
        "tuesdayOfYear": function () {},
        "wednesday": function () {},
        "wednesdays": function () {},
        "wednesdayOfYear": function () {},
        "thursday": function () {},
        "thursdays": function () {},
        "thursdayOfYear": function () {},
        "friday": function () {},
        "fridays": function () {},
        "fridayOfYear": function () {},
        "saturday": function () {},
        "saturdays": function () {},
        "saturdayOfYear": function () {},
        "week": function () {},
        "weeks": function () {},
        "weekOfYear": function () {},
        "month": function () {},
        "months": function () {},
        "year": function () {},
        "years": function () {},
        "scale": function () {}
    }
};

d3.selection.prototype = {
  "select": function () {},
  "selectAll": function () {},
  "attr": function () {},
  "classed": function () {},
  "style": function () {},
  "property": function () {},
  "text": function () {},
  "html": function () {},
  "append": function () {},
  "insert": function () {},
  "remove": function () {},
  "data": function () {},
  "datum": function () {},
  "filter": function () {},
  "order": function () {},
  "sort": function () {},
  "on": function () {},
  "each": function () {},
  "call": function () {},
  "empty": function () {},
  "node": function () {},
  "transition": function () {}
};

d3.selection.enter = function () {};

// it's not clear if quadtree is working yet...
// i wish this didn't give two warnings when compiling...
//    commenting out for now
/*
d3.geom.quadtree = function() {
  function d3_geom_quadtreeNode() {
    return {
      leaf: true,
      nodes: [],
      point: null
    };
  }
  var root = d3_geom_quadtreeNode();
  root.add = function(p) {};
  root.visit = function(f) {};
  return root;
}
*/
