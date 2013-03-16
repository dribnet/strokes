var map = new L.Map("map", {
      attributionControl: false,
      center: [50, 50],
      zoom: 3,
      crs: L.CRS.Simple
    });

var svg = d3.select(map.getPanes().overlayPane).append("svg"),
    g = svg.append("g").attr("class", "leaflet-zoom-hide");

var bounds = [[0,0],[100,100]];

// var brown = g.append("svg:circle")
//     .attr("class", "la-circ");
var brown = g.append("rect")
    .attr("class", "la-circ");
 
map.on("viewreset", reset);
reset();

// Reposition the SVG to cover the features.
function reset() {
  var bottomLeft = project(bounds[0]),
      topRight = project(bounds[1]);

  console.log("reset: " + bottomLeft + " / " + topRight);

  svg .attr("width", topRight[0] - bottomLeft[0])
      .attr("height", bottomLeft[1] - topRight[1])
      .style("margin-left", bottomLeft[0] + "px")
      .style("margin-top", topRight[1] + "px");

  g   .attr("transform", "translate(" + -bottomLeft[0] + "," + -topRight[1] + ")");

  var center = project([50, 50]);
  var rad = center[0] - project([0, 50])[0];
  brown.attr("x", center[0] - rad)
    .attr("y", center[1] - rad)
    .attr("width", 2*rad)
    .attr("height", 2*rad)
    .attr("rx", 5)
    .attr("ry", 5)
  // feature.attr("d", path);
}

// Use Leaflet to implement a D3 geographic projection.
function project(x) {
  var point = map.latLngToLayerPoint(new L.LatLng(x[1], x[0]));
  return [point.x, point.y];
}
