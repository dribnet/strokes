// var tiles = new L.TileLayer.Canvas({continuousWorld: true});

// tiles.drawTile = function(canvas, tile, zoom) {
//     var ctx = canvas.getContext('2d');

//     ctx.fillStyle = 'white';
//     ctx.fillRect(0, 0, 256, 256);

//     ctx.fillStyle = 'black';
//     // ctx.fillText('x: ' + tile.x + ', y: ' + tile.y + ', zoom:' + zoom, 20, 20);
//     // ctx.fillText('LL: ' + this._map.layerPointToLatLng(tile), 20, 50);

//     var tileCount = 1 << zoom;        
//     var XStart = 0;
//     var XDiff = 1024;          
//     var MinX = XStart + XDiff * tile.x / tileCount;
//     var MaxX = MinX + XDiff / tileCount;                
//     var YStart = 0;
//     var YDiff = 1024;        
//     var MinY = YStart + YDiff * tile.y / tileCount;
//     var MaxY = MinY + YDiff / tileCount;                

//     ctx.fillText('rx: ' + MinX + ' - ' + MaxX, 20, 80);
//     ctx.fillText('ry: ' + MinY + ' - ' + MaxY, 20, 90);

//     ctx.strokeStyle = '#eeeee2';
//     ctx.beginPath();
//     ctx.moveTo(0, 0);
//     ctx.lineTo(255, 0);
//     ctx.lineTo(255, 255);
//     ctx.lineTo(0, 255);
//     ctx.closePath();
//     ctx.stroke();
// }

var map = new L.Map("map", {
      attributionControl: false,
      center: [50, 50],
      zoom: 2,
      // layers: [tiles],
      crs: L.CRS.Simple
    });

var svg = d3.select(map.getPanes().overlayPane).append("svg"),
    g = svg.append("g").attr("class", "leaflet-zoom-hide");

var bounds = [[0,0],[100,100]];

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

  center = project([25, 25]);
  rad = center[0] - project([0, 25])[0];
  smallr.attr("x", center[0] - rad)
    .attr("y", center[1] - rad)
    .attr("width", 2*rad)
    .attr("height", 2*rad)
    .attr("rx", 5)
    .attr("ry", 5)
  // feature.attr("d", path);
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
