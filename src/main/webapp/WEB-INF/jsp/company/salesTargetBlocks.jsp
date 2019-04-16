<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Sales Target Blocks</title>
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
			<h2>Sales Target Blocks</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="SalesTargetBlock.showModalPopup($('#myModal'));">Create
						new Sales Target Block</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Start</th>
						<th>End</th>
						<th>Target Setting Type</th>
						<th>Description</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${pageSalesTargetBlocks}" var="salesTargetBlock"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${salesTargetBlock.name}</td>
							<td>${salesTargetBlock.startMonthName}</td>
							<td>${salesTargetBlock.endMonthName}</td>
							<td>${salesTargetBlock.targetSettingType}</td>
							<td>${salesTargetBlock.description}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="SalesTargetBlock.showModalPopup($('#myModal'),'${salesTargetBlock.pid}',1,'');">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="SalesTargetBlock.showModalPopup($('#deleteModal'),'${salesTargetBlock.pid}',2,'');">Delete</button>
								<button type="button" class="btn btn-info"
									onclick="SalesTargetBlock.showModalPopup($('#salesTargetgroupsModal'),'${salesTargetBlock.pid}',3,'${salesTargetBlock.targetSettingType}');">Assign
									Sales Target group</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/salesTargetBlocks" var="urlSalesTargetBlock"></spring:url>

			<form id="salesTargetBlockForm" role="form" method="post"
				action="${urlSalesTargetBlock}">
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
									Sales Target Block</h4>
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
										<label class="control-label" for="field_name">Label</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="name" id="field_name" maxlength="255"
											placeholder="Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="hasDefaultAccount">Create
											Dynamic Label ?</label> <input type="checkbox" class="form-control"
											name="createDynamicLabel" id="field_createDynamicLabel"
											style="width: 4%;" />
									</div>
									<div class="form-group col-xs-12">
										<div class="col-xs-4">
											<label class="control-label" for="field_startMonth">Start
												Date</label> <select id="field_startMonth" name="startMonth"
												class="form-control"
												onchange="SalesTargetBlock.onchangeStartMonth()">
												<option value="0">Current Month</option>
												<option value="1">January</option>
												<option value="2">February</option>
												<option value="3">March</option>
												<option value="4">April</option>
												<option value="5">May</option>
												<option value="6">June</option>
												<option value="7">July</option>
												<option value="8">August</option>
												<option value="9">September</option>
												<option value="10">October</option>
												<option value="11">November</option>
												<option value="12">December</option>
											</select>
										</div>
										<div class="col-xs-4">
											<label class="control-label" for="field_startMonthMinus">Month
												Minus</label> <input type="number" class="form-control"
												name="startMonthMinus" id="field_startMonthMinus" min="0"
												max="12" value="0" />
										</div>
										<div class="col-xs-4">
											<label class="control-label" for="field_startMonthYearMinus">Year
												Minus</label> <input type="number" class="form-control"
												name="startMonthYearMinus" id="field_startMonthYearMinus"
												min="0" max="10" value="0" />
										</div>
									</div>
									<div class="form-group col-xs-12">
										<div class="col-xs-4">
											<label class="control-label" for="field_endMonth">End
												Date</label> <select id="field_endMonth" name="endMonth"
												class="form-control"
												onchange="SalesTargetBlock.onchangeEndMonth()">
												<option value="0">Current Month</option>
												<option value="1">January</option>
												<option value="2">February</option>
												<option value="3">March</option>
												<option value="4">April</option>
												<option value="5">May</option>
												<option value="6">June</option>
												<option value="7">July</option>
												<option value="8">August</option>
												<option value="9">September</option>
												<option value="10">October</option>
												<option value="11">November</option>
												<option value="12">December</option>
											</select>
										</div>
										<div class="col-xs-4">
											<label class="control-label" for="field_endMonthMinus">MonthMinus</label>
											<input type="number" class="form-control"
												name="endMonthMinus" id="field_endMonthMinus" min="0"
												max="12" value="0" />
										</div>
										<div class="col-xs-4">
											<label class="control-label" for="field_endMonthYearMinus">Year
												Minus</label> <input type="number" class="form-control"
												name="endMonthYearMinus" id="field_endMonthYearMinus"
												min="0" max="10" value="0" />
										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_targetSettingType">Target
										Type</label> <select id="field_targetSettingType" name="targetSettingType"
										class="form-control">
										<option value="-1">Select Target Setting Type</option>
										<c:forEach items="${targetSettingTypes}" var="targetSettingType">
											<option value="${targetSettingType}">${targetSettingType}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_description">Description</label>
									<textarea class="form-control" name="description"
										id="field_description" placeholder="Description"></textarea>
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
									Block</h4>
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
				action="${urlSalesTargetBlock}">
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
									<p>Are you sure you want to delete this Sales Target Block?</p>
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


			<div class="modal fade container" id="salesTargetgroupsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Sales Target Groups</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="salesTargetgroupsCheckboxes">
									<div class="row">
										<div class="col-md-12 col-sm-12 clearfix">
											<input type="radio" value="all" name="filter">
											&nbsp;All&nbsp;&nbsp; <input type="radio" value="selected"
												name="filter"> &nbsp;Selected&nbsp;&nbsp; <input
												type="radio" value="unselected" name="filter">
											&nbsp;Unselected&nbsp;&nbsp;

											<button type="button" class="btn btn-info" id="btnSearch"
												style="float: right;">Search</button>
											<input type="text" id="search" placeholder="Search..."
												class="form-control" style="width: 200px; float: right;">
										</div>
									</div>
									<br>
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">
													All</th>
												<th>Sales Target group</th>
											</tr>
										</thead>
										<tbody id="tblSalesTargetgroups">
											<%-- <c:forEach items="${salesTargetgroups}"
												var="salesTargetgroup">
												<tr>
													<td><input name='salesTargetgroup' type='checkbox'
														value="${salesTargetgroup.pid}" /></td>
													<td>${salesTargetgroup.name}</td>
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
								id="btnSaveSalesTargetgroups" value="Save" />
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

	<spring:url value="/resources/app/sales-target-block.js"
		var="salesTargetBlockJs"></spring:url>
	<script type="text/javascript" src="${salesTargetBlockJs}"></script>
</body>
</html>