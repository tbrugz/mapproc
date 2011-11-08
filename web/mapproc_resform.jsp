<%@ page import="java.util.*, tbrugz.mapproc.*" %>
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/mapproc.css" />
</head>
<body>

<h2>MapProc.res</h2>

<div id="form">

<form action="proc/">
KML Resource: 
<select name="kmlResource">
<%
//TODO: sort
Properties p = new Properties();
p.load(MapProc.class.getResourceAsStream("/paths.properties"));
for(Object o: p.keySet()) {
	String s = (String) o;
	out.print("<option value=\""+p.getProperty(s)+"\">"+s+"</option>");
}
%>
</select>
<br/>
Data (CSV) Resource:
<select name="csvResource">
<option value="/input/csv/ha.csv">Habitantes</option>
<option value="/input/csv/ha_por_area.csv">Habitantes por km^2</option>
<option value="/input/csv/pib.csv">PIB</option>
<option value="/input/csv/pib_por_ha.csv">PIB por habitante</option>
<option value="/input/csv/pib_por_area.csv">PIB por km^2</option>
<option value="/input/csv/area.csv">Area</option>
</select>
<br/>
<em>Categories Resource</em> or <em>(<em>Number of categories</em> and <em>Scale Type</em>)</em> must be set<br/>
Categories (CSV) Resource: <input type="text" name="categoriesResource"/><br/>
Number of categories: <input type="text" name="numOfCategories"/><br/>
Scale Type: 
<select name="scaleType">
	<option value="LINEAR">LINEAR</option>
	<option value="LOG">LOG</option>
	<option value="PERCENTILE">PERCENTILE</option>
</select><br/>
Color From: <input type="text" name="colorFrom"/><br/>
Color To: <input type="text" name="colorTo"/><br/>
<br/>
<input type="submit"/>
</form>

</div>

</body>
</html>
