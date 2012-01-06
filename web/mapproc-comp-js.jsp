<%@ page import="java.util.*, tbrugz.mapproc.*" %>
<html>
<head>
<title>MapProc: map processor</title>
<link rel="stylesheet" type="text/css" href="css/mapproc.css" />
<script type="text/javascript" src="js/jquery-1.7.js"></script>
<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?sensor=false"></script>
<script type="text/javascript" src="js/mapproc.js"></script>
<script type="text/javascript" src="js/mapproc.sa.js"></script>
<script type="text/javascript" src="js/jscolor/jscolor.js"></script>
<%@ include file="fragments/analytics.html" %>
<script type="text/javascript">
//TODO: constants for map center position
var places, catData, seriesData;
var gmapsPlaces = {};
var map;

function loadNakedMap(map_canvas_id) {
	var jqxhrPlaces = $.getJSON("input/json/map-35-mun.json", function(data) {
		//okNumber++;
		places = data;
		loadNakedMapCallback(map_canvas_id);
		//if(okNumber==3) { doIt(); }
	})
	.error(function() { alert("error Places"); })
}
	
function loadNakedMapCallback(map_canvas_id) {
	var debug = document.getElementById('debug');
	
	var myOptions = {
			zoom: 6,
			//TODO: posicionamento automatico... ?
			position: new google.maps.LatLng(-22, -48),
			mapTypeId: google.maps.MapTypeId.ROADMAP
		};

	map = new google.maps.Map(document.getElementById(map_canvas_id), myOptions);
	map.setCenter(myOptions.position);
	
	//[new google.maps.LatLng(25.774252, -80.190262)];
	
	var count = 0;
	for(id in places) {
		//debug.innerHTML += "\n\n>> "+places[id].name+":: ";
		var coords = new Array();
		//$("#debug").append("["+$.dump(places[id].coordinates)+"]");
		for(c in places[id].coordinates) {
			coords.push( new google.maps.LatLng(places[id].coordinates[c][0], places[id].coordinates[c][1]) );
		}
		var theplace = new google.maps.Polygon({
			paths: coords,
			strokeColor: "#444444",
			strokeOpacity: 0.8,
			strokeWeight: 1,
			fillColor: DEFAULT_FILL_COLOR,
			fillOpacity: 0.7
		});
		theplace.id = id;
		theplace.name = places[id].name;
		theplace.description = places[id].description;
		theplace.setMap(map);
		gmapsPlaces[theplace.id] = theplace;
		count++;
		//if(count>10) { break; }
	}
	
	//return map;
}

function loadData() {
	var dataUrl = document.getElementById('dataUrl').value;
	var jqxhrSeries = $.getJSON(dataUrl, function(data) {
		//okNumber++;
		seriesData = data;
		loadDataCallback();
		//if(okNumber==3) { doIt(); }
	})
	.error(function() { alert("error Series"); })
}

function createCatElements() {
	//TODO: double-clickable cat elements (shows only elements from thar category) - .class for placemarks - only change clas style
	//console.log(catData);
	var ncats = Object.keys(catData).length;
	//var divLimits = getLinearCategoriesLimits(0, 100, ncats);
	var catheight = 100/ncats;
	var container = document.getElementById('categories_canvas');
	while (container.hasChildNodes()) {
		container.removeChild(container.lastChild);
	}
	//console.log(ncats+" ; "+divLimits);
	var catDataIndexes = [];
	//var counter = ncats-1;
	for(var id in catData) {
		catDataIndexes.push(id);
	}
	catDataIndexes.reverse();
	for(var idx in catDataIndexes) {
	//for(var i=0; i<ncats; i++) {
		var id = catDataIndexes[idx];
		var cat = document.createElement('div');
		cat.setAttribute('id', 'cat'+id);
		cat.setAttribute('class', 'category');
		cat.setAttribute('style', 'background-color: #'+catData[id].color+'; height: '+catheight+'%;');
		cat.setAttribute('onclick', 'showPlaceInfo("'+id+'", null, "'+catData[id].description+'")'); //catData[id].description
		//cat.setAttribute('onclick', 'showPlaceInfo(id, "'+catData[id].name+'", "'+catData[id].description+'")'); //catData[id].description
		//cat.setAttribute('onclick', function() { showPlaceInfo(id, "", ""); }); //catData[id].description
		//console.log(cat);
		container.appendChild(cat);
	}
	var mapcanvas = document.getElementById('map_canvas');
	mapcanvas.style.right = "" + (parseInt(container.style.width) + 0.3) + "em";
	container.style.display = 'block';
}

function resetMap() {
	document.getElementById('place_info').style.display='none';
	
	for(var id in gmapsPlaces) {
		var placemark = gmapsPlaces[id];
		placemark.fillColor = DEFAULT_FILL_COLOR;
		placemark.setMap(map); //atualiza placemark no mapa - 'null' retira elemento do mapa
		//TODO: removeListener
		//google.maps.event.removeListener(placemark.listener);		
	}
	
	//console.log('reset');
	var container = document.getElementById('categories_canvas');
	container.style.display = 'none';
	var mapcanvas = document.getElementById('map_canvas');
	mapcanvas.style.right = 0;
}
	
function loadDataCallback() {
	//console.log(seriesData.series);
	//TODOne: remove (or not) data elements not present in map
	var genCatLimitsFromExistingPlacemarks = document.getElementById('genCatLimitsFromExistingPlacemarks').checked;
	console.log('genCatLimitsFromExistingPlacemarks: '+genCatLimitsFromExistingPlacemarks)
	if(genCatLimitsFromExistingPlacemarks) {
		removeSeriesDataWhenPlacemarkNotFound(gmapsPlaces, seriesData);
	}
	
	var maxval = max(seriesData.series);
	var minval = min(seriesData.series);
	var numOfCategories = document.getElementById('numOfCategories').value;
	//console.log('vals: '+minval+'; '+ maxval+'; '+numOfCategories );
	//TODO: log/linear/percentile cat limits
	var catLimits;
	var scaleType = document.getElementById('scaleType').value;
	if(scaleType=='LINEAR') {
		catLimits = getLinearCategoriesLimits(minval, maxval, numOfCategories);
	}
	else {
		catLimits = getLogCategoriesLimits(minval, maxval, numOfCategories);
	}
	//console.log(catLimits);
	
	var cats = genCategoriesFromLimits(catLimits);
	catData = cats;

	//console.log('cats::');
	//console.log(cats);
	
	var colorFrom = document.getElementById('colorFrom').value;
	var colorTo = document.getElementById('colorTo').value;

	console.log('color: '+colorFrom+'; '+ colorTo);
	procStylesFromCategories(cats, colorFrom, colorTo, seriesData.valueLabel);
	
	console.log("hex:: "+hexString(2)+"; "+hexString(9)+"; "+hexString(10)+"; "+hexString(11)+"; "+hexString(250)+"; "+hexString(255));
	
	applySeriesDataAndStyle(gmapsPlaces, seriesData, cats, map);
	//applySeriesData(gmapsPlaces, seriesData, cats);
	//applyStyleColor(gmapsPlaces, cats);
	createCatElements();
	
	//TODO: option to select (show only) placemarks from given category
	//TODO: remove placemark not found? nah... add all not found to 'unknown' category
}
</script>
</head>
<body onload="changeColor('colorFromRGB', 'colorFrom');changeColor('colorToRGB', 'colorTo');loadNakedMap('map_canvas');">

<h2>MapProc<sup style="color: #fa0; font-size: 10pt;">beta!</sup></h2>

<div id="form">

<!-- TODO: change action to "proc/" -->
<form id="theform" action="proc/">
Data:
<select name="dataUrl" id="dataUrl">
<option value="/input/json/series-ha.json">Habitantes</option>
<option value="/input/json/series-ha_por_area.json">Habitantes por km^2</option>
<option value="/input/json/series-pib.json">PIB</option>
<option value="/input/json/series-pib_por_ha.json">PIB por habitante</option>
<option value="/input/json/series-pib_por_area.json">PIB por km^2</option>
<option value="/input/json/series-area.json">Area</option>
</select>
<br/>

<div id="categoriesGenComp" class="boxsmall">
#Cat: 
<select name="numOfCategories" id="numOfCategories">
<%
for(int i=2;i<=10;i++) {
	out.print("<option value=\""+i+"\""+(i==5?" selected":"")+">"+i+"</option>");
}
%>
</select><br/>
Scale: 
<select name="scaleType" id="scaleType">
	<option value="LINEAR">LINEAR</option>
	<option value="LOG" selected>LOG</option>
	<!-- option value="PERCENTILE">PERCENTILE</option -->
</select><br/>
Normalize Limits? <input type="checkbox" class="smaller" name="genCatLimitsFromExistingPlacemarks" id="genCatLimitsFromExistingPlacemarks" value="1" checked/><br/>
</div>
Color Max: <input type="text" class="color small" id="colorToRGB" name="colorToRGB" value="FF0000" onchange="changeColor('colorToRGB', 'colorTo')"/> <input type="hidden" id="colorTo" name="colorTo"/><br/>
Color Min: <input type="text" class="color small" id="colorFromRGB" name="colorFromRGB" value="FFFF00" onchange="changeColor('colorFromRGB', 'colorFrom')"/> <input type="hidden" id="colorFrom" name="colorFrom"/><br/>
<!-- 
Remove not found? <input type="checkbox" class="smaller" name="removeIfNotFound" value="1"/><br/ -->
<br/>
<input type="button" value="Load Data" class="medium" onClick="loadData();"/><br/>
<input type="button" value="Reset" class="medium" onClick="resetMap();"/><br/>
<br/>
<!-- input type="button" value="Open in GMaps" class="medium" onClick="openInGoogleMaps('theform');"/><br/ -->
<input type="submit" value="Download" class="small"/><br/>
</form>

</div>

<div id="map_canvas" class="canvas" style="position: absolute; top: 0; bottom: 0; left: 16em; right: 0; border: 1px solid black;"></div>

<div id="categories_canvas" class="canvas" style="position: absolute; top: 0; bottom: 0; width: 4em; right: 0; border: 1px solid black; z-index: 2; display: none;"></div>

<div id="messages" style="position: absolute; bottom: 8px; left: 16.3em; border: 1px solid black; display: none;"></div>

<div id="map_location" style="width: 800px; height: 60px; border: 1px solid black; background-color: #ddd; display: none;"></div>

<div id="place_info" style="position: absolute; height: 11em; bottom: 4px; width: 15em; left: 4px; border: 1px solid black; display: none;">
<div id="place_info_close" style="float: right; top: 0px; right: 0px;"><a href="#" onclick="document.getElementById('place_info').style.display='none';">[x]</a></div>
<span class="label debug" id="placeIdLabel">id: </span><span id="placeId" class="placeInfo debug"></span><br/>
<span class="label" id="placeNameLabel">name: </span><div id="placeName" class="placeInfo"></div>
<span class="label">desc: <br/></span><div id="placeDesc" class="placeInfo"></div>
</div>

</body>
</html>
