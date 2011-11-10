<%@ page isErrorPage="true" import="java.io.*" %>
<html>
<head>
<link rel="stylesheet" type="text/css" href="/mapproc/css/mapproc.css" />
</head>
<body>
<h2>Exception occured</h2>
<div>Exception <code><%= exception %></code> has been encountered</div>
<h4>Stack trace:</h4>
<pre>
<% 
StackTraceElement[] stack = exception.getStackTrace();
for(int i=0;i<stack.length;i++) {
	out.print("\t"+stack[i]+"\n");
}
%>
</pre>
</body>
</html>