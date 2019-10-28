<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<title>SalesNrich | Company Performance Config</title>

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
			<h2>Company Performance Config</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="modal-content">
				<div class="modal-body">
					<div class="modal-body" style="overflow: auto;">
						<div class="form-group">
							<select id="dbCompany" class="form-control selectpicker" data-live-search="true"
								onchange="getCompanyPerformanceConfig()"><option
									value="no">Select Company</option>
								<c:forEach items="${companies}" var="company">
									<option value="${company.pid}">${company.legalName}</option>
								</c:forEach>
							</select>
						</div>
						<div class="form-group">
							<label class="control-label">Voucher Type</label> <select
								id="dbVoucherType" class="form-control">
								<option value="no">Select Voucher Type</option>
								<option value="PRIMARY_SALES">Primary Sales</option>
								<option value="PRIMARY_SALES_ORDER">Primary Sales Order</option>
								<option value="SECONDARY_SALES">Secondary Sales</option>
								<option value="SECONDARY_SALES_ORDER">Secondary Sales
									Order</option>
								<option value="PURCHASE">Purchase</option>
								<option value="PURCHASE_ORDER">Purchase Order</option>
							</select>
						</div>
						<div class="form-group">
							<div>
								<div class="radio radio-replace" id="userWise">
									<input type="radio" name="userLocationWise" value="USER_WISE"
										checked="checked"> <label>User Wise</label>
								</div>
								<br />
								<div class="radio radio-replace" id="locationWise">
									<input type="radio" name="userLocationWise"
										value="LOCATION_WISE"> <label>Location Wise</label>
								</div>
							</div>
						</div>
					</div>
				</div>
				<!-- class="modal-footer" -->
				<div>
					<button id="myFormSubmit" class="btn btn-primary"
						onclick="saveCompanyPerformanceConfig()">Save</button>
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

			$('.selectpicker').selectpicker();
			$("#txtFromDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});
			$("#txtToDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});

			var company = "${company}";
			console.log(company);
		});

		var contextPath = location.protocol + '//' + location.host
				+ location.pathname;

		function saveCompanyPerformanceConfig() {
			if ($("#dbCompany").val() == 'no') {
				alert("Please select company");
				return;
			}
			if ($("#dbVoucherType").val() == 'no') {
				alert("Please select voucher type");
				return;
			}
			$
					.ajax({
						url : contextPath,
						method : 'POST',
						data : {
							companyPid : $("#dbCompany").val(),
							voucherType : $("#dbVoucherType").val(),
							userLocationWise : $(
									'input[name=userLocationWise]:checked')
									.val()
						},
						success : function(data) {
							alert("Period updated successfully...");
						},
						error : function(xhr, error) {
							onError(xhr, error);
						}
					});
		}

		function getCompanyPerformanceConfig() {
			$
					.ajax({
						url : contextPath + "/" + $("#dbCompany").val(),
						method : 'GET',
						success : function(performanceConfigs) {
							$
									.each(
											performanceConfigs,
											function(index, performanceConfig) {
												$('#locationWise').removeClass(
														'checked');
												$('#userWise').removeClass(
														'checked');
												if (performanceConfig.name == 'COMPANY_PERFORMANCE_BASED_ON') {
													$("#dbVoucherType").val(performanceConfig.value);
												} else if (performanceConfig.name == 'USER_WISE') {
													$('#userWise').addClass(
															'checked');
												} else if (performanceConfig.name == 'LOCATION_WISE') {
													$('#locationWise')
															.addClass('checked');
												} else {
													$('#userWise').addClass(
															'checked');
												}

											});

						},
						error : function(xhr, error) {
							onError(xhr, error);
						}
					});
		}
	</script>
</body>
</html>