<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>
<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<title>SalesNrich | Best Performers</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<h2>Best-Performer</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								<select class="form-control" id="dbDateSearch"
									onchange="BestPerformer.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
									<option value="MTD">MTD</option>
									<option value="CUSTOM">CUSTOM</option>
								</select>
							</div>
							<div class="col-sm-2 hide custom_date1">
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
								<button type="button" class="btn btn-info"
									onclick="BestPerformer.filter()">Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6">
					<div class="panel panel-primary" data-collapsed="0">
						<!-- panel head -->
						<div class="panel-heading">
							<div class="panel-title">Sales Details</div>
						</div>
						<!-- panel body -->
						<div class="panel-body">
							<div class="col-xs-12 col-sm-12 col-md-12 table-responsive">
								<p style="font-size: 20px;"class="text-success lead"><strong>Best Sales Performer : <span id="sPerformerName"></span></strong></p>
								<table
									class="table table-condensed table-striped table-bordered table-hover">
									<thead>
										<tr>
											<th style="width: 45%">Employee name</th>
											<th style="width: 25%">Sale Count</th>
										</tr>
									</thead>
									<tbody id="tbl_salesBestPerformers">
										
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="panel panel-primary" data-collapsed="0">
						<!-- panel head -->
						<div class="panel-heading">
							<div class="panel-title">Receipt Details</div>
						</div>
						<!-- panel body -->
						<div class="panel-body">
							<div class="col-xs-12 col-sm-12 col-md-12 table-responsive">
							<p style="font-size: 20px;"class="text-success lead"><strong>Best Receipt Performer : <span id="rPerformerName"></span></strong></p>
							
								<table
									class="table table-condensed table-striped table-bordered table-hover">
									<thead>
										<tr>
											<th style="width: 45%">Employee name</th>
											<th style="width: 25%">Receipt Count</th>
										</tr>
									</thead>
									<tbody id="tbl_receiptBestPerformers">
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
				<hr />
				<!-- Footer -->
				<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			</div>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/best-performer.js"
		var="bestPerformerJs"></spring:url>
	<script type="text/javascript" src="${bestPerformerJs}"></script>
</body>
</html>