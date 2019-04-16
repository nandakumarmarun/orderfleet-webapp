<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Sales Target Report Settings</title>
</head>
<style type="text/css">
.error {
	color: red;
}
</style>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Sales Target Report Settings</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="SalesTargetReportSettings.showModalPopup($('#myModal'));">Create
						new Sales Target Report Settings</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Account Wise Target</th>
						<th>Target Frequency</th>
						<th>Target Type</th>
						<th>Monthly Average</th>
						<th>Target Setting Type</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${pageSalesTargetReportSettings}"
						var="salesTargetReportSettings" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${salesTargetReportSettings.name}</td>
							<td><c:choose>
									<c:when
										test="${salesTargetReportSettings.accountWiseTarget== true}">Account Wise </c:when>
									<c:otherwise>User</c:otherwise>
								</c:choose></td>
							<td>${salesTargetReportSettings.targetPeriod}</td>
							<td>${salesTargetReportSettings.targetType}</td>
							<td>${salesTargetReportSettings.monthlyAverageWise}</td>
							<td>${salesTargetReportSettings.targetSettingType}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="SalesTargetReportSettings.showModalPopup($('#myModal'),'${salesTargetReportSettings.pid}',1,'');">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="SalesTargetReportSettings.showModalPopup($('#deleteModal'),'${salesTargetReportSettings.pid}',2,'');">Delete</button>
								<button type="button" class="btn btn-info"
									onclick="SalesTargetReportSettings.showModalPopup($('#assignTargetBlockModal'),'${salesTargetReportSettings.pid}',3,'${salesTargetReportSettings.targetSettingType}');">Assign
									Target Blocks</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/salesTargetReportSettings"
				var="urlSalesTargetReportSettings"></spring:url>

			<form id="salesTargetReportSettingsForm" role="form" method="post"
				action="${urlSalesTargetReportSettings}">
				<!-- Model Container-->
				<div class="modal fade container" id="myModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Create or edit a
									Sales Target Report Settings</h4>
							</div>
							<div class="modal-body">
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>
								<div class="modal-body" style="overflow: auto;">
									<div class="form-group">
										<label class="control-label" for="field_name">Name</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="name" id="field_name" maxlength="255"
											placeholder="Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_accountWiseTarget">Account
											Wise Target</label> <br> <input type="radio"
											name="accountWiseTarget" id="field_accountWiseTarget_account"
											value="true"> Account Wise <br> <input
											type="radio" name="accountWiseTarget"
											id="field_accountWiseTarget_user" value="false"> User
									</div>
									<div class="form-group col-xs-12">
										<div class="col-xs-4">
											<label class="control-label" for="field_targetPeriod">Target
												Period</label> <select id="field_targetPeriod" name="targetPeriod"
												class="form-control">
												<option value="DAY">Day</option>
												<option value="WEEK">Week</option>
												<option value="MONTH">Month</option>
												<option value="YEAR">Year</option>
												<option value="QUARTERLY">Quarterly</option>
												<option value="HALF_YEARLY">Half yearly</option>
											</select>
										</div>
									</div>
									<div class="form-group">
										<input type="checkbox" name="createDynamicLabel"
											id="field_createDynamicLabel_amount" /> Amount <br /> <input
											type="checkbox" name="createDynamicLabel"
											id="field_createDynamicLabel_volume" /> Volume
									</div>

									<div class="form-group">
										<input type="checkbox" name="monthlyAverageWiseLabel"
											id="field_monthlyAverageWise" /> Monthly Average Wise
									</div>

									<div class="form-group col-xs-12">
										<div class="col-xs-6">
											<label class="control-label" for="field_mobileUIName">Mobile
												UI Name</label> <select id="field_mobileUIName" name="mobileUIName"
												class="form-control">
												<option value="SELECT">Select</option>
												<option value="DEALER_PERFORMANCE">Dealer
													Performance</option>
												<option value="USER_PERFORMANCE">User Performance</option>
											</select>
										</div>
									</div>

									<div class="form-group">
										<label class="control-label" for="field_targetSettingType">Target
											Type</label> <select id="field_targetSettingType"
											name="targetSettingType" class="form-control">
											<option value="-1">Select Target Setting Type</option>
											<c:forEach items="${targetSettingTypes}"
												var="targetSettingType">
												<option value="${targetSettingType}">${targetSettingType}</option>
											</c:forEach>
										</select>
									</div>

								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button id="myFormSubmit" class="btn btn-primary">Save</button>
								</div>
							</div>
							<!-- /.modal-content -->
						</div>
						<!-- /.modal-dialog -->
					</div>
				</div>
				<!-- /.Model Container-->
			</form>

			<form name="viewForm" role="form">
				<!-- Model Container-->
				<div class="modal fade container" id="viewModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="viewModalLabel">Sales Target
									Report Settings</h4>
							</div>
							<div class="modal-body">
								<div class="modal-body" style="overflow: auto;">
									<!-- error message -->
									<div class="alert alert-danger alert-dismissible" role="alert"
										style="display: none;">
										<button type="button" class="close"
											onclick="$('.alert').hide();" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
										<p></p>
									</div>

									<dl class="dl-horizontal">
										<dt>
											<span>Name</span>
										</dt>
										<dd>
											<span id="lbl_name"></span>
										</dd>
										<hr />
										<dt>
											<span>Alias</span>
										</dt>
										<dd>
											<span id="lbl_alias"></span>
										</dd>
										<hr />
										<dt>
											<span>Description</span>
										</dt>
										<dd>
											<span id="lbl_description"></span>
										</dd>
										<hr />
									</dl>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

			<form id="deleteForm" name="deleteForm"
				action="${urlSalesTargetReportSettings}">
				<!-- Model Container-->
				<div class="modal fade container" id="deleteModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title">Confirm delete operation</h4>
							</div>
							<div class="modal-body">

								<div class="modal-body" style="overflow: auto;">
									<!-- error message -->
									<div class="alert alert-danger alert-dismissible" role="alert"
										style="display: none;">
										<button type="button" class="close"
											onclick="$('.alert').hide();" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
										<p></p>
									</div>
									<p>Are you sure you want to delete this Sales Target Report
										Settings ?</p>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button class="btn btn-danger">Delete</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

			<div class="modal fade container" id="assignTargetBlockModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Sales Target Modal</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divTargetBlock">
									<div class="row">
										<div class="col-md-12 col-sm-12 clearfix">
											<input type="radio" value="all" name="filter"
												checked="checked"> &nbsp;All&nbsp;&nbsp; <input
												type="radio" value="selected" name="filter">
											&nbsp;Selected&nbsp;&nbsp; <input type="radio"
												value="unselected" name="filter">
											&nbsp;Unselected&nbsp;&nbsp;

											<button type="button" class="btn btn-info" id="btnSearch"
												style="float: right;">Search</button>
											<input type="text" id="search" placeholder="Search..."
												class="form-control" style="width: 200px; float: right;">
										</div>
									</div>
									<br />
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>Target Block</th>
												<th>Sort Order</th>
											</tr>
										</thead>
										<tbody id="tbodyTargetBlock">
											<%-- <c:forEach items="${targetBlocks}" var="targetBlock">
												<tr>
													<td><input name='targetBlock' type='checkbox'
														value="${targetBlock.pid}" /></td>
													<td>${targetBlock.name}</td>
													<td><input type="number"
														id="txtOrder${targetBlock.pid}" value="0" /></td>
												</tr>
											</c:forEach> --%>
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveTargetBlocks" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/sales-target-report-settings.js"
		var="salesTargetReportSettingsJs"></spring:url>
	<script type="text/javascript" src="${salesTargetReportSettingsJs}"></script>
</body>
</html>