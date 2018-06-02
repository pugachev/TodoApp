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
	    <meta name="viewport" content="width=device-width, initial-scale=1.0,minimum-scale=1.0">
		<title>TodoAppli</title>
		<link href="<c:url value="/resources/css/bootstrap-grid.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/css/bootstrap-grid.min.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/js/bootstrap.js.min.css" />" rel="text/javascript">
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
		<!-- <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>-->
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
		function changeDataBtn(flg)
		{
			var btn='';
			var val = $('#TodoModal [name=opbtn]').val();
			if(val==='復活'){
				btn=document.getElementById('restorreDataBtn');
			}else{
				btn=document.getElementById('completeDataBtn');
			}
			btn.click();
		}
		function del()
		{
			var btn='';
			btn=document.getElementById('deleteDataBtn');
			btn.click();
		}
		function openInsertModal()
		{
			$("#InsertModal").modal("show");
		}

		function openSearchModal()
		{
			$("#SearchModal").modal("show");
		}
		$(document).ready(function(){
			var contents='';
			var bntFlg='';
			var tmp3='';

			$("#dataInsert").click(function(){
				$("#InsertModal").modal("show");
			});
			$("#InsertModal").on('show.bs.modal',function(){

			});

			$(".content").click(function(){
				contents = $(this).text();
				bntFlg=$(this).attr("value");
				$("#TodoModal").modal("show");
			});
			$("#TodoModal").on('show.bs.modal',function(){
				var modal = $(this);
				if(bntFlg=="1"){
					modal.find('#target').html( "<span style='text-decoration: line-through;'>"+contents+"</span>");
					var val = $('#TodoModal [name=opbtn]').val();
					$('#TodoModal [name=opbtn]').val('復活');

				}else{
					modal.find('#target').html( "<span>"+contents+"</span>");
					var val = $('#TodoModal [name=opbtn]').val();
					$('#TodoModal [name=opbtn]').val('完了');
				}
			});
		});
		</script>
	</head>
	<body>
	    <nav class="navbar navbar-light navbar-expand-md navigation-clean-search">
	        <div class="container"><a class="navbar-brand" href="#">${username}さん ようこそ</a><div style="color:red">${resutErrors}</div><button class="navbar-toggler" data-toggle="collapse" data-target="#navcol-1"><span class="sr-only">Toggle navigation</span><span class="navbar-toggler-icon"></span></button>
	            <div class="collapse navbar-collapse" id="navcol-1">
			        <form name="f" action="<c:url value='/logout'/>" method="post" >
			 				<input type="submit" class="btn btn-light action-button"  name="login" value="ログアウト">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
					</form>
	            </div>
	        </div>
	    </nav>
		<div class="container d-flex align-self-center align-items-center w-100 p-0">
			<table class="table table-striped table-bordered " id="data">
			  <thead>
			    <tr>
			      <th scope="col" colspan="2">Todo内容一覧 <a class="btn btn-xs bg-primary text-white bg-dark" href="#" onclick="openInsertModal(); return false;" role="button" style="float:right;margin:5px;">登録</a><a class="btn btn-xs bg-primary text-white bg-dark" href="#" onclick="openSearchModal(); return false;"  role="button" style="float:right;margin:5px;">検索</a></th>
			    </tr>
			  </thead>
			  <tbody>
				  <c:forEach var="mdata" items="${mList}" >
				  <tr >
				  	<td >
					  	<div class="container">
						  	<div class="form-group">
							  	<div class="row">
								  	<div class="col-md-8">
									  	 <c:if test="${mdata.done}" >
									  		<span class="content" style="width:100%;display: inline-block;text-decoration: line-through;" value="1">${mdata.content}</span>
									  	 </c:if>
									  	 <c:if test="${not mdata.done}" >
									  		<span  class="content" style="width:100%;display: inline-block;" value="2">${mdata.content}</span>
									  		<input type="hidden" id="checkflg2" value="2" />
									  	 </c:if>
									 </div>
								  	<div class="col-md-2">
									  	 <c:if test="${mdata.done}" >
						                    <form  method="post" action="${pageContext.request.contextPath}/restore">
						                        <input type="hidden" name="id" value="${mdata.id}" />
						                        <input type="hidden" name="username" value="${username}" />
						                        <input type="submit" id="restorreDataBtn" value="復活" style="width:100%;visibility:hidden;"/>
						                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						                    </form>
									  	 </c:if>
									  	 <c:if test="${not mdata.done}" >
						                    <form  method="post" action="${pageContext.request.contextPath}/done">
						                        <input type="hidden" name="id" value="${mdata.id}" />
						                        <input type="hidden" name="username" value="${username}" />
						                        <input type="submit" id="completeDataBtn" value="完了" style="width:100%;visibility:hidden;"/>
						                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						                    </form>
									  	 </c:if>
									  	 </div>
									  	<div class="col-md-2">
						                    <form  method="post" action="${pageContext.request.contextPath}/deletedata">
						                        <input type="hidden" name="id" value="${mdata.id}" />
						                        <input type="hidden" name="username" value="${username}" />
						                        <input type="submit" id="deleteDataBtn" value="削除" style="width:100%;visibility:hidden;"/>
						                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						                    </form>
						                    </div>
									  </div>
							    </div>
						    </div>
                    </td>
				  </tr>
				  </c:forEach>
			  </tbody>
			</table>
		</div>
		 <form:form modelAttribute="todoForm" action="${pageContext.request.contextPath}/newItem" style="visibility:hidden;">
		           <input type="submit" class="btn btn-success btn-block" name="newItem" value="登録" style="visibility:hidden;">
		           <input type="submit" class="btn btn-success btn-block" name="searchItem" value="検索" style="visibility:hidden;">
		           <input type="hidden" name="username" value="${username}" />
		           <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		  </form:form>
	</body>
</html>
<!-- The InsertModal -->
<div class="modal" id="InsertModal">
  <div class="modal-dialog">
    <div class="modal-content">

      <!-- Modal Header -->
      <div class="modal-header">
        <h4 class="modal-title">Todo内容</h4>
        <button type="button" class="close" data-dismiss="modal">&times;</button>
      </div>

      <!-- Modal body -->
      <div class="modal-body">
		 <form:form modelAttribute="todoForm" action="${pageContext.request.contextPath}/newItem">
		   <div class="form-group">
		       <div class="row">
		           <div class="col-md-8"><input class="form-control input-sm " id="email" name="content" placeholder="Todo内容" size="100%" type="text"></div>
		           <div class="col-md-4"><input type="submit" class="btn btn-success btn-block" name="newItem" value="登録" size="100%" ></div>
		           <input type="hidden" name="username" value="${username}" />
		           <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		       </div>
		   </div>
		  </form:form>
      </div>

      <!-- Modal footer -->
      <div class="modal-footer">
      </div>
    </div>
  </div>
</div>
<!-- The SearchModal -->
<div class="modal" id="SearchModal">
  <div class="modal-dialog">
    <div class="modal-content">

      <!-- Modal Header -->
      <div class="modal-header">
        <h4 class="modal-title">Todo内容</h4>
        <button type="button" class="close" data-dismiss="modal">&times;</button>
      </div>

      <!-- Modal body -->
      <div class="modal-body">
		 <form:form modelAttribute="todoForm" action="${pageContext.request.contextPath}/newItem">
		   <div class="form-group">
		       <div class="row">
		           <div class="col-md-8"><input class="form-control input-sm " id="email" name="content" placeholder="Todo内容" size="100%" type="text"></div>
		           <div class="col-md-4"><input type="submit" class="btn btn-success btn-block" name="searchItem" value="検索" size="100%"></div>
		           <input type="hidden" name="username" value="${username}" />
		           <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		       </div>
		   </div>
		  </form:form>
      </div>

      <!-- Modal footer -->
      <div class="modal-footer">
      </div>
    </div>
  </div>
</div>
<!-- The TodoModal -->
<div class="modal" id="TodoModal">
  <div class="modal-dialog">
    <div class="modal-content">

      <!-- Modal Header -->
      <div class="modal-header">
        <h4 class="modal-title">Todo内容一覧</h4>
        <button type="button" class="close" data-dismiss="modal">&times;</button>
      </div>

      <!-- Modal body -->
      <div class="modal-body">
        <p id="target"/></p>
      </div>

      <!-- Modal footer -->
      <div class="modal-footer">
		<input type="button" class="btn btn-primary" id="opbtn" name="opbtn" onclick= 'changeDataBtn()' data-dismiss="modal" value="完了"/>
      	<input type="button" class="btn btn-danger" id="delbtn" name="delbtn" onclick='del()' data-dismiss="modal" value="削除"/>
      </div>
    </div>
  </div>
</div>