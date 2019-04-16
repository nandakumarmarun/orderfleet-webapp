<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<title>SalesNrich | Financial Period</title>

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
			<h2>Financial Period</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="modal-content">
				<div class="modal-body">
					<div class="alert alert-danger alert-dismissible" role="alert"
						style="display: none;">
						<button type="button" class="close" onclick="$('.alert').hide();"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<p></p>
					</div>
					<h4>${company.legalName}</h4>
					<br />
					<div class="modal-body" style="overflow: auto;">
						<div>
							<fmt:parseDate value="${company.periodEndDate}"
								pattern="yyyy-MM-dd" var="periodEndDate" type="date" />
							<fmt:formatDate value="${periodEndDate}" type="date"
								pattern="${pattern}" />
						</div>


						<div class="form-group">
							<label class="control-label">Start Date</label> <input
								id="txtFromDate" type="text" class="form-control"
								placeholder="Start Date" value="${company.periodStartDate}" />
						</div>
						<div class="form-group">
							<label class="control-label">End Date</label> <input
								id="txtToDate" type="text" class="form-control"
								placeholder="End Date" value="${company.periodEndDate}" />
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button id="myFormSubmit" class="btn btn-primary"
						onclick="savePeriod()">Save</button>
				</div>
			</div>
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
			$("#txtFromDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});
			$("#txtToDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});
			var company = "${company}";
		});

		var periodContextPath = location.protocol + '//' + location.host
				+ location.pathname;

		function savePeriod() {

			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				alert("Select period from and to date");
				return;
			}

			$.ajax({
				url : periodContextPath,
				method : 'POST',
				data : {
					fromDate : $("#txtFromDate").val(),
					toDate : $("#txtToDate").val()
				},
				success : function(data) {
					alert("Period updated successfully...");
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	</script>
</body>
</html>