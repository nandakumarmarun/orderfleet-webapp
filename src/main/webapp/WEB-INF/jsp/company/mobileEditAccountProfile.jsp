<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | MobileEditAccountProfile</title>
<style type="text/css">
.error {
	color: red;
}

@media ( min-width : 1360px) and (max-width: 1370px) {
	.width83 {
		width: 83%
	}
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Mobile Edited Account Profile</h2>
			<div class="row">
				<div class="col-sm-2">
					Employee<select id="dbEmployee" name="employeePid"
						class="form-control">
						<option value="no">All Employee</option>
						<c:forEach items="${employees}" var="employee">
							<option value="${employee.pid}">${employee.name}</option>
						</c:forEach>
					</select>
				</div>

				<div class="col-sm-2">
					<br /> <select class="form-control" id="dbDateSearch"
						onchange="MobileEditAccountProfile.showDatePicker()">
						<option value="TODAY">Today</option>
						<option value="YESTERDAY">Yesterday</option>
						<option value="WTD">WTD</option>
						<option value="MTD">MTD</option>
						<option value="SINGLE">Single Date</option>
						<option value="CUSTOM">CUSTOM</option>
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
					<br />
					<button type="button" class="btn btn-info"
						onclick="MobileEditAccountProfile.filter()">Apply</button>
				</div>
			</div>
			<br>

			<div class="row">
				<div class="col-sm-4">
					<!-- <button type="button" class="btn btn-success"
						onclick="MobileEditAccountProfile.ImportedStatus();">Verify</button>
					<button type="button" class="btn"
						onclick="MobileEditAccountProfile.Deactivated();">Delete</button> -->
					<button id="btnDownload" type="button" class="btn btn-success">Download
						Xls</button>
				</div>
				<div class="col-sm-4 ">
					<div class="input-group">
						<span class="input-group-addon btn btn-default"
							onclick="$(this).next('input').val('');$('#ofModalSearch').modal('show', {backdrop: 'static'});"><i
							class="glyphicon glyphicon-filter"></i></span><input type="text"
							class="form-control" placeholder="Search...." id="search"
							style="height: 35px" /> <span
							class="input-group-addon btn btn-default" title="Search"
							id="btnSearch"> <i class=" entypo-search"
							style="font-size: 20px"> </i>
						</span>
					</div>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<div class="table-responsive">
				<table class="table  table-striped table-bordered"
					id="tblMobileEditAccountProfile">
					<thead>
						<tr>
							<!-- <th><input type="checkbox" class="allcheckbox"> All
								Select</th> -->
							<th>Created By</th>
							<th>Name</th>
							<th>Alias</th>
							<th>Address</th>
							<th>City</th>
							<th>Phone</th>
							<th>Email</th>
							<th>WhatsApp No</th>
							<th>Date</th>
							<th>GST Number</th>
						</tr>
					</thead>
					<tbody id="tBodyMobileEditAccountProfile">

					</tbody>
				</table>
			</div>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<div class="modal fade custom-width" id="ofModalSearch"
				style="display: none;">
				<div class="modal-dialog" style="width: 96%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title">
								Search for: <b>Account Profile</b>
							</h4>
						</div>
						<div class="modal-body">
							<section class="search-results-env">
								<div class="row">
									<div class="col-md-12">
										<ul class="nav nav-tabs right-aligned">
											<li class="tab-title pull-left">
												<div>
													<button
														onclick="$('.search-results-panes').find('input[type=checkbox]:checked').removeAttr('checked');"
														type="button" class="btn btn-secondary">Clear All</button>
													<b>&nbsp;</b>
												</div>
											</li>
											<li class="active"><a href="#paccountType">Account
													Type</a></li>
											<!-- <li class="active"><a href="#pimportStatus">Import Status</a></li> -->
										</ul>
										<form class="search-bar">
											<div class="input-group">
												<input id="ofTxtSearch" type="text"
													class="form-control input-lg" name="search"
													placeholder="Type for search...">
												<div class="input-group-btn">
													<button class="btn btn-lg btn-primary btn-icon"
														style="pointer-events: none;">
														Search <i class="entypo-search"></i>
													</button>
												</div>
											</div>
										</form>

										<div class="search-results-panes">
											<div class="search-results-pane" id="paccountType"
												style="display: block;">
												<div class="row">
													<c:forEach items="${accountTypes}" var="accountType">
														<div class="col-md-4">
															<div class="checkbox">
																<label> <input type="checkbox"
																	value="${accountType.pid}">${accountType.name}
																</label>
															</div>
														</div>
													</c:forEach>
												</div>
											</div>

										</div>
									</div>
								</div>
							</section>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Close</button>
							<button type="button" class="btn btn-info"
								onclick="MobileEditAccountProfile.filterByAccountType(); $('#ofModalSearch').modal('hide');">Apply</button>
						</div>
					</div>
				</div>
			</div>
			<!-- OF Modal Filter end -->

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/app/mobile-edit-account-profile.js"
		var="mobileEditAccountProfileJs"></spring:url>
	<script type="text/javascript" src="${mobileEditAccountProfileJs}"></script>
</body>
</html>