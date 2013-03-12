var tiles = new L.TileLayer.Canvas({continuousWorld: true});

// http://stackoverflow.com/questions/4467539/javascript-modulo-not-behaving
Number.prototype.mod = function(n) {
    return ((this%n)+n)%n;
}

var cachedRandomConstant = Math.pow(2, 13)+1;
var cachedRandomMaximum = Math.pow(2, 50);

// adapted from http://michalbe.blogspot.com/2011/02/javascript-random-numbers-with-custom.html
var CustomRandom = function(x,y,s) {  
  
    var seed,  
        constant = cachedRandomConstant;
        prime = 37,  
        maximum = cachedRandomMaximum;

    x += 10000;
    y += 10000;
   
    x = (x < 0) ? -x : x;
    y = (y < 0) ? -y : y;

    seed = (x * y * 13 + 1) + ((y & x) * 13 + 1) + s;

    var nextfn = function() {  
            seed *= constant;  
            seed += prime;  
            seed %= maximum;  
              
            return ~~(1024 * seed / maximum);
        }  
   
    for(var i=0;i<6;i++) {
        nextfn();
    }

    return {  
        next : nextfn
    }  
}

var getPointAlignedToGrid = function (x, y, s) {
    var gx = x - x.mod(s);
    var gy = y - y.mod(s);
    return [gx, gy];
}

var rememberCellProperty = function (map, x, y, s, k, v) {
    if(!map[s]) map[s] = {};
    if(!map[s][x]) map[s][x] = {};
    if(!map[s][x][y]) map[s][x][y] = {};
    map[s][x][y][k] = v;
}

var recallCellProperty = function (map, x, y, s, k) {
    if(!map[s]) return undefined;
    if(!map[s][x]) return undefined;
    if(!map[s][x][y]) return undefined;
    return map[s][x][y][k];
}

var LINE = 1;
var CROSS = 2;
var POOL = 3;

var indexSizeTable = {
    14: 8192,
    15: 16384,
    16: 32768,
    17: 65536
};


var cityGroup = [
        {index: 17, size: 65536, thresh: 40, grow: LINE, minDrawSize: 8,
            stretch: 3, colors:["#f50603", "#dba300", "#5b5c94", "#dfc4bd"]},
        {index: 16, size: 32768, thresh: 18, grow: LINE, minDrawSize: 8,
            stretch: 3, colors:["#f50603", "#dba300", "#5b5c94", "#dfc4bd"]},
        {index: 15, size: 16384, thresh: 30, grow: LINE, minDrawSize: 8,
            stretch: 4, colors:["#f50603", "#dba300", "#5b5c94"]},
        // red, yellow, black, blue, grey
        {index: 14, size: 8192, thresh: 14, grow: LINE,  minDrawSize: 8,
            stretch: 18, colors:["#f50603", "#dba300", "#291f20", "#5b5c94", "#dfc4bd"]}
    ];

var peopleGroup = [
        {index: 11, size: 1024, thresh: 2, grow: LINE,  minDrawSize: 4,
            stretch: 2, colors:["#000000", "#dddddd", "#bbbbbb", "#999999"]}
    ];

var outerGroup = [
        {index: 28, size: 134217728, thresh: 30, grow: LINE, minDrawSize: 8,
            stretch: 4, colors:["#7777ee"]},
        {index: 27, size: 67108864, thresh: 30, grow: LINE, minDrawSize: 8,
            stretch: 2, colors:["#8888ee"]},
        {index: 25, size: 16777216, thresh: 8, grow: POOL, minDrawSize: 8,
            stretch: 4, colors:["#dfc4bd", "#c4dfbd"]},
        {index: 24, size: 8388608, thresh: 8, grow: POOL, minDrawSize: 8,
            stretch: 4, colors:["#dfc4bd", "#c4dfbd"]},
        {index: 22, size: 2097152, thresh: 8, grow: POOL, minDrawSize: 8,
            stretch: 5, colors:["#dfc4bd", "#c4dfbd"]},
        {index: 23, size: 4194304, thresh: 2, grow: POOL,  minDrawSize: 4,
            stretch: 2, colors:["#fcfffc"]},
        {index: 19, size: 262144, thresh: 5, grow: CROSS, minDrawSize: 8,
            stretch: 5, colors:["#f50603"], outcolors:["#46f543", "#63f546"]},
        {index: 18, size: 131072, thresh: 20, grow: LINE, minDrawSize: 8,
            stretch: 2, colors:["#f50603", "#dba300"], outcolors:["#06f503", "#13f506"]},
    ];

/* 12 zooms deep */
var layerGroups = [
    outerGroup,
    cityGroup,
    peopleGroup
];

// var map = {};

// checks layers from low to high (cycle index) and returns true if available at level s
var pointIsClear = function(x, y, s, low, cycleHigh) {

}

var growSeed = function(c, x1, y1, scalex, scaley, rects, map, cy) {
    var colors = cy.colors;
    if(cy.grow == POOL) {
        var main_rng = CustomRandom(c.x, c.y, cy.size); 
        cindex = main_rng.next() % cy.colors.length;
        var poolColor = colors[cindex];
        for(i=0-c.extent1;i<c.extent2;i++) {
            for(j=0-c.extent1;j<c.extent2;j++) {
                r = {};
                r.color = poolColor;
                r.rect = [(c.x-x1+i*cy.size)*scalex, (c.y-y1+j*cy.size)*scaley, cy.size*scalex, cy.size*scaley];
                rects.push(r);                
                rememberCellProperty(map, (c.x+i*cy.size), (c.y+j*cy.size), cy.size, "active", true);
            }
        }
    }
    if(cy.grow == CROSS || (cy.grow == LINE && c.dir)) {
        for(i=0-c.extent1;i<c.extent2;i++) {
            var rng = CustomRandom((c.x+i*cy.size), c.y, cy.size); 
            r = {};
            cindex = rng.next() % cy.colors.length;
            // cindex = rng.next() < 512 ? 0 : 1;
            // console.log("cindex is " + cindex);
            // c.color= cy.color;
            var abort = false;
            if(cy.index >= 14 && cy.index <= 17) {
                // lookup out of city colors
                var gridPoint = getPointAlignedToGrid((c.x+i*cy.size), c.y, 4194304);
                var onCity = recallCellProperty(map, gridPoint[0], gridPoint[1], 4194304, "active");
                if(onCity) {
                    for(var l=14; !abort && l<18; l++) {
                        var cellSize = indexSizeTable[l];
                        gridPoint = getPointAlignedToGrid((c.x+i*cy.size), c.y, cellSize);
                        var onOther = recallCellProperty(map, gridPoint[0], gridPoint[1], cellSize, "active");
                        if(onOther) {
                            abort = true;
                        }
                    }
                }
                if(cy.index == 19 || cy.index == 18) {
                    if(onCity && cy.index == 19) {
                        abort = true;
                    }
                    if(!onCity) {
                        colors = cy.outcolors;                    
                    }
                    // else if(!onCity && cy == 18) {
                    //     colors = cy.outcolors;
                    // }
                }                
            }
            if(!abort) {            
                r.color = colors[cindex];
                r.rect = [(c.x-x1+i*cy.size)*scalex, (c.y-y1)*scaley, cy.size*scalex, cy.size*scaley];
                rects.push(r);                
                rememberCellProperty(map, (c.x+i*cy.size), c.y, cy.size, "active", true);
            }
        }
    }
    if(cy.grow == CROSS || (cy.grow == LINE && !c.dir)) {
        for(j=0-c.extent1;j<c.extent2;j++) {
            var rng = CustomRandom(c.x, (c.y+j*cy.size), cy.size); 
            r = {};
            cindex = rng.next() % cy.colors.length;
            // cindex = rng.next() < 512 ? 0 : 1;
            // console.log("cindex is " + cindex);
            // c.color= cy.color;
            var abort = false;
            if(cy.index >= 14 && cy.index <= 17) {
                // lookup out of city colors
                var gridPoint = getPointAlignedToGrid(c.x, (c.y+j*cy.size), 4194304);
                var onCity = recallCellProperty(map, gridPoint[0], gridPoint[1], 4194304, "active");
                if(onCity) {
                    for(var l=14; !abort && l<18; l++) {
                        var cellSize = indexSizeTable[l];
                        gridPoint = getPointAlignedToGrid(c.x, (c.y+j*cy.size), cellSize);
                        var onOther = recallCellProperty(map, gridPoint[0], gridPoint[1], cellSize, "active");
                        if(onOther) {
                            abort = true;
                        }
                    }
                }
                if(cy.index == 19 || cy.index == 18) {
                    if(onCity && cy.index == 19) {
                        abort = true;
                    }
                    if(!onCity) {
                        colors = cy.outcolors;                    
                    }
                }
            }
            if(!abort) {            
                r.color = colors[cindex];
                r.rect = [(c.x-x1)*scalex, (c.y-y1+j*cy.size)*scaley, cy.size*scalex, cy.size*scaley];
                rects.push(r);                
                rememberCellProperty(map, c.x, (c.y+j*cy.size), cy.size, "active", true);
            }
        }
    }
    // console.log(c.tstart);
}

var getRectsIn = function(x1, y1, x2, y2, s) {
    // console.log("RECTSIN : " + x1 + "," + y1 + "," + x2 + "," + y2 + "," + s);
    // console.log("RECTDIFF : " + (x2 - x1) + "," + (y2 - y1));
    var rects = [];
    var hs = s / 2;

    var temp;
    if(x2 < x1) {
        temp = x2;
        x2 = x1;
        x1 = temp;
    }
    if(y2 < y1) {
        temp = y2;
        y2 = y1;
        y1 = temp;
    }

    var scalex = s / (x2 - x1);
    var scaley = s / (y2 - y1);

    var map = {};

    layerGroups.forEach(function(lg) {
        // todo: runlist should exist across some layers
        var runlist = SortedList.create({
            compare:  function(a,b) {
                if(a.tstart != b.tstart)
                    return a.tstart - b.tstart
                if(a.x != b.x)
                    return a.x - b.x
                return a.y - b.y
            }
        });

        lg.forEach(function(cy) {
            // short circuit out if features are too small
            if(cy.size * scalex < cy.minDrawSize) {
                return;
            }

            var maxstretch = cy.stretch;

            // iteration bounds
            var gridPoint = getPointAlignedToGrid(x1, y1, cy.size);
            var dx = cy.size;
            var dy = cy.size;
            var size = cy.size;
            xmin = gridPoint[0] - (maxstretch * dx);
            ymin = gridPoint[1] - (maxstretch * dy);
            gridPoint = getPointAlignedToGrid(x2, y2, cy.size);
            xmax = gridPoint[0] + (maxstretch * dx) + dx;
            ymax = gridPoint[1] + (maxstretch * dy) + dy;

            var i, j, n;
            var c = {};

            // console.log("xbounds: " + xmin + "," + xmax);

            // first pass, determine the grid of seeds
            // the cells themselves
            var grid = new Array((xmax-xmin)/dx);
            // ordered list to run them later
            // https://github.com/shinout/SortedList

            var Nthresh = cy.thresh;

            var stepx = 0;
            for(i=xmin; i<xmax; i+=dx) {
                grid[stepx] = new Array((ymax-ymin)/dy);
                var stepy = 0;
                for(j=ymin; j<ymax; j+=dy) {
                    var cellSkip = false;
                    // water filter
                    if(!cellSkip && cy.index == 27) {
                        // lookup water skip
                        var gridPoint = getPointAlignedToGrid(i, j, 134217728);
                        if(!recallCellProperty(map, gridPoint[0], gridPoint[1], 134217728, "active"))
                            cellSkip = true;
                    }
                    // nothing else on water for now
                    if(!cellSkip && cy.index > 20 && cy.index < 27) {
                        // lookup water skip
                        var gridPoint = getPointAlignedToGrid(i, j, 134217728);
                        if(recallCellProperty(map, gridPoint[0], gridPoint[1], 134217728, "active"))
                            cellSkip = true;
                    }
                    // don't cover up cities before layer 18
                    if(!cellSkip && cy.index > 20 && cy.index < 23) {
                        // lookup city skip
                        var gridPoint = getPointAlignedToGrid(i, j, 4194304);
                        if(recallCellProperty(map, gridPoint[0], gridPoint[1], 4194304, "active"))
                            cellSkip = true;
                    }
                    // city filter
                    if(!cellSkip && cy.index < 18) {
                        // lookup city skip
                        var gridPoint = getPointAlignedToGrid(i, j, 4194304);
                        if(!recallCellProperty(map, gridPoint[0], gridPoint[1], 4194304, "active"))
                            cellSkip = true;
                    }
                    if(!cellSkip) {
                        var rng = CustomRandom(i, j, cy.size);
                        var n = rng.next();
                        if(n < Nthresh) {
                            c = {};
                            c.cy = cy;
                            c.x = i;
                            c.y = j;
                            c.size = scalex;
                            c.dir = rng.next() < 512 ? 0 : 1;
                            c.tstart = rng.next();
                            c.extent1 = rng.next().mod(cy.stretch);
                            c.extent2 = rng.next().mod(cy.stretch);
                            grid[stepx][stepy] = c;
                            runlist.insert(c);
                        }
                    }
                    stepy += 1;
                }
                stepx += 1;
            }
        })

        var r = {};
        // console.log("---");
        for(n=0;n<runlist.length;n++) {
            growSeed(runlist[n], x1, y1, scalex, scaley, rects, map, runlist[n].cy);
        }
    })

    return rects;
}

tiles.drawTile = function(canvas, tile, zoom) {
    var ctx = canvas.getContext('2d');

    // ctx.fillStyle = '#fefef2';
    ctx.fillStyle = '#eeeee2';
    ctx.fillRect(0, 0, 256, 256);

    ctx.fillStyle = 'black';
/*
    ctx.fillText('x: ' + tile.x + ', y: ' + tile.y + ', zoom:' + zoom, 20, 20);
    ctx.fillText('LL: ' + this._map.layerPointToLatLng(tile), 20, 50);
*/
    var tileCount = 1 << zoom;        
    var XStart = 0;
    var XDiff = 268435456;          
    var MinX = XStart + XDiff * tile.x / tileCount;
    var MaxX = MinX + XDiff / tileCount;                
    var YStart = 0;
    var YDiff = 268435456;        
    var MinY = YStart + YDiff * tile.y / tileCount;
    var MaxY = MinY + YDiff / tileCount;                

    // console.log("MaxX: " + MaxX);

    var rects = getRectsIn(MinX, MinY, MaxX, MaxY, 256);

    rects.forEach(function (r) {
        ctx.fillStyle=r.color;
        // if(r.rect[2] == 8) {
        //     ctx.globalAlpha = 0.3;
        // }
        // else {
        //     ctx.globalAlpha = 1.0;            
        // }
        // console.log("Drawing " + r.rect);
        ctx.fillRect.apply(ctx, r.rect);
    });
    // ctx.fillRect.apply(ctx, [ -40, -40, 100, 100]);
}

var MyCustomLayer = L.Class.extend({
    initialize: function (latlng, size) {
        // save position of the layer or any options from the constructor
        this._latlng = new L.LatLng(39.96094, -37.90723);
        this._scale = 1;
    },

    onAdd: function (map) {
        this._map = map;

        // create a DOM element and put it into one of the map panes
        this._el = L.DomUtil.create('div', 'my-custom-layer leaflet-zoom-hide');
        this._el.innerHTML = "Hello World";
        // this._el.setAttribute('style', "font-size: " + this._size + "px;");
        map.getPanes().overlayPane.appendChild(this._el);

        // add a viewreset event listener for updating layer's position, do the latter
        map.on('viewreset', this._reset, this);
        this._reset();
    },

    onRemove: function (map) {
        // remove layer's DOM elements and listeners
        map.getPanes().overlayPane.removeChild(this._el);
        map.off('viewreset', this._reset, this);
    },

    _reset: function () {
        // update layer's position
        var pos = this._map.latLngToLayerPoint(this._latlng);
        // this._el.setAttribute('style', "font-size: " + _this._size + "px;");
        // elem.setAttribute('style', styleString);

        this._scale *= 2;
        var s = this._scale;
        // var scaleStr = ";scale(" + s + "," + s + ");"
        // var scaleStr = "-webkit-transform: scale(" + s + "," + s + ");"
        // var point = new L.Point(200, 300);
        // var scaleStr = L.DomUtil.getScaleString(this._scale, point)
        // console.log(scaleStr);
        L.DomUtil.setPosition(this._el, pos);

        var transform = new WebKitCSSMatrix(window.getComputedStyle(this._el).webkitTransform);
           // transform = transform.rotateAxisAngle(0,0,0,45)
           transform = transform.scale(s,s,s);
           this._el.style.webkitTransform = transform;

        // var curst = this._el.getAttribute('style');
        // this._el.setAttribute('style', curst + ";" + scaleStr);

    }
});

var map = new L.Map('map', {
    center: new L.LatLng(39.96094, -37.90723), 
    zoom: 10, 
    minZoom: 0,
    maxZoom: 14,
    layers: [],
    attributionControl: false,    
    crs: L.CRS.Simple
});

map.addLayer(new MyCustomLayer([39.96094, -37.90723], 64));

var popup = L.popup();

function onMapClick(e) {
    popup
        .setLatLng(e.latlng)
        .setContent("You clicked the map at " + e.latlng.toString())
        .openOn(map);
}

map.on('click', onMapClick);
