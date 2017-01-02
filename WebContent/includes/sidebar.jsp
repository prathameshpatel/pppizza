<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<aside id="sidebar">
    <nav>
        <c:url var="welcomeURL" value="/welcome.html" />
        <c:url var="adminWelcomeURL" value="/adminController/adminWelcome.html" />
        <c:url var="studentWelcomeURL" value="/studentWelcome.html" />
        <c:url var="listVarsURL" value='/adminController/listVariables.html'/>
        <c:url var="logoutURL" value='/adminController/logout.html'/>

        <h4>Links</h4>

        <a href="${welcomeURL}">Home</a><br>
        <a href="${adminWelcomeURL}">Admin Service</a><br>
        <a href="${studentWelcomeURL}">Student Service</a> <br>
        <a href="${listVarsURL}"> List Variables</a><br>
        <a href="${logoutURL}"> Logout</a><br>
    </nav>
</aside>