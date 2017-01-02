<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/sidebar.jsp" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section>
<h3>Current Toppings</h3>
	<c:forEach items="${allToppings}" var="curTopping">
				${curTopping} <br>
	</c:forEach>
	<br>
	<c:url var="toppingsURL" value="toppings"/>
	<form action="${toppingsURL}" method="post">
		Add a new topping: <input type="text" name="item"> 
		<input type="submit" name="command" value="add"> <br>
	</form>
	<br>
	<form action="${toppingsURL}" method="post">
		Delete an old topping:
		 <select name="item">
			<c:forEach items="${allToppings}" var="curTopping">
				<option value="${curTopping}">${curTopping}
				</option>
			</c:forEach>
		</select> 
		<input type="submit" name="command" value="remove">
	</form>
	<br> <a href="<c:url value = 'adminWelcome.html'/>"> Back to Admin page</a>
</section>
<jsp:include page="/includes/footer.jsp" />

