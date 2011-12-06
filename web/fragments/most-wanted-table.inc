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