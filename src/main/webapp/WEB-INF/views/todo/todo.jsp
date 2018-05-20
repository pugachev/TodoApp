<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page import="java.util.Calendar"%>
<!DOCTYPE html>
<html>
	<head>
	    <meta charset="utf-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title>TodoAppli</title>
		<link href="<c:url value="/resources/css/bootstrap-grid.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/css/bootstrap-grid.min.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/js/bootstrap.js.min.css" />" rel="text/javascript">
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
		<script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
		<script src="https://code.jquery.com/ui/1.11.1/jquery-ui.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
	    <script src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.0/js/bootstrap.bundle.min.js"></script>
		<link rel="stylesheet" href="https://code.jquery.com/ui/1.11.1/themes/smoothness/jquery-ui.css" />
	    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.0/css/bootstrap.min.css">
	    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
	    <link href="<c:url value="/resources/css/list_styles.min.css" />" rel="stylesheet">
        <style type="text/css">
			#delete {
			    text-decoration: line-through;
			}
        </style>
		<script type="text/javascript">
		</script>
	</head>
	<body>
	    <nav class="navbar navbar-light navbar-expand-md navigation-clean-search">
	        <div class="container"><a class="navbar-brand" href="#">${username}さん ようこそ</a><button class="navbar-toggler" data-toggle="collapse" data-target="#navcol-1"><span class="sr-only">Toggle navigation</span><span class="navbar-toggler-icon"></span></button>
	            <div class="collapse navbar-collapse" id="navcol-1">
			        <form name="f" action="<c:url value='/logout'/>" method="post" >
			 				<input type="submit" class="btn btn-light action-button"  name="login" value="ログアウト">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
					</form>
	            </div>
	        </div>
	    </nav>
	    <div class="container">
		 <form:form modelAttribute="todoForm" action="${pageContext.request.contextPath}/newItem?username=${username}">
		   <div class="form-group">
		       <div class="row">
		       	   <form:errors path="content" cssStyle="color:red" />
		           <div class="col-sm-8"><input class="form-control input-sm " id="email" name="content" placeholder="Todo内容" size="100%" type="text"></div>
		           <div class="col-sm-2"><input type="submit" class="btn btn-success btn-block" name="newItem" value="登録" size="100%"></div>
		           <div class="col-sm-2"><input type="submit" class="btn btn-success btn-block" name="searchItem" value="検索" size="100%"></div>
		           <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		       </div>
		   </div>
		  </form:form>
	    </div>
		<div class="container">
			<table class="table table-striped table-bordered" id="data" style="width:100%;">
			  <thead>
			    <tr>
			      <th scope="col" colspan="2">Todo内容一覧</th>
			    </tr>
			  </thead>
			  <tbody>
				  <c:forEach var="mdata" items="${mList}" >
				  <tr>
				  	<td style="height:20px;">
				  	<div class="form-group" style="display: inline-block;width:100%;">
					  	<div class="row" style="height:20px;">
						  	<div class="col-sm-8" style="height:20px;">
							  	 <c:if test="${mdata.done}" >
							  		<span id="delete" style="display: inline-block;width:100%;">${mdata.content}</span>
							  	 </c:if>
							  	 <c:if test="${not mdata.done}" >
							  		<span  style="display: inline-block;width:100%;">${mdata.content}</span>
							  	 </c:if>
							 </div>
						  	<div class="col-sm-4">
							  	 <c:if test="${mdata.done}" >
				                    <form  method="post" action="${pageContext.request.contextPath}/restore?username=${username}">
				                        <input type="hidden" name="id" value="${mdata.id}" />
				                        <input type="submit" class="btn btn-primary" value="復活" style="width:100%;"/>
				                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
				                    </form>
							  	 </c:if>
							  	 <c:if test="${not mdata.done}" >
				                    <form  method="post" action="${pageContext.request.contextPath}/done?username=${username}">
				                        <input type="hidden" name="id" value="${mdata.id}" />
				                        <input type="submit" class="btn btn-danger" value="完了" style="width:100%;"/>
				                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
				                    </form>
							  	 </c:if>
							  </div>
					    </div>
					    </div>
                    </td>
				  </tr>
				  </c:forEach>
			  </tbody>
			</table>
		</div>
	</body>
</html>