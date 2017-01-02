<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/sidebar.jsp" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<section>
	<c:url var="initDBURL" value = "initializeDB.html"/>
	<form action="${initDBURL}" method="post">
		<input type="submit" value="Initialize DataBase">
	</form>
	<c:url var="toppingsURL" value = "toppings"/>
	<c:url var="sizesURL" value = "sizes"/>
	<c:url var="ordersURL" value = "orders"/>
	<c:url var="daysURL" value = "days"/>
	<ul>
		<li><a href="${toppingsURL}"> Manage Toppings</a></li>
		<li><a href="${sizesURL}"> Manage Pizza Sizes</a></li>
		<li><a href="${ordersURL}"> Manage Orders</a></li>
		<li><a href="${daysURL}"> Manage Days</a></li>

	</ul>
</section>
<jsp:include page="/includes/footer.jsp" />