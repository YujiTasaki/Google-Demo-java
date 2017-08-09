<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.lang.*"%>
<%@ page import="jp.co.kke.Lockstatedemo.*"%>
<%@ page import="jp.co.kke.Lockstatedemo.mng.*"%>
<%@ page import="jp.co.kke.Lockstatedemo.util.*"%>
<%
	String pageTitle = "システムエラー";
	response.setHeader("Pragma","no-cache");
	response.setHeader("Cache-Control","no-cache");
	String url = ServletUtil.getUrl(request);
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Tue, 10 Dec 1968 15:00:00 GMT">
<link rel="stylesheet" type="text/css" href="<%=url%>/css/conf.css" />
<link rel="stylesheet" href="<%=url%>/css/jquery.mobile-1.4.5.min.css" />
<script type="text/javascript" src="<%=url%>/js/jquery-1.11.3.min.js"></script>
<script type="text/javascript"
	src="<%=url%>/js/jquery.mobile-1.4.5.min.js"></script>

<title><%=pageTitle%></title>
</head>
<body>
	<!-- WRAPPER -->
	<div id="wrapper">
		<!-- HEADER -->
		<div id="header">
			<h2>エラー詳細</h2>
		</div>
		<!-- /HEADER -->
		<div id="main">
			<blockquote>
				<%=request.getAttribute(ServletUtil.S_REQ_ATT_KEY_ERROR)%>
			</blockquote>
		</div>
		<!-- FOOTER  -->
		<div id="footer">
			<address>KOZO KEIKAKU ENGINEERING Inc.</address>
		</div>
		<!-- /FOOTER  -->
	</div>
	<!-- /WRAPPER -->
</body>
</html>