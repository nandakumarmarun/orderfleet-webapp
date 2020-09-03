<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Net Sales And Collection Report</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Net Sales And Collection Report</h2>
			<%-- <div class="row col-xs-12">
				<security:authorize access="hasAnyRole('MASTER_DATA_MANAGER')">
					<div class="pull-right">
						<button type="button" class="btn btn-success"
							onclick="Document.showModalPopup($('#myModal'));">Create
							new Document</button>
					</div>
				</security:authorize>
			</div> --%>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								Employee <select id="dbEmployee" name="employeePid"
									class="form-control">
									<option value="all">All Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="NetSalesAndCollectionReport.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="SINGLE">Single Date</option>
								</select>
							</div>
							<div class="col-sm-2 hide custom_date1">
								<br />
								<div class="input-group">
									<input type="text" class="form-control" id="txtFromDate"
										placeholder="Select From Date" style="background-color: #fff;"
										readonly="readonly" />

									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-2 hide custom_date2">
								<br />
								<div class="input-group">
									<input type="text" class="form-control" id="txtToDate"
										placeholder="Select To Date" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-info" id="btnApply">Apply</button>
							</div>
							<!-- 							<div class="col-sm-2" style="margin-top: 23px">
								<button type="button" class="btn btn-success"
									id="btnPrint">Print</button>
							</div> -->
							<div class="col-sm-2">
								<br>
								<button type="button" class="btn btn-success"
									onclick="window.print()">Print</button>
							</div>
						</div>
					</form>
				</div>
			</div>

			<table id='tableNetSalesAndCollection'
				class="table  table-striped table-bordered" border="1"
				cellpadding="3">
				<tbody id='tBodyNetSalesAndCollection'>
					<tr>
						<td>Net Sales Amount</td>
						<td><label id='lblNetSalesAmount'>0.00</label></td>
					</tr>
					<tr>
						<td>Net Collection Amount</td>
						<td><label id='lblNetCollectionAmount'>0.00</label></td>
					</tr>
					<tr>
						<td>Net Collection Amount - Cash</td>
						<td><label id='lblNetCollectionAmountCash'>0.00</label></td>
					</tr>
					<tr>
						<td>Net Collection Amount - Cheque</td>
						<td><label id='lblNetCollectionAmountCheque'>0.00</label></td>
					</tr>
					<tr>
						<td>Net Collection Amount - RTGS</td>
						<td><label id='lblNetCollectionAmountRtgs'>0.00</label></td>
					</tr>

				</tbody>
			</table>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/netSalesAndCollectionReport.js"
		var="netSalesAndCollectionReportJs"></spring:url>
	<script type="text/javascript" src="${netSalesAndCollectionReportJs}"></script>
</body>
</html>