<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User On-Premise Import</title>
</head>
<style type="text/css">
.error {
	color: red;
}
#errorMessage{
	color : red;
}
#loadingDiv{
	  	position:absolute;
		top:0px;
		right:0px;
		width:100%;
		height:inherit;
		background-color:#363636;
		background-image:url('/resources/assets/images/Spinner.gif');
		background-size: 80px 80px;
		background-repeat:no-repeat;
		background-position:center;
		z-index:10000000;
		opacity: 0.6;
	}
</style>

<body class="page-body" data-url="">
<div id="loadingDiv"></div>

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>User On-Premise Import</h2>
			<hr/>
			<div class="clearfix"></div>
			<br>
			
			<div class="row col-md-12">

				<div class="col-md-3">
					<!-- <label class="control-label" for="field_url">Company</label> -->
					<select id="field_company" name="company" onchange="UserOnPremiseImport.showCompanyUrl()"
						class="form-control"><option value="-1">Select Company</option>
						<c:forEach items="${companies}" var="company">
							<option value="${company.pid}">${company.legalName}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-md-6">
						<!-- <label class="control-label" for="field_url">Company URL</label>  -->
						<input autofocus="autofocus" type="text" class="form-control"
							name="url" id="field_url" maxlength="255" placeholder="Company URL" />
							<span id="errorMessage"></span>
				</div>
				<div class="col-md-1">
					<button id="myFormSubmit" class="btn btn-info" onclick="UserOnPremiseImport.updateCompanyApi()">Save Url</button>
				</div>
				<div class="col-md-1">
					<button id="btnImportUsers" class="btn btn-success">Import User</button>
				</div>
				<br>
				<br>
				
				<div class="alert alert-danger alert-dismissible" role="alert" style="display: none;">
					<button type="button" class="close" onclick="$('.alert').hide();" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<p></p>
				</div>
				
				<table  class="table table-striped table-bordered">
				<thead><th>Users</th></thead>
				<tbody id="tblUserDetails"></tbody>
				</table>
			</div>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/user-on-premise-import.js" var="userOnPremiseImportJs"></spring:url>
	<script type="text/javascript" src="${userOnPremiseImportJs}"></script>

</body>
</html>