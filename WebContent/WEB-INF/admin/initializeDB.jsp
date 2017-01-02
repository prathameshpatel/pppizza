<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/sidebar.jsp" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<section>
	Initialize DataBase: ${info}
	
	<br> <a href="<c:url value = 'adminWelcome.html'/>"> Back to Admin page</a>
	
</section>
<jsp:include page="/includes/footer.jsp" />
