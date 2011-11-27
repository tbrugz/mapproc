<%@ page import="java.util.*, tbrugz.mapproc.*" %>

<%@page import="tbrugz.mapproc.gae.RequestCountSB"%>
<%@page import="tbrugz.mapproc.gae.URLAccessCount.UrlType"%>
<%@page import="tbrugz.mapproc.gae.URLAccessCount"%><html>
<head>
<title>MapProc: Most Wanted</title>
<link rel="stylesheet" type="text/css" href="css/mapproc.css" />
<%@ include file="fragments/analytics.html" %>
<style type="text/css">
table {
	width: 70%;
	background-color: #ccc;
}
td {
	background-color: #eee;
}
.number {
	width: 20%;
	text-align: right;
}
</style>
</head>
<body>
<h2>MapProc<sup style="color: #fa0; font-size: 10pt;">beta!</sup>: Most Wanted</h2>

<h3>MAP:</h3>
<%
RequestCountSB rc = new RequestCountSB(); 
List lo = rc.getMostViewed(UrlType.MAP);
%>
<table>
<tr>
<th>URL</th><th>Count</th></tr>
<%
for(Object o: lo) {
	URLAccessCount uac = (URLAccessCount) o;
%>	
<tr><td><a href="<%= uac.getUrl() %>"><%= uac.getUrl() %></a></td><td class="number"><%= uac.getCounter() %></td></tr>
<%
}
rc.closeEM();
%>
</table>

<br/>

<h3>SERIES:</h3>
<%
//RequestCountSB rc = new RequestCountSB(); 
lo = rc.getMostViewed(UrlType.SERIES);
%>
<table>
<tr>
<th>URL</th><th>Count</th></tr>
<%
for(Object o: lo) {
	URLAccessCount uac = (URLAccessCount) o;
%>	
<tr><td><a href="<%= uac.getUrl() %>"><%= uac.getUrl() %></a></td><td class="number"><%= uac.getCounter() %></td></tr>
<%
}
rc.closeEM();
%>
</table>

</body>
</html>