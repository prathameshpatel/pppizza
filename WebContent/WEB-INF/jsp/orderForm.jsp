<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/sidebar.jsp" />

<section>

	<h1>Order Your Pizza Here</h1>
	<c:url value="orderPizza.jsp" var="orderPizzaURL" />

	<c:if test="${student.roomNo > 0}">
		Your selected room number now is: ${student.roomNo}<br />
	</c:if>

	<c:if test="${empty allSizes}">
		<p style="color: red">Note: Admin needs to add pizza sizes (go to
			home page, then admin page)</p>
	</c:if>
	<c:if test="${!empty errorMessage}">
		<p style="color: red">
			<c:out value="${errorMessage}" />
		</p>
	</c:if>
	<br>
	<c:url var="orderPizzaURL" value="orderPizza.html" />

	<!--change to method="post" when development is done -->
	<form method="get" action="${orderPizzaURL}">

		Pizza Size: <br>
		<c:forEach items="${allSizes}" var="curSize">
			<input type="radio" name="size" value="${curSize}">  ${curSize} <br>
		</c:forEach>
		<br> Pizza Toppings: <br>
		<c:forEach items="${allToppings}" var="curTopping">
			<input type="checkbox" name="toppings" value="${curTopping}">  ${curTopping}  <br>
		</c:forEach>
		<br> Room for pizza delivery: 
		<select name="room">
			<c:forEach begin="1" end="${numRooms}" step="1" var="i">
				<option
					<c:if test="${i == student.roomNo}">
					selected = "selected"	
					</c:if>
					value="${i}">${i}</option>
			</c:forEach>
		</select> <br> <br> <input type="submit" value="Place Your Order">
		<input type="reset" value="Reset">
	</form>
</section>
<jsp:include page="/includes/footer.jsp" />
