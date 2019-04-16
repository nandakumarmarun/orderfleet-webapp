<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Financial Closing Report</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Financial Closing Report</h2>

			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="col-md-12 clearfix">
					<form id="frmFilter" role="form"
						class="form-horizontal form-groups-bordered" method="post"
						action="/web/financial-closing-report">
						<input type="hidden" id="selectedEmployeePid"
							value='${employeePid}' />
						<div class="form-group">
							<div class="col-sm-6">
								<select id="dbEmployee" name="employeePid" class="form-control">
									<option value="no">All Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-3">
								<button id="btnFilter" type="submit" class="btn btn-info">Load</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<spring:url value="/web/close-financial-report" var="closeFinanceUrl" />
			<div id="divPreLoader" style="display: none;">
				<img src="/resources/assets/images/content-ajax-loader.gif"
					style="display: block; margin: auto;">
			</div>
			<form:form id="frmClose" method="post"
				modelAttribute="fClosingReportHolder" action="${closeFinanceUrl}">
				<form:input type="hidden" path="selectedUserPid" />
				<div class="row">
					<c:if test="${not empty fClosingReportHolder.financialClosings}">
						<div class="col-md-6">
							<div class="panel panel-primary">
								<!-- panel head -->
								<div class="panel-heading">
									<div class="panel-title">Financial Closing</div>
									<div class="panel-options">
										<a href="#" data-rel="close"><i class="entypo-cancel"></i></a>
									</div>
								</div>
								<!-- panel body -->
								<div class="panel-body with-table">
									<table class="table table-bordered table-responsive">
										<thead>
											<tr>
												<th>Name</th>
												<th>Amount</th>
											</tr>
										</thead>
										<tbody id="tblFinanceClosingReport">
											<c:forEach items="${fClosingReportHolder.financialClosings}"
												var="fClosingReport" varStatus="fc">
												<tr>
													<form:input type="hidden"
														path="financialClosings[${fc.index}].documentPid" />
													<form:input type="hidden"
														path="financialClosings[${fc.index}].documentType" />
													<form:input type="hidden"
														path="financialClosings[${fc.index}].paymentMode" />
													<td><form:input type="hidden"
															path="financialClosings[${fc.index}].documentName" />${fClosingReport.documentName}</td>
													<td><form:input type="hidden"
															path="financialClosings[${fc.index}].debitCredit" /> <form:input
															type="hidden"
															path="financialClosings[${fc.index}].amount" /> <c:choose>
															<c:when test="${fClosingReport.debitCredit == 'Dr'}">
															- &nbsp; <fmt:formatNumber type="number"
																	maxFractionDigits="2" value="${fClosingReport.amount}" />
															</c:when>
															<c:otherwise>
																<fmt:formatNumber type="number" maxFractionDigits="2"
																	value="${fClosingReport.amount}" />
															</c:otherwise>
														</c:choose></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</c:if>

					<c:if test="${not empty fClosingReportHolder.pettyCashClosings}">
						<div class="col-md-6">
							<div class="panel panel-primary">
								<!-- panel head -->
								<div class="panel-heading">
									<div class="panel-title">Petty Cash</div>
									<div class="panel-options">
										<a href="#" data-rel="close"><i class="entypo-cancel"></i></a>
									</div>
								</div>
								<!-- panel body -->
								<div class="panel-body with-table">
									<table class="table table-bordered table-responsive">
										<thead>
											<tr>
												<th>Name</th>
												<th>Amount</th>
											</tr>
										</thead>
										<tbody id="tblPettyCashReport">
											<tr>
												<td>Opening Balance</td>
												<td><form:input type="hidden" path="openingBalance" />
													<fmt:formatNumber type="number" maxFractionDigits="2"
														value="${fClosingReportHolder.openingBalance}"></fmt:formatNumber></td>
											</tr>
											<c:forEach items="${fClosingReportHolder.pettyCashClosings}"
												var="pettyCashReport" varStatus="pc">
												<tr>
													<form:input type="hidden"
														path="pettyCashClosings[${pc.index}].documentPid" />
													<form:input type="hidden"
														path="pettyCashClosings[${pc.index}].documentType" />
													<form:input type="hidden"
														path="pettyCashClosings[${pc.index}].paymentMode" />
													<td><form:input type="hidden"
															path="pettyCashClosings[${pc.index}].documentName" />${pettyCashReport.documentName}</td>
													<td><form:input type="hidden"
															path="pettyCashClosings[${pc.index}].debitCredit" /> <form:input
															type="hidden"
															path="pettyCashClosings[${pc.index}].amount" /> <c:choose>
															<c:when test="${pettyCashReport.debitCredit == 'Dr'}">
															- &nbsp; <fmt:formatNumber type="number"
																	maxFractionDigits="2" value="${pettyCashReport.amount}" />
															</c:when>
															<c:otherwise>
																<fmt:formatNumber type="number" maxFractionDigits="2"
																	value="${pettyCashReport.amount}" />
															</c:otherwise>
														</c:choose></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</c:if>
				</div>
				<div class="row">
					<c:if test="${not empty fClosingReportHolder.financialClosings}">
						<div class="col-md-6" align="right">
							<h3>
								<b>Total : </b>
								<form:input type="hidden" path="fClosingTotal" />
								<fmt:formatNumber type="number" maxFractionDigits="2"
									value="${fClosingReportHolder.fClosingTotal}" />
							</h3>
						</div>
					</c:if>
					<c:if test="${not empty fClosingReportHolder.pettyCashClosings}">
						<div class="col-md-6" align="right">
							<h3>
								<b>Total : </b>
								<form:input type="hidden" path="pettyCashTotal" />
								<fmt:formatNumber type="number" maxFractionDigits="2"
									value="${fClosingReportHolder.pettyCashTotal}" />
							</h3>
						</div>
					</c:if>
				</div>
				<br />
				<br />
				<br />
				<sec:authorize access="hasAnyRole('ADMIN','MANAGER')">
					<div class="row">
						<div class="col-md-12" align="center">
							<button id="btnClose" type="submit" class="btn btn-success">Close</button>
						</div>
					</div>
				</sec:authorize>
			</form:form>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<script type="text/javascript">
		var financeClosingContextPath = location.protocol + '//'
				+ location.host;
		$(document).ready(function() {
			var empPid = $("#selectedEmployeePid").val();
			if (empPid.length > 0) {
				$("#dbEmployee").val(empPid);
			}
			$("#frmFilter").submit(function(event) {
				$("#btnFilter").html('Loading...');
				$("#btnFilter").prop("disabled", true);
			});

			$("#frmClose").submit(function(event) {
				if (confirm("Are you sure!")) {
					$("#frmClose").css('display', 'none');
					$("#divPreLoader").css('display', 'block');
				} else {
					event.preventDefault();
				}
			});
		});
	</script>
</body>
</html>