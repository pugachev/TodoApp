<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title>詳細画面</title>
	<link href="<c:url value="/resources/css/loginform.css" />" rel="stylesheet">
</head>
<body>
<h1>
	詳細画面
</h1>
<div class="container">
  <span>${id}</span>
</div>
       <form class="login-container" action="${pageContext.request.contextPath}/test?username=${username}" method="POST">
           <input type="hidden" name="id" value="${mdata.id}" />
           <input type="submit" class="btn btn-primary" value="一覧画面へ" />
           <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
       </form>


<a href="<c:url value="list" ><c:param name="isSearche" value="no" /></c:url>" >リンク</a>


</body>
</html>
