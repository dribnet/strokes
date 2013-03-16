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

console.log(L.CRS.Simple);

var map = new L.Map('map', {
    center: new L.LatLng(0,0), 
    zoom: 8, 
    minZoom: 0,
    maxZoom: 16,
    layers: [tiles],
    attributionControl: false,    
    crs: L.CRS.Simple
});
