<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<title>SalesNrich | Day-Wise User Target-Group Setting</title>
<style type="text/css">
.error {
	color: red;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Day-Wise User Target-Group Setting</h2>
			<br />
			<div class="row col-xs-12">
				<div class="col-sm-2">
					<select id="dbEmployee" name="employeePid" class="form-control">
						<option value="no">Select Employee</option>
						<c:forEach items="${employees}" var="employee">
							<option value="${employee.pid}">${employee.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-sm-2">
					<select id="dbDataSalesTargetGroup" name="dataSalesTargetGroupPid"
						class="form-control">
						<option value="no">All SalesTargetGroups</option>
						<c:forEach items="${salesTargetGroups}" var="salesTargetGroup">
							<option value="${salesTargetGroup.pid}">${salesTargetGroup.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-sm-2  custom_date1">
					<div class="input-group">
						<input type="text" class="form-control" id="txtStartDate"
							placeholder="Select From Date" style="background-color: #fff;"
							readonly="readonly" />
						<div class="input-group-addon">
							<a href="#"><i class="entypo-calendar"></i></a>
						</div>
					</div>
				</div>
				<div class="col-sm-2  custom_date2">
					<div class="input-group">
						<input type="text" class="form-control" id="txtEndDate"
							placeholder="Select To Date" style="background-color: #fff;"
							readonly="readonly" />
						<div class="input-group-addon">
							<a href="#"><i class="entypo-calendar"></i></a>
						</div>
					</div>
				</div>
				<div class="col-sm-1">
					<button id="btnApply" type="button" class="btn btn-info">Load</button>
				</div>
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						id="btnViewDayWiseUserTargetSetting">create new</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table">
				<thead>
					<tr>
						<th>User</th>
						<th>SalesTargetGroup</th>
						<th>Date</th>
						<th>Day</th>
						<th>Quantity</th>
					</tr>
				</thead>
				<tbody id="tbMainDayWiseUserTargetSetting">
					<c:forEach items="${salesTargetGroupUserTargets}"
						var="dayWiseUserTargetSetting" varStatus="loopStatus">
						<tr>
							<td>${dayWiseUserTargetSetting.userName}</td>
							<td>${dayWiseUserTargetSetting.salesTargetGroupName}</td>
							<td>${dayWiseUserTargetSetting.fromDate}</td>
							<td>${dayWiseUserTargetSetting.day}</td>
							<td>${dayWiseUserTargetSetting.volume}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/sales-target-group-userTargets"
				var="urlDayWiseUserTargetSetting"></spring:url>

			<div id="dayWiseUserTargetSettingForm">
				<!-- Model Container-->
				<div class="modal fade container" id="myModal" style="width: 100%">
					<!-- model Dialog -->
					<div class="modal-dialog"
						style="width: 75%; height: 750px; overflow: auto;">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Create or edit a
									Day Wise User Target group Setting</h4>
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
								<div class="row col-xs-15">
									<div class="col-sm-2">
										<select id="dbUserId" name="employeePid"
											class="form-control">
											<option value="no">Select Employee</option>
											<c:forEach items="${employees}" var="employee">
												<option value="${employee.pid}">${employee.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="col-sm-2">
										<select id="dbsalesTargetGroup" name="salesTargetGroupPid"
											class="form-control">
											<option value="no">All SalesTargetGroups</option>
											<c:forEach items="${salesTargetGroups}"
												var="salesTargetGroup">
												<option value="${salesTargetGroup.pid}">${salesTargetGroup.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="col-sm-2  custom_date1">
										<div class="form-group">
											<input type="text" class="form-control" id="txtFromDate"
												placeholder="Select From Date"
												style="background-color: #fff;" readonly="readonly" />
											<!-- <div class="input-group-addon">
												<a href="#"><i class="entypo-calendar"></i></a>
											</div> -->
										</div>
									</div>
									<div class="col-sm-2  custom_date2">
										<div class="form-group">
											<input type="text" class="form-control" id="txtToDate"
												placeholder="Select To Date" style="background-color: #fff;"
												readonly="readonly" />
											<!-- <div class="input-group-addon">
												<a href="#"><i class="entypo-calendar"></i></a>
											</div> -->
										</div>
									</div>

									<div class="col-sm-2">
										<button type="button" class="btn btn-success"
											id="btnSaveDayWiseUserTargetSetting">Load</button>
									</div>
									<div class="col-sm-2">
										<button type="button" class="btn btn-info"
											title="set common quantity for the table" id="btncommonQTY">common
											quantity</button>
									</div>
								</div>
								<div>
									<table class="table" id="tbleDayWiseUserTarget">
										<thead>
											<tr>
												<th>Date</th>
												<th>Day</th>
												<th>Quantity</th>
											</tr>
										</thead>
										<tbody id="tbDayWiseUserTargetSetting">
										</tbody>
									</table>
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
				<!-- /.Model Container-->
			</div>

			<div class="modal fade container" id="defaultValueModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content" style="background-color: #eaf5fd;">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Default Quantity Value</h4>
						</div>
						<div class="modal-body">
							<div class="form-group">
								<label class="control-label" for="field_defaultValue">Default
									Value</label> <input type="number" class="form-control"
									name="defaultValue" id="field_defaultValue" maxlength="6"
									placeholder="default value" value="0" />
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSetValue"
								value="OK" />
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

	<spring:url value="/resources/app/day_wise_targer_setting.js"
		var="dayWiseUserTargetSettingJs"></spring:url>
	<script type="text/javascript" src="${dayWiseUserTargetSettingJs}"></script>
</body>
</html>