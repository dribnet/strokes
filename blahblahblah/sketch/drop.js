var nativeSize = [1024, 768];
var centerPoint = [nativeSize[0]/2, nativeSize[1]/2];

var div = d3.select("#infoi").append("div").attr("class", "coincident");

var current = div.append("iframe").attr("id", "current").attr("class", "leaflet-zoom-hide")
      .attr("src", "bar-clj-code.html");

var previous;
// var previous = div.append("iframe").attr("class", "leaflet-zoom-hide")
//       .attr("src", "bar-clj-code.html");
var next = div.append("iframe").attr("id", "next").attr("class", "leaflet-zoom-hide")
      .attr("src", "bar.html");

d3.select("body").on("keyup", function(e) {
  var code = d3.event.keyCode;
  console.log(code);
  if(code == 88) {
    // map.fitBounds(bounds);
  }
  else if(code == 90) {
    // map.fitBounds(zoomBounds);
  }
  else if (code == 32) { // space
      previous = current.attr("id", "previous");
      current = next.attr("id", "current");
      next = previous.attr("id", "next");    
  }
});

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

reset();

function reset() {
  var bottomLeft = [0, 768],
      topRight = [1024, 0];

  console.log("reset: " + bottomLeft + " / " + topRight);

  svg .attr("width", topRight[0] - bottomLeft[0])
      .attr("height", bottomLeft[1] - topRight[1])
      .style("margin-left", bottomLeft[0] + "px")
      .style("margin-top", topRight[1] + "px");

  console.log("BL: " + bottomLeft);
  // placeBackdrop(topRight, bottomLeft);
  placeBox(current, [50, 76+50], [102, 76], topRight, bottomLeft);
  placeBox(next, [200, 76+200], [102, 76], topRight, bottomLeft);
}

