<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<title>SalesNrich | Company Integration Module</title>

<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Company Integration Module</h2>
			<div class="clearfix"></div>
			<hr />
			<form id="integrationModuleForm">
				<div class="form-group">
					<label class="control-label">Company</label> <select id="dbCompany"
						class="form-control selectpicker" data-live-search="true"><option value="no">Select
							Company</option>
						<c:forEach items="${companies}" var="company">
							<option value="${company.pid}">${company.legalName}</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group">
					<label class="control-label">Integration Module</label> <select
						id="dbIntegrationModules" class="form-control">
						<option value="no">Select Integration Module</option>
						<c:forEach items="${integration_module}" var="integration">
							<option value="${integration.name}">${integration.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group">
					<label class="control-label">URL</label> <input type="text"
						id="moduleUrl" class="form-control" placeholder="Enter the url">
				</div>
				<div class="form-check">
					<input type="checkbox" class="form-check-input" id="isEnabledCheckBox">
					<label class="form-check-label" for="isEnabledCheckBox">Auto Update</label>
				</div>

			</form>
			<button id="myFormSubmit" class="btn btn-success"
				onclick="saveCompanyIntegrationModule()">Save</button>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/account-type.js" var="accountTypeJs"></spring:url>
	<script type="text/javascript" src="${accountTypeJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
		var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

	<script type="text/javascript">
		$(document).ready(function() {

			$('.selectpicker').selectpicker();
			/* $("#txtFromDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});
			$("#txtToDate").datepicker({
				dateFormat : 'yy-mm-dd'
			}); */

			var company = "${company}";
			console.log(company);
		});

		var contextPath = location.protocol + '//' + location.host
				+ location.pathname;

		function saveCompanyIntegrationModule() {
			if ($("#dbCompany").val() == 'no') {
				alert("Please select company");
				return;
			}
			if ($("#dbIntegrationModules").val() == 'no') {
				alert("Please select Integration Module");
				return;
			}
			if ($("#moduleUrl").val() == '') {
				alert("Please enter url");
				return;
			}

			$.ajax({
				url : contextPath,
				method : 'POST',
				data : {
					companyPid : $("#dbCompany").val(),
					integrationModuleName : $("#dbIntegrationModules").val(),
					baseUrl : $("#moduleUrl").val(),
					autoUpdate : $("#isEnabledCheckBox").prop('checked') ? true : false
					/* autoUpdate : $("#isEnabledCheckBox").prop('checked') ? true : false; */
				},
				success : function(data) {
					alert("Module added successfully...");
					$('#integrationModuleForm').trigger("reset");
				},
				error : function(xhr, error) {
					console.log(error);
					console.log(xhr);
					onError(xhr, error);
				}

			});
		}
	</script>
</body>
</html>