<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/sidebar.jsp" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section>
	<h1> In-Progress Order Report</h1>
	<h3>Orders Baked but not delivered</h3>
	<c:if test="${empty orders_baked}">
    	No orders baked but not delivered
    </c:if>
	<c:forEach items="${orders_baked}" var="order">
				order ${order.id} for room ${order.roomNumber} <br>
	</c:forEach>
	<br>
		<h3>Orders Preparing (in the oven): any ready now?</h3>
	<c:if test="${empty orders_preparing}">
    	No orders in ovens
    	</c:if>
	<c:forEach items="${orders_preparing}" var="order">
				order ${order.id} for room ${order.roomNumber} <br>
	</c:forEach>
	<br>
	
	<c:if test="${!empty orders_preparing}">
		<c:url var="ordersURL" value="orders" />
		<form action="${ordersURL}" method="post">
			<input type="hidden"  name="command" value="mark">
			<input type="submit" value="mark next order ready">
			<br>
		</form>
	</c:if>
	<br>
<a href="<c:url value = 'adminWelcome.html'/>"> Back to Admin page</a>
</section>
<jsp:include page="/includes/footer.jsp" />

