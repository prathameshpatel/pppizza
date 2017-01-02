<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Error</title>
</head>
<body>
	<h1>Error: ${error}</h1>
	<br> 
	<br> 
	<a href="<c:url value = 'adminWelcome.html'/>"> Back to Admin Menu</a>
	
</body>
</html>