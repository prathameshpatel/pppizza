<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/sidebar.jsp" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section>
	<h3>Current Pizza Sizes</h3>
	<c:forEach items="${allSizes}" var="curSize">
				${curSize}
	</c:forEach>
	<br><br>
	<c:url var="sizesURL" value="sizes"/>
	<form action="${sizesURL}" method="post">
		Add a new pizza size: <input type="text" name="item"> <input
			type="submit" name="command" value="add">
	</form>
	<br>
	<form action="${sizesURL}" method="post">
		<select name="item">
			<c:forEach items="${allSizes}" var="curSize">
				<option value="${curSize}">${curSize}</option>
			</c:forEach>
		</select> <input type="submit" name="command" value="remove">
	</form>
	<br> <a href="<c:url value = 'adminWelcome.html'/>"> Back to Admin page</a>
</section>
<jsp:include page="/includes/footer.jsp" />
