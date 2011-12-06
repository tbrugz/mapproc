<%@page import="tbrugz.mapproc.gae.DailyURLAccessCount"%>
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
int maxLines = 10;
RequestCountSB rc = new RequestCountSB(); 
List lo = rc.getMostViewed(UrlType.MAP);
%>
<table>
<tr>
<th>URL</th><th>Count</th></tr>
<%
int totalLines = lo.size();
if(maxLines>totalLines) { maxLines = totalLines; }
for(int i=0; i < maxLines; i++) {
	URLAccessCount uac = (URLAccessCount) lo.get(i);
%>	
<tr>
<td>
	<a href="<%= uac.getUrl() %>"><%= StringUtils.stringSnippet( uac.getUrl(), 50) %></a><span class="smalltext">
	<%= (uac.getNumOfElements()>0?"[elements: "+uac.getNumOfElements()+"] ":"")
	+(uac.getDescription()!=null?"[desc: "+uac.getDescription()+"] ":"")
	+(uac.getHttpStatus()!=200?"[error-code: "+uac.getHttpStatus()+"] ":"")
	+"[last access: "+StringUtils.getDateString( uac.getLastAccess() )+"]"
	%></span>
</td>
<td class="number"><%= uac.getCounter() %></td>
</tr>
<%
}
rc.closeEM();
%>
</table>
<!-- a href="most-wanted-bytype.jsp?type=MAP">see all</a -->
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
maxLines = 10;
totalLines = lo.size();
if(maxLines>totalLines) { maxLines = totalLines; }
for(int i=0; i < maxLines; i++) {
	URLAccessCount uac = (URLAccessCount) lo.get(i);
%>	
<tr>
<td>
	<a href="<%= uac.getUrl() %>"><%= StringUtils.stringSnippet( uac.getUrl(), 50) %></a><span class="smalltext">
	<%= (uac.getNumOfElements()>0?"[elements: "+uac.getNumOfElements()+"] ":"")
	+(uac.getDescription()!=null?"[desc: "+uac.getDescription()+"] ":"")
	+(uac.getHttpStatus()!=200?"[error-code: "+uac.getHttpStatus()+"] ":"")
	+"[last access: "+StringUtils.getDateString( uac.getLastAccess() )+"]"
	%></span>
</td>
<td class="number"><%= uac.getCounter() %></td>
</tr>
<%
}
rc.closeEM();
%>
</table>
<!-- a href="most-wanted-bytype.jsp?type=SERIES">see all</a-->
<br/>

<h3>MAP+SERIES:</h3>
<%
//RequestCountSB rc = new RequestCountSB(); 
lo = rc.getMostViewed(UrlType.MAP_SERIES);
%>
<table>
<tr>
<th>URL</th><th>Count</th></tr>
<%
maxLines = 10;
totalLines = lo.size();
if(maxLines>totalLines) { maxLines = totalLines; }
for(int i=0; i < maxLines; i++) {
	URLAccessCount uac = (URLAccessCount) lo.get(i);
%>	
<tr>
<td>
	<a href="<%= uac.getUrl() %>"><%= StringUtils.stringSnippet( uac.getUrl(), 50) %></a><span class="smalltext">
	<%= (uac.getNumOfElements()>0?"[elements: "+uac.getNumOfElements()+"] ":"")
	+(uac.getDescription()!=null?"[desc: "+uac.getDescription()+"] ":"")
	+(uac.getHttpStatus()!=200?"[error-code: "+uac.getHttpStatus()+"] ":"")
	+"[last access: "+StringUtils.getDateString( uac.getLastAccess() )+"]"
	%></span>
</td>
<td class="number"><%= uac.getCounter() %></td>
</tr>
<%
}
rc.closeEM();
%>
</table>
<!-- a href="most-wanted-bytype.jsp?type=MAP_SERIES">see all</a-->
<br/>

<h3>MAP, last 7 days:</h3>
<%
//RequestCountSB rc = new RequestCountSB(); 
maxLines = 10;
lo = rc.getMostViewedLastXDays(UrlType.MAP, 7);
lo = rc.groupByURL(lo);
/*for(int i=0; i < lo.size(); i++) {
	DailyURLAccessCount uac = (DailyURLAccessCount) lo.get(i);
	out.println(uac);
}*/
%>
<table>
<tr>
<th>URL</th><th>Count</th></tr>
<%
totalLines = lo.size();
if(maxLines>totalLines) { maxLines = totalLines; }
for(int i=0; i < maxLines; i++) {
	URLAccessCount uac = (URLAccessCount) lo.get(i);
%>	
<tr>
<td>
	<a href="<%= uac.getUrl() %>"><%= StringUtils.stringSnippet( uac.getUrl(), 50) %></a><span class="smalltext">
	<%= (uac.getNumOfElements()>0?"[elements: "+uac.getNumOfElements()+"] ":"")
	+(uac.getDescription()!=null?"[desc: "+uac.getDescription()+"] ":"")
	+(uac.getHttpStatus()!=200?"[error-code: "+uac.getHttpStatus()+"] ":"")
	+"[last access: "+StringUtils.getDateString( uac.getLastAccess() )+"]"
	%></span>
</td>
<td class="number"><%= uac.getCounter() %></td>
</tr>
<%
}
rc.closeEM();
%>
</table>

</body>
</html>