<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<title>SalesNrich | Mobile Configuration</title>

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
			<h2>Mobile Configuration</h2>
			<div class="clearfix"></div>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success" id="mobilesModal">Create/Update
						Mobile Configuration</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Company Name</th>
						<th>Task Execution Save Offline</th>
						<th>Prompt Attendance Marking</th>
						<th>Prompt DayPlan Update</th>
						<th>Prompt MasterData Update</th>
						<th>Attendance Marking Required</th>
						<th>Day Plan Download Required</th>
						<th>Master Data Update Required</th>
						<th>Build Due Details</th>
						<th>Include Address In AccountList</th>
						<th>Show All Activity Count</th>
						<th>Create Territory</th>
						<th>Real Time Product Price Enabled</th>
						<th>Has Geo Tag</th>
						<th>Has Post Dated Voucher</th>
						<th>Prompt Vehicle</th>
						<th>Add Customer</th>
						<th>Refresh Product ProductGroup Association</th>
						<th>Vch.No Generation Type
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${mobileConfigurations}"
						var="mobileConfiguration" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${mobileConfiguration.companyName}</td>
							<td>${mobileConfiguration.taskExecutionSaveOfflineValue}</td>
							<td>${mobileConfiguration.promptAttendanceMarkingvalue}</td>
							<td>${mobileConfiguration.promptDayPlanUpdate}</td>
							<td>${mobileConfiguration.promptMasterDataUpdate}</td>
							<td>${mobileConfiguration.attendanceMarkingRequired}</td>
							<td>${mobileConfiguration.dayPlanDownloadRequired}</td>
							<td>${mobileConfiguration.masterDataUpdateRequired}</td>
							<td>${mobileConfiguration.buildDueDetail}</td>
							<td>${mobileConfiguration.includeAddressInAccountList}</td>
							<td>${mobileConfiguration.showAllActivityCount}</td>
							<td>${mobileConfiguration.createTerritory}</td>
							<td>${mobileConfiguration.realTimeProductPriceEnabled}</td>
							<td>${mobileConfiguration.hasGeoTag}</td>
							<td>${mobileConfiguration.hasPostDatedVoucher}</td>
							<td>${mobileConfiguration.promptVehicleMaster}</td>
							<td>${mobileConfiguration.addNewCustomer}</td>
							<td>${mobileConfiguration.refreshProductProductGroup}</td>
							<td>${mobileConfiguration.voucherNumberGenerationType}</td>
							<td><button type="button" class="btn btn-info"
									onclick="MobileConfiguration.edit('${mobileConfiguration.pid}','${mobileConfiguration.companyPid}');">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="MobileConfiguration.deleteConfig('${mobileConfiguration.pid}');">Delete</button></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<hr />

			<div class="modal fade container"
				id="assignMobileConfigurationsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Mobile
								Configuration</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">

							<div class="col-md-6 col-md-offset-3">
								<div class="form-group">
									<select id="dbCompany" name="companyPid" class="form-control"><option
											value="-1">Select Company</option>
										<c:forEach items="${companies}" var="company">
											<option value="${company.pid}">${company.legalName}</option>
										</c:forEach>
									</select>
								</div>
							</div>

							<div class="form-group">
								<div id="divSyncOperations">
									<table class='table table-striped table-bordered'>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Task Execution Save
														Offline</label> <input id="saveOffline" name='check'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Prompt Attendance
														Marking</label> <input id="promptAttendance" name='checks'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Prompt DayPlan Update</label>
													<input id="promptDayPlanUpdate" name='checkDayPlan'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Prompt MasterData
														Update</label> <input id="promptMasterDataUpdate"
														name='checkMasterData' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Attendance Marking
														Required</label> <input id="attendanceMarkingRequired"
														name='checkAttendanceMarkingRequired' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Day Plan Download
														Required</label> <input id="dayPlanDownloadRequired"
														name='checkDayPlanDownloadRequired' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Master Data Update
														Required</label> <input id="masterDataUpdateRequired"
														name='checkMasterDataUpdateRequired' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Build Due Details</label> <input
														id="buildDueDetails" name='checkBuildDueDetails'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Include Address In
														AccountList</label> <input id="includeAddressInAccountList"
														name='checkIncludeAddressInAccountList' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Show All Activity
														Count</label> <input id="showAllActivityCount"
														name='checkShowAllActivityCount' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Create Territory</label> <input
														id="createTerritory" name='checkCreateTerritory'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>
										<tr>

											<td><div class="form-group">
													<label class="control-label">Real Time Product
														Price Enabled</label> <input id="realTimeProductPriceEnabled"
														name='checkrealTimeProductPriceEnabled' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Has GeoTag</label> <input
														id="hasGeoTag" name='hasGeoTag' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Has Post Dated Voucher</label>
													<input id="hasPostDatedVoucher" name='hasPostDatedVoucher'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Prompt Vehicle</label> <input
														id="promptVehicleMaster" name='promptVehicleMaster'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Add New Customer</label> <input
														id="addNewCustomer" name='addNewCustomer' type='checkbox'
														checked="checked" class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Refresh Product-ProductGroup</label>
														 <input id="refreshProductProductGroup"
														name='refreshProductProductGroup' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Vchr.No Generation
														type</label> <select id="voucherNumberGenerationType"
														name='voucherNumberGenerationType' class="form-control">
														<option value="TYPE_1" selected="selected">Type_1</option>
														<option value="TYPE_2">Type_2</option>
													</select>
												</div></td>
										</tr>



									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveMobileConfigurations" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="alertBox">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body" id="alertMessage"
							style="font-size: large;"></div>
						<div class="modal-footer">
							<button id="btnDelete" class="btn btn-danger"
								data-dismiss="modal">Ok</button>
							<button class="btn btn-info" data-dismiss="modal">Close</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/mobile-configuration.js"
		var="mobileConfigurationJs"></spring:url>
	<script type="text/javascript" src="${mobileConfigurationJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
		var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

</body>
</html>