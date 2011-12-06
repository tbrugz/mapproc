<%@page import="tbrugz.mapproc.gae.DailyURLAccessCount"%>
<%@ page import="java.util.*, tbrugz.mapproc.*" %>

<%@page import="tbrugz.mapproc.gae.RequestCountSB"%>
<%@page import="tbrugz.mapproc.gae.URLAccessCount.UrlType"%>
<%@page import="tbrugz.mapproc.gae.URLAccessCount"%><html>
<head>
<title>MapProc: Most Wanted</title>
<link rel="stylesheet" type="text/css" href="css/mapproc.css" />
<%@ include file="fragments/analytics.html" %>
</head>
<body>
<h2>MapProc<sup style="color: #fa0; font-size: 10pt;">beta!</sup>: Most Wanted</h2>

<h3>MAP:</h3>
<%
int maxLines = 0;
int totalLines = 0;
RequestCountSB rc = new RequestCountSB(); 
List lo = rc.getMostViewed(UrlType.MAP);
%>
<%@ include file="fragments/most-wanted-table.inc" %>
<br/>

<h3>SERIES:</h3>
<%
//RequestCountSB rc = new RequestCountSB(); 
lo = rc.getMostViewed(UrlType.SERIES);
%>
<%@ include file="fragments/most-wanted-table.inc" %>
<br/>

<h3>MAP+SERIES:</h3>
<%
//RequestCountSB rc = new RequestCountSB(); 
lo = rc.getMostViewed(UrlType.MAP_SERIES);
%>
<%@ include file="fragments/most-wanted-table.inc" %>
<br/><br/>

<hr align="left"/>
<br/>

<% int lastXdays =1; %>

<h3>MAP, last <%= lastXdays %> days:</h3>
<%
//RequestCountSB rc = new RequestCountSB(); 
maxLines = 10;
lo = rc.getMostViewedLastXDays(UrlType.MAP, lastXdays);
lo = rc.groupByURL(lo);
/*for(int i=0; i < lo.size(); i++) {
	DailyURLAccessCount uac = (DailyURLAccessCount) lo.get(i);
	out.println(uac);
}*/
%>
<%@ include file="fragments/most-wanted-table.inc" %>
<br/>

<h3>SERIES, last <%= lastXdays %> days:</h3>
<%
maxLines = 10;
lo = rc.getMostViewedLastXDays(UrlType.SERIES, lastXdays);
lo = rc.groupByURL(lo);
%>
<%@ include file="fragments/most-wanted-table.inc" %>
<br/>

<h3>MAP+SERIES, last <%= lastXdays %> days:</h3>
<%
maxLines = 10;
lo = rc.getMostViewedLastXDays(UrlType.MAP_SERIES, lastXdays);
lo = rc.groupByURL(lo);
%>
<%@ include file="fragments/most-wanted-table.inc" %>

</body>
</html>