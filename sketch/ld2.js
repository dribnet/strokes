var tiles = new L.TileLayer.Canvas({continuousWorld: true});

tiles.drawTile = function(canvas, tile, zoom) {
    var ctx = canvas.getContext('2d');

    ctx.fillStyle = 'white';
    ctx.fillRect(0, 0, 255, 255);


    ctx.fillStyle = 'black';
    ctx.fillText('x: ' + tile.x + ', y: ' + tile.y + ', zoom:' + zoom, 20, 20);
    ctx.fillText('LL: ' + this._map.layerPointToLatLng(tile), 20, 50);

    var tileCount = 1 << zoom;        
    var ReStart = 0;
    var ReDiff = 100;          
    var MinRe = ReStart + ReDiff * tile.x / tileCount;
    var MaxRe = MinRe + ReDiff / tileCount;                
    var ImStart = 0;
    var ImDiff = 100;        
    var MinIm = ImStart + ImDiff * tile.y / tileCount;
    var MaxIm = MinIm + ImDiff / tileCount;                

    ctx.fillText('rx: ' + MinRe + ' - ' + MaxRe, 20, 80);
    ctx.fillText('ry: ' + MinIm + ' - ' + MaxIm, 20, 90);

    ctx.strokeStyle = 'red';
    ctx.beginPath();
    ctx.moveTo(0, 0);
    ctx.lineTo(255, 0);
    ctx.lineTo(255, 255);
    ctx.lineTo(0, 255);
    ctx.closePath();
    ctx.stroke();
}

var map = new L.Map("map", {
      attributionControl: false,
      center: [50, 50],
      zoom: 3,
      layers: [tiles],
      crs: L.CRS.Simple
    });

var div = d3.select(map.getPanes().overlayPane).append("div"),
    g = div.append("iframe").attr("class", "leaflet-zoom-hide")
      .attr("src", "bar-clj-code.html");

var bounds = [[0,0],[100,100]];

// var brown = g.append("svg:circle")
//     .attr("class", "la-circ");
// var brown = g.append("rect")
//     .attr("class", "la-circ");
 
map.on("viewreset", reset);
reset();

// Reposition the SVG to cover the features.
function reset() {
  var bottomLeft = project(bounds[0]),
      topRight = project(bounds[1]);

  console.log("reset: " + bottomLeft + " / " + topRight);

  div .attr("width", topRight[0] - bottomLeft[0])
      .attr("height", bottomLeft[1] - topRight[1])
      .style("margin-left", bottomLeft[0] + "px")
      .style("margin-top", topRight[1] + "px");

  var bottom = project([0, 0]);
  var top = project([128, 80]);
  var width = top[0] - bottom[0];
  var height = top[1] - bottom[1];

  // g   .attr("transform", "translate(" + -bottomLeft[0] + "," + -topRight[1] + ")");
  // g   .attr("transform", "translate(" + -bottomLeft[0] + "," + -topRight[1] + ")");
  // g   .attr("transform", "scale(0.05, 0.05)");

  var transform = new WebKitCSSMatrix();
  // var transform = new WebKitCSSMatrix(window.getComputedStyle(g[0][0]).webkitTransform);
  //    // transform = transform.rotateAxisAngle(0,0,0,45)
     transform = transform.scale(0.25,0.25,0.25);
     g[0][0].style.webkitTransform = transform;

  // var center = project([50, 50]);
  // var rad = center[0] - project([0, 50])[0];
  // brown.attr("x", center[0] - rad)
  //   .attr("y", center[1] - rad)
  //   .attr("width", 2*rad)
  //   .attr("height", 2*rad)
  //   .attr("rx", 5)
  //   .attr("ry", 5)
  // feature.attr("d", path);
}

// Use Leaflet to implement a D3 geographic projection.
function project(x) {
  return x;
  // var point = map.latLngToLayerPoint(new L.LatLng(x[1], x[0]));
  // return [point.x, point.y];
}
