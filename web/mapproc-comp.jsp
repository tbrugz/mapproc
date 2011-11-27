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
Map: 
<select name="kmlResource">
<%
//http://maps.google.com.br/maps?q=http:%2F%2Fdl.dropbox.com%2Fu%2F26206165%2Fmapproc.kml&hl=en&sll=-30.027704,-51.228735&sspn=0.827522,1.086273&vpsrc=0&t=h&z=6
Properties p = new Properties();
p.load(MapProc.class.getResourceAsStream("/paths.properties"));
List l = new ArrayList(p.keySet());
Collections.sort( l );
for(Object o: l) {
	String s = (String) o;
	out.print("<option value=\""+p.getProperty(s)+"\">"+s+"</option>");
}
%>
</select>
<br/>
Data:
<select name="csvResource">
<!-- option value="/input/csv/tabela-municipios_e_habitantes-parcial-100-RS.csv">Habitantes 100</option -->
<option value="/input/csv/ha.csv">Habitantes</option>
<option value="/input/csv/ha_por_area.csv">Habitantes por km^2</option>
<option value="/input/csv/pib.csv">PIB</option>
<option value="/input/csv/pib_por_ha.csv">PIB por habitante</option>
<option value="/input/csv/pib_por_area.csv">PIB por km^2</option>
<option value="/input/csv/area.csv">Area</option>
</select>
<br/>

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
</form>

</div>

<div id="map_canvas" style="position: absolute; top: 4px; bottom: 4px; left: 16em; right: 4px; border: 1px solid black;"></div>

<div id="map_location" style="width: 800px; height: 60px; border: 1px solid black; background-color: #ddd; display:none;"></div>

<div id="place_info" style="position: absolute; height: 11em; bottom: 4px; width: 15em; left: 4px; border: 1px solid black; display: none;">
<div id="place_info_close" style="float: right; top: 0px; right: 0px;"><a href="#" onclick="document.getElementById('place_info').style.display='none';">[x]</a></div>
<span class="label">id: </span><span id="placeId" class="placeInfo"></span><br/>
<span class="label">name: </span><div id="placeName" class="placeInfo"></div>
<span class="label">desc: <br/></span><div id="placeDesc" class="placeInfo"></div>
</div>

</body>
</html>
