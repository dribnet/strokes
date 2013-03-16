var nativeSize = [1024, 768];
var centerPoint = [nativeSize[0]/2, nativeSize[1]/2];

var map = new L.Map("map", {
      attributionControl: false,
      center: [nativeSize[1]/2, nativeSize[0]/2],
      zoom: 2,
      crs: L.CRS.Simple,
      keyboard: false
    });

var svg = d3.select(map.getPanes().overlayPane).append("svg")
    // .attr("display", "none"),
    g = svg.append("g").attr("class", "leaflet-zoom-hide");

var div = d3.select(map.getPanes().overlayPane).append("div");

var backdrop = div.append("iframe").attr("id", "current").attr("class", "leaflet-zoom-hide")
      .attr("src", "backdrop.html");

var current = div.append("iframe").attr("id", "current").attr("class", "leaflet-zoom-hide")
      .attr("src", "bar-clj-code.html");

var previous;
// var previous = div.append("iframe").attr("class", "leaflet-zoom-hide")
//       .attr("src", "bar-clj-code.html");
var next = div.append("iframe").attr("id", "next").attr("class", "leaflet-zoom-hide")
      .attr("src", "bar.html");

var bounds = [[0,0],[768,1024]];
var svgbounds = [[0,0],[1024,768]];
var zoomBounds = [[768-76-50, 50],[768-76-50+76, 50+102]];
// var zoomBounds = [[0,0],[102,76]];
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

var nativeSize = [1024, 768];

var placeBox = function(boxSel, boxPos, boxSize, topRight, bottomLeft) {
  var mapScale = [topRight[0] - bottomLeft[0], bottomLeft[1] - topRight[1]];
  var targetSize = [boxSize[0] * mapScale[0] / nativeSize[0], boxSize[1] * mapScale[1] / nativeSize[1]];
  // var targetSize = [100, 100];
  var scale = targetSize[0] / nativeSize[0];
  var overshootX = (nativeSize[0] - targetSize[0]) * 0.5;
  var overshootY = (nativeSize[1] - targetSize[1]) * 0.5;
  // var overshootX = targetSize[0] * 0.5;
  // var overshootY = targetSize[1] * 0.5;
  
  // current.attr("style", function () {
      // return "-webkit-transform: translate3d(0, 0, 0) scale(1);";
  // });

  var translateStr = "translate3d(" + (boxPos[0]-overshootX) + "px," + (boxPos[1]-targetSize[1]-overshootY) + "px,0px)";
  boxSel.transition().duration(200).attr("style", function () {
      // return "-webkit-transform: scale(1) translate3d(0px, 198px, 0px);";
      return "-webkit-transform: " + translateStr + " scale(" + scale + ");";
  });
}

var placeBackdrop = function(topRight, bottomLeft) {
  // var mapScale = [topRight[0] - bottomLeft[0], bottomLeft[1] - topRight[1]];

  // current.attr("style", function () {
      // return "-webkit-transform: translate3d(0, 0, 0) scale(1);";
  // });

  var translateStr = "translate3d(" + (bottomLeft[0]) + "px," + (bottomLeft[1]-nativeSize[1]) + "px,0px)";
  backdrop.transition().duration(500).attr("style", function () {
      // return "-webkit-transform: scale(1) translate3d(0px, 198px, 0px);";
      return "-webkit-transform: " + translateStr + ";";
  });  
}

reset();

// Reposition the SVG to cover the features.
function reset() {
  var bottomLeft = project(svgbounds[0]),
      topRight = project(svgbounds[1]);

  console.log("reset: " + bottomLeft + " / " + topRight);

  svg .attr("width", topRight[0] - bottomLeft[0])
      .attr("height", bottomLeft[1] - topRight[1])
      .style("margin-left", bottomLeft[0] + "px")
      .style("margin-top", topRight[1] + "px");

  g   .attr("transform", "translate(" + -bottomLeft[0] + "," + -topRight[1] + ")");

  var border = 0;
  var origin = project([0, 0]);
  var center = project(centerPoint);
  var radx = center[0] - origin[0];
  var rady = origin[1] - center[1];
  console.log("is:" + radx + "," + rady)
  brown.attr("x", center[0] - radx)
    .attr("y", center[1] - rady)
    .attr("width", 2*radx)
    .attr("height", 2*rady)
    .attr("rx", 30)
    .attr("ry", 30)

  // center = project([25, 12.5]);
  // var radx = project([50, 25])[0] - center[0];
  // var rady = center[1] - project([50, 25])[1];
  // smallr.attr("x", center[0] - radx)
  //   .attr("y", center[1] - rady)
  //   .attr("width", 2*radx)
  //   .attr("height", 2*rady)
  //   .attr("rx", 5)
  //   .attr("ry", 5)
  // feature.attr("d", path);

  console.log("BL: " + bottomLeft);
  // placeBackdrop(topRight, bottomLeft);
  placeBox(backdrop, bottomLeft, [1024, 768], topRight, bottomLeft)
  placeBox(current, project([50, 768-76-50]), [102, 76], topRight, bottomLeft)

  // current.style("transform", "translate(" + -bottomLeft[0] + "," + -topRight[1] + ")");

  // var transform = new WebKitCSSMatrix();
     // transform = transform.scale(0.5,0.5,1.0);
     // current[0][0].style.webkitTransform = transform;
    // ifr[0][0].style.webkitTransform = new WebKitCSSMatrix();
  // var transform = new WebKitCSSMatrix(window.getComputedStyle(ifr[0][0]).webkitTransform);
     // transform = transform.scale(scalex,scaley,1.0);
     // current[0][0].style.webkitTransform = transform;

  // var transform = new WebKitCSSMatrix();
  //   // ifr[0][0].style.webkitTransform = new WebKitCSSMatrix();
  // // var transform = new WebKitCSSMatrix(window.getComputedStyle(ifr[0][0]).webkitTransform);
  //    transform = transform.scale(scalex,scaley,1.0);
  //    transform = transform.translate(500,-1000,0);
  //    next[0][0].style.webkitTransform = transform;
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
