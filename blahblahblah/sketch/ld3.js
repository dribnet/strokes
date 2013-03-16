
var map = new L.Map("map", {
      attributionControl: false,
      center: [50, 50],
      zoom: 2,
      crs: L.CRS.Simple,
      keyboard: false
    });

var svg = d3.select(map.getPanes().overlayPane).append("svg")
    // .attr("display", "none"),
    g = svg.append("g").attr("class", "leaflet-zoom-hide");

var div = d3.select(map.getPanes().overlayPane).append("div"),
    current = div.append("iframe").attr("id", "current").attr("class", "leaflet-zoom-hide")
      .attr("src", "bar-clj-code.html");

var previous;
// var previous = div.append("iframe").attr("class", "leaflet-zoom-hide")
//       .attr("src", "bar-clj-code.html");
var next = div.append("iframe").attr("id", "next").attr("class", "leaflet-zoom-hide")
      .attr("src", "bar.html");

var bounds = [[0,0],[100,100]];
var zoomBounds = [[0,0],[25,50]];
map.fitBounds(bounds);

d3.select("body").on("keyup", function(e) {
  var code = d3.event.keyCode;
  console.log(code);
  if(code == 88) {
    map.fitBounds(bounds);
  }
  else if(code == 90) {
    map.fitBounds(zoomBounds);
  }
  else if (code == 32) { // space
      previous = current.attr("id", "previous");
      current = next.attr("id", "current");
      next = previous.attr("id", "next");    
  }
});

// var brown = g.append("svg:circle")
//     .attr("class", "la-circ");
var brown = g.append("rect")
    .attr("class", "la-circ");
 
var smallr = g.append("rect")
    .attr("class", "small-rect");

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

  center = project([25, 12.5]);
  var radx = project([50, 25])[0] - center[0];
  var rady = center[1] - project([50, 25])[1];
  smallr.attr("x", center[0] - radx)
    .attr("y", center[1] - rady)
    .attr("width", 2*radx)
    .attr("height", 2*rady)
    .attr("rx", 5)
    .attr("ry", 5)
  // feature.attr("d", path);

  var mapSize = [30, 30];
  var mapScale = [topRight[0] - bottomLeft[0], bottomLeft[1] - topRight[1]];
  var targetSize = [mapSize[0] * mapScale[0] / 100, mapSize[1] * mapScale[1] / 100];
  var nativeSize = [1280, 800];
  // var targetSize = [100, 100];
  var scale = targetSize[0] / nativeSize[0];
  var overshootX = (nativeSize[0] - targetSize[0]) * 0.5;
  var overshootY = (nativeSize[1] - targetSize[1]) * 0.5;
  var isize = [50, 32.5];
  var bottom = project([0, 0]);
  // var top = project([128, 80]);
  var top = project(isize);
  var width = top[0] - bottom[0];
  var height = bottom[1] - top[1];
  var scalex = width / (isize[0]*20);
  var scaley = height / (isize[1]*20);
  console.log(bottom);

  // current.attr("style", function () {
      // return "-webkit-transform: translate3d(0, 0, 0) scale(1);";
  // });

  var translateStr = "translate3d(" + (bottomLeft[0]-overshootX) + "px," + (bottomLeft[1]-targetSize[1]-overshootY) + "px,0px)";
  current.transition().duration(200).attr("style", function () {
      // return "-webkit-transform: scale(1) translate3d(0px, 198px, 0px);";
      return "-webkit-transform: " + translateStr + " scale(" + scale + ");";
  });
  // current.style("transform", "translate(" + -bottomLeft[0] + "," + -topRight[1] + ")");

  // var transform = new WebKitCSSMatrix();
     // transform = transform.scale(0.5,0.5,1.0);
     // current[0][0].style.webkitTransform = transform;
    // ifr[0][0].style.webkitTransform = new WebKitCSSMatrix();
  // var transform = new WebKitCSSMatrix(window.getComputedStyle(ifr[0][0]).webkitTransform);
     // transform = transform.scale(scalex,scaley,1.0);
     // current[0][0].style.webkitTransform = transform;

  var transform = new WebKitCSSMatrix();
    // ifr[0][0].style.webkitTransform = new WebKitCSSMatrix();
  // var transform = new WebKitCSSMatrix(window.getComputedStyle(ifr[0][0]).webkitTransform);
     transform = transform.scale(scalex,scaley,1.0);
     transform = transform.translate(500,-1000,0);
     next[0][0].style.webkitTransform = transform;
}

// Use Leaflet to implement a D3 geographic projection.
function project(x) {
  // return x;
  var point = map.latLngToLayerPoint(new L.LatLng(x[1], x[0]));
  return [point.x, point.y];
}

var popup = L.popup();

function onMapClick(e) {
    popup
        .setLatLng(e.latlng)
        .setContent("You clicked the map at " + e.latlng.toString())
        .openOn(map);
}

map.on('click', onMapClick);
