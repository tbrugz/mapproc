<%@ page import="java.util.*, tbrugz.mapproc.*" %>
<html>
<head>
<title>MapProc: map processor</title>
<link rel="stylesheet" type="text/css" href="css/mapproc.css" />
<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?sensor=false"></script>
<script type="text/javascript" src="js/mapproc.js"></script>
<script type="text/javascript" src="js/jscolor/jscolor.js"></script>
<%@ include file="fragments/analytics.html" %>
<script type="text/javascript">
var map;
var geoXml;

function loadMap() {
	var kmlLayer = loadKmlInMap('theform',map,'map_location');
	google.maps.event.addListener(kmlLayer, 'click', function (kmlEvent) {
        var id = kmlEvent.featureData.id;
        var name = kmlEvent.featureData.name;
        var desc = kmlEvent.featureData.description;
		document.getElementById('placeId').innerHTML = id;
		document.getElementById('placeName').innerHTML = name;
		document.getElementById('placeDesc').innerHTML = desc;
		//alert('id: '+id+'; name: '+name+'; desc: '+desc);
		document.getElementById('place_info').style.display = 'block';
    });
}
</script>
</head>
<body onload="changeColor('colorFromRGB', 'colorFrom');changeColor('colorToRGB', 'colorTo');map = initMap('map_canvas');">

<h2>MapProc<sup style="color: #fa0; font-size: 10pt;">beta!</sup></h2>

<div id="form">

<form id="theform" action="proc/">

<div id="urls" style="position: absolute; top: 4px; left: 16em;">
Map URL: 
<input type="text" name="kml"/><br/>
Data URL:
<input type="text" name="csv"/><br/>
Categories URL:
<input type="text" name="cat"/><br/>
</div>

<br/>

<div id="leftform" style="position: absolute; top: 6em;">

<div id="categoriesGenComp" class="boxsmall">
#Cat: 
<select name="numOfCategories">
<%
for(int i=2;i<=10;i++) {
	out.print("<option value=\""+i+"\""+(i==5?" selected":"")+">"+i+"</option>");
}
%>
</select><br/>
Scale: 
<select name="scaleType">
	<option value="LINEAR">LINEAR</option>
	<option value="LOG" selected>LOG</option>
	<option value="PERCENTILE">PERCENTILE</option>
</select><br/>
Normalize Limits? <input type="checkbox" class="smaller" name="genCatLimitsFromExistingPlacemarks" value="1" checked/><br/>
</div>
Color Min: <input type="text" class="color small" id="colorFromRGB" name="colorFromRGB" value="FFFF00" onchange="changeColor('colorFromRGB', 'colorFrom')"/> <input type="hidden" id="colorFrom" name="colorFrom"/><br/>
Color Max: <input type="text" class="color small" id="colorToRGB" name="colorToRGB" value="FF0000" onchange="changeColor('colorToRGB', 'colorTo')"/> <input type="hidden" id="colorTo" name="colorTo"/><br/>

Remove not found? <input type="checkbox" class="smaller" name="removeIfNotFound" value="1"/><br/>
<br/>
<input type="button" value="Load Map" class="medium" onClick="loadMap();"/><br/>
<br/>
<input type="button" value="Open in GMaps" class="medium" onClick="openInGoogleMaps('theform');"/><br/>
<input type="submit" value="Download" class="small"/><br/>

</div>

</form>

</div>

<div id="map_canvas" style="position: absolute; top: 6em; bottom: 4px; left: 16em; right: 4px; border: 1px solid black;"></div>

<div id="map_location" style="width: 800px; height: 60px; border: 1px solid black; background-color: #ddd; display:none;"></div>

<div id="place_info" style="position: absolute; height: 11em; bottom: 4px; width: 15em; left: 4px; border: 1px solid black; display: none;">
<div id="place_info_close" style="float: right; top: 0px; right: 0px;"><a href="#" onclick="document.getElementById('place_info').style.display='none';">[x]</a></div>
<span class="label">id: </span><span id="placeId" class="placeInfo"></span><br/>
<span class="label">name: </span><div id="placeName" class="placeInfo"></div>
<span class="label">desc: <br/></span><div id="placeDesc" class="placeInfo"></div>
</div>

</body>
</html>
