<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.lang.*"%>
<%@ page import="jp.co.kke.Lockstatedemo.util.*"%>
<%--
	String codeurl = LockApiUtil.getAuthorizationCodeUrl();
    response.sendRedirect(codeurl);
--%>

<%
    String s_CID = request.getParameter("ClientID");

    System.out.println(s_CID);

    String codeurl = LockApiUtil.getAuthorizationCodeUrl(s_CID);
    response.sendRedirect(codeurl);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>
</head>
<body>
</body>
</html>