//---------------- stats functions

function getLinearCategoriesLimits(min, max, numCategories) {
	var list = [];
	var amplitude = max-min;
	var interval = amplitude/numCategories;
	for(var i = 0;i<=numCategories;i++) {
		list.push(min+interval*i);
	}
	return list;
}

function getLogCategoriesLimits(min, max, numCategories) {
	var negativeDiff = 0;
	if(min<1) {
		negativeDiff = min-1;
	}
	var newMin = Math.log(min-negativeDiff);
	var newMax = Math.log(max-negativeDiff);
	var list = [];
	
	var amplitude = newMax-newMin;
	var interval = amplitude/numCategories;
	for(var i = 0;i<=numCategories;i++) {
		list.push(Math.exp(newMin+interval*i)+negativeDiff);
	}
	return list;
}

function max(series) {
	var max = -Number.MAX_VALUE;
	for(var i in series) {
		if(series[i]>max) { max = series[i]; }
	}
	return max;
}

function min(series) {
	var min = Number.MAX_VALUE;
	for(var i in series) {
		//console.log("i:"+i+" / "+series[i]);
		if(series[i]<min) { min = series[i]; }
	}
	return min;
}

function hexString(number) {
	var hex = number.toString(16);
	if(hex.length==1) { hex = "0"+hex; }
	return hex;
}

//----

var DEFAULT_FILL_COLOR = "#cccccc";

function normalizeNum(float) {
	return Math.round(float * 100);
	//return float; //float.toFixed(0);
}

function getCat(value, catData) {
	value = normalizeNum(value);
	for(id in catData) {
		//$("#debug").append("id: "+id+"; v:"+value+":"+catData[id].startval+"-"+catData[id].endval+"\n");
		if(value >= normalizeNum(catData[id].startval) && value <= normalizeNum(catData[id].endval)) {
			return id;
		}
	}
	return null;
}

function genCategoriesFromLimits(vals) {
	var cats = {};
	for(var i=1;i<vals.length;i++) {
		var cat = {};
		cat.startval = vals[i-1];
		cat.endval = vals[i];
		//cat.name = getNameFromId(i);
		cat.styleId = i;
		//cats.push(cat);
		cats[i] = cat;
	}
	return cats;
}

function procStylesFromCategories(cats, colorFrom, colorTo, valueLabel) {
	//console.log(colorFrom);
	//console.log(colorTo);

	var numCat = Object.keys(cats).length - 1;
	var colorsA = getLinearCategoriesLimits(parseInt(colorFrom.substring(0, 2), 16), parseInt(colorTo.substring(0, 2), 16), numCat);
	var colorsB = getLinearCategoriesLimits(parseInt(colorFrom.substring(2, 4), 16), parseInt(colorTo.substring(2, 4), 16), numCat);
	var colorsG = getLinearCategoriesLimits(parseInt(colorFrom.substring(4, 6), 16), parseInt(colorTo.substring(4, 6), 16), numCat);
	var colorsR = getLinearCategoriesLimits(parseInt(colorFrom.substring(6, 8), 16), parseInt(colorTo.substring(6, 8), 16), numCat);
	
	//console.log(colorsA);
	//console.log(colorsB);
	//console.log(colorsG);
	//console.log(colorsR);

	var i=0;
	for(var c in cats) {
		cats[c].kmlcolor = hexString(Math.round(colorsA[i])) + hexString(Math.round(colorsB[i])) + hexString(Math.round(colorsG[i])) + hexString(Math.round(colorsR[i]));
		//XXX: color -> rgbcolor?
		cats[c].color = hexString(Math.round(colorsR[i])) + hexString(Math.round(colorsG[i])) + hexString(Math.round(colorsB[i]));
		//TODO: format numbers! integer, float, ...
		cats[c].description = cats[c].startval + " &lt; # " + valueLabel + " &lt; " + cats[c].endval;
		//console.log('cat: '+c+'/'+colorsA[i]+'/'+colorsB[i]);
		//console.log(cats[c].kmlcolor);
		i++;
	}
	
	return cats;
}

/*function applyStyleColor(gPlaceMarks, cats) {
	var count = 0;
	for(var id in gPlaceMarks) {
		var placemark = gPlaceMarks[id];
		//var bef = placemark.fillColor;
		
		placemark.catId = getCat(placemark.dataValue, cats);
		var cat = cats[placemark.catId];
		if(cat==undefined) {
			console.warn('undefined id: '+id+' / '+placemark+' / '+placemark.catId);
			continue;
		}
		placemark.kmlColor = cats[placemark.catId].kmlcolor;
		placemark.fillColor = placemark.kmlColor.substring(6,8) + placemark.kmlColor.substring(4,6) + placemark.kmlColor.substring(2,4);
		//console.log('b: '+bef+' / a: '+placemark.fillColor);
		placemark.setMap(map); //atualiza placemark no mapa - 'null' retira elemento do mapa
		count++;
		//if(count>10) { break; }
	}
	//console.log(count+' / '+Object.keys(gmapsPlaces).length+' / '+Object.keys(places).length);
	//console.log(gmapsPlaces);
}*/

function applySeriesDataAndStyle(gPlaceMarks, seriesData, catData, map) {
	var count = 0;
	for(var id in gPlaceMarks) {
		var placemark = gPlaceMarks[id];
		
		//set data
		placemark.dataValue = seriesData.series[id];
		placemark.catId = getCat(placemark.dataValue, catData);
		//TODO: numberFormat (grouping char, ...)
		placemark.description = seriesData.valueLabel + ': '+seriesData.series[id] + ' ' + seriesData.measureUnit;
		if(placemark.catId==undefined) {
			console.warn('undefined cat: '+id+' / '+placemark.name); //+' / '+placemark.catId);
			placemark.fillColor = DEFAULT_FILL_COLOR;
			placemark.setMap(map);
			//TODO: option to remove element from map
			continue;
		}
		
		//set style & map
		placemark.kmlColor = catData[placemark.catId].kmlcolor;
		placemark.fillColor = catData[placemark.catId].color;
		//placemark.fillColor = placemark.kmlColor.substring(6,8) + placemark.kmlColor.substring(4,6) + placemark.kmlColor.substring(2,4);
		//TODO: add category in description
		
		//console.log('b: '+bef+' / a: '+placemark.fillColor);
		placemark.setMap(map); //atualiza placemark no mapa - 'null' retira elemento do mapa
		
		placemark.listener = google.maps.event.addListener(placemark, 'click', function(event) {
			//console.log('click!');
			//console.log(placemark);
			//console.log(this);
			showPlaceInfo(this.id, this.name, this.description);
		});		
		
		count++;
	}
	console.log('applySeriesDataCount: '+count);
}

function removeSeriesDataWhenPlacemarkNotFound(gPlaceMarks, seriesData) {
	var count = 0;
	console.log('before: '+Object.keys(seriesData.series).length+" / "+Object.keys(gPlaceMarks).length);
	for(var id in seriesData.series) {
		//console.log(gPlaceMarks[id]);
		if(gPlaceMarks[id]==undefined) { delete seriesData.series[id]; }
	}
	console.log('after: '+Object.keys(seriesData.series).length+" / "+Object.keys(gPlaceMarks).length);
}

function showPlaceInfo(id, name, description) {
	//TODO: do not show id (?); do not show name if null
	//console.log(id+" / "+name);
	//console.log(placemark);
	document.getElementById('placeId').innerHTML = id;
	document.getElementById('placeName').innerHTML = name;
	document.getElementById('placeDesc').innerHTML = description;
	if(name==null) {
		document.getElementById('placeName').style.display = 'none';
		document.getElementById('placeNameLabel').style.display = 'none';
	}
	else {
		document.getElementById('placeName').style.display = 'inherit';
		document.getElementById('placeNameLabel').style.display = 'inherit';
	}
	document.getElementById('place_info').style.display = 'block';
}
