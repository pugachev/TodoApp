<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<html>
<head>
	<title>新規登録</title>
	<link href="<c:url value="/resources/css/regiform.css" />" rel="stylesheet">
</head>
<body>
<div class="login">
  <div class="login-triangle"></div>
  <h2 class="login-header">Register</h2>
  <form name="f" action="${pageContext.request.contextPath}/register" method="post">
    <p><input type="text" name="name" placeholder="Email"></p>
    <p><input type="password" name="password" placeholder="Password"></p>
    <p>	<input type="submit" name="register" value="新規登録"></p>
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  </form>
</div>
</body>
</html>
