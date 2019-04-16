<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Purchase History Configs</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Purchase History Configs</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="PurchaseHistoryConfig.showModalPopup($('#myModal'));">Create
						new Config</button>
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
						<th>Description</th>
						<th>Sort Order</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${pagePurchaseHistoryConfigs}"
						var="purchaseHistoryConfig" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${purchaseHistoryConfig.name}</td>
							<td>${purchaseHistoryConfig.startMonthName}</td>
							<td>${purchaseHistoryConfig.endMonthName}</td>
							<td>${purchaseHistoryConfig.description}</td>
							<td>${purchaseHistoryConfig.sortOrder}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="PurchaseHistoryConfig.showModalPopup($('#myModal'),'${purchaseHistoryConfig.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="PurchaseHistoryConfig.showModalPopup($('#deleteModal'),'${purchaseHistoryConfig.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/purchaseHistoryConfigs"
				var="urlPurchaseHistoryConfig"></spring:url>

			<form id="purchaseHistoryConfigForm" role="form" method="post"
				action="${urlPurchaseHistoryConfig}">
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
									Purchase History Config</h4>
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
												onchange="PurchaseHistoryConfig.onchangeStartMonth()">
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
												onchange="PurchaseHistoryConfig.onchangeEndMonth()">
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

									<div class="form-group">
										<label class="control-label" for="field_sortOrder">Sort
											Order</label> <input type="number" class="form-control"
											name="sortOrder" id="field_sortOrder" min="0" max="500"
											value="0" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_description">Description</label>
										<textarea class="form-control" name="description"
											id="field_description" placeholder="Description"></textarea>
									</div>
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
								<h4 class="modal-title" id="viewModalLabel">Purchase
									History Config</h4>
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
										<dt>
											<span>Sort Order</span>
										</dt>
										<dd>
											<span id="lbl_sortOrder"></span>
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
				action="${urlPurchaseHistoryConfig}">
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
									<p>Are you sure you want to delete this Purchase History
										Config?</p>
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



			<div class="modal fade container" id="accountsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Accounts</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
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
												<th>Account</th>
											</tr>
										</thead>
										<tbody id="tblAccounts">
											<c:forEach items="${accounts}" var="account">
												<tr>
													<td><input name='account' type='checkbox'
														value="${account.pid}" /></td>
													<td>${account.name}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveAccounts"
								value="Save" />
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

	<spring:url value="/resources/app/purchase-history-config.js"
		var="purchaseHistoryConfigJs"></spring:url>
	<script type="text/javascript" src="${purchaseHistoryConfigJs}"></script>
</body>
</html>