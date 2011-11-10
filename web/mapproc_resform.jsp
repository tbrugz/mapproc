<%@ page import="java.util.*, tbrugz.mapproc.*" %>
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/mapproc.css" />
<script type="text/javascript" src="js/mapproc.js"></script>
<script type="text/javascript" src="js/jscolor/jscolor.js"></script>
</head>
<body onload="changeColor('colorFromRGB', 'colorFrom');changeColor('colorToRGB', 'colorTo');">

<h2>MapProc.res</h2>

<div id="form">

<form action="proc/">
KML Resource: 
<select name="kmlResource">
<%
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
Data (CSV) Resource:
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
<em>Categories (CSV) Resource</em> or <em>(<em>Number of categories</em> and <em>Scale Type</em>)</em> must be set<br/>

<div class="box">
Categories (CSV) Resource: <input type="text" name="categoriesResource"/><br/>
</div>

<div id="categoriesGen" class="box">
Number of categories: 
<select name="numOfCategories">
<%
for(int i=2;i<=10;i++) {
	out.print("<option value=\""+i+"\""+(i==5?" selected":"")+">"+i+"</option>");
}
%>
</select><br/>
Scale Type: 
<select name="scaleType">
	<option value="LINEAR">LINEAR</option>
	<option value="LOG" selected>LOG</option>
	<option value="PERCENTILE">PERCENTILE</option>
</select><br/>
Color From: <input type="text" class="color small" id="colorFromRGB" name="colorFromRGB" value="FF0000" onchange="changeColor('colorFromRGB', 'colorFrom')"/> <input type="hidden" id="colorFrom" name="colorFrom"/><br/>
Color To: <input type="text" class="color small" id="colorToRGB" name="colorToRGB" value="00FF00" onchange="changeColor('colorToRGB', 'colorTo')"/> <input type="hidden" id="colorTo" name="colorTo"/><br/>
<!-- TODO: checkbox: only consider series values that exists in map -->
</div>

Remove Placemark if no value found? <input type="checkbox" class="smaller" name="removeIfNotFound" value="1"/><br/>
<br/>
<input type="submit" class="small"/>
</form>

</div>

</body>
</html>
