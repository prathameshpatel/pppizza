<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%-- Note: The just-previous page directive is the first line of
   the JSP, as it should be according to the JSP spec., so the
   parser can determine the pageEncoding immediately for parsing
   the rest of the text. The contentType specifies the contentType
   and charset of the response for this page. 
--%>
<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/sidebar.jsp" />
<%-- Note: The following taglib is not needed for correct execution,
   since there is a relevant taglib directive in header.jsp,
   but this directive prevents spurious warnings in the IDE.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section>
	<h1>Welcome to the Pizza Shop!</h1>
	<c:if test="${empty allSizes}">
		<p style="color: red">Note: Admin needs to add pizza sizes (go to
			home page, then admin page)</p>
	</c:if>
	<c:if test="${!empty errorMessage}">
		<p style="color: red">
			<c:out value="${errorMessage}" />
		</p>
	</c:if>

	<h3>Pizza Sizes:</h3>
	<c:forEach items="${allSizes}" var="curSize">
	 ${curSize} <br>
	</c:forEach>
	<br>

	<h3>Today's Pizza Toppings:</h3>
	<c:forEach items="${allToppings}" var="curTopping">
	 ${curTopping} <br>
	</c:forEach>
	<br>

	<c:if test="${student.roomNo>0}">
		<h3>Pizza Orders for room ${student.roomNo}</h3>
		<c:url var="studentWelcomeURL" value="studentWelcome.html" />
		<form method="get" action="${studentWelcomeURL}">
			Change room: <select name="room">
				<c:forEach begin="1" end="${numRooms}" step="1" var="i">
					<option
						<c:if test="${i == student.roomNo}">
					selected = "selected"	
					</c:if>
						value="${i}">${i}</option>
				</c:forEach>
			</select> <input type="submit" value="Set room number">
		</form>
		<br>
		<c:if test="${empty statusReport}">
    	No orders for room ${student.roomNo}
    	</c:if>

		<!-- The following displays nothing if there are no orders -->
		<c:forEach items="${statusReport}" var="order">
       Size ${order.pizzaSize}, status ${order.status}, 
       Toppings:
      	 <c:forEach var="top" items="${order.toppings}">
          ${top}<br>
       	  </c:forEach>			
		</c:forEach>
	</c:if>

	<c:url var="orderReceiveURL" value="orderReceive.html" />
	<c:if test="${hasBaked}">
		<form method="get" action="${orderReceiveURL}">
			<input type="submit" value="Acknowledge delivery of baked pizza(s)">
		</form>
	</c:if>
	
	<c:url var="orderFormURL" value="orderForm.html" />
	<h3>
		<a href="${orderFormURL}"> Order a pizza now! </a>
	</h3>

</section>
<jsp:include page="/includes/footer.jsp" />

