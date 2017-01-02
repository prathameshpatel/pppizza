<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/sidebar.jsp" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<section>
	<h3>Today is day ${currentDay}</h3> 
	<c:url var="submitURL" value="days" />
	<form action="${submitURL}" method="post">
		<input type="hidden"  name="command" value="advance">
		<input type="submit" value="advance day to day ${currentDay + 1} "> <br>
	</form>
	<h2>Today's Orders</h2>
    <strong>Order number size room status </strong>
	<br>
	<c:forEach items="${orders}" var="order">
  	  ${order.id}  ${order.pizzaSize} ${order.roomNumber} ${order.status} <br>
	</c:forEach>
	<br>
	<br> <a href="<c:url value = 'adminWelcome.html'/>"> Back to Admin page</a>
</section>
<jsp:include page="/includes/footer.jsp" />