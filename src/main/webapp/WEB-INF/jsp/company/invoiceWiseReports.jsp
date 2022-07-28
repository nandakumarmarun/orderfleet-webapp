<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Invoice Wise Reports</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
	
<style type="text/css">
.error {
	color: red;
}
</style>
<script type="text/javascript">
	var contextPath = "${pageContext.request.contextPath}";
</script>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Activities\Transactions</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12  clearfix">
					<form role="form" class="form-horizontal">
						<div class="form-group">
							<div class="col-sm-6">
								<div class="form-check">
									<input type="checkbox" class="form-check-input"
										id="inclSubOrdinates" checked="checked"> <label
										class="form-check-label" for="inclSubOrdinates">Include
										Subordinates</label>
								</div>
							</div>
						</div>
						<div class="form-group">
							<div class="col-sm-3">
								Employee
								<div class=" input-group">
									<span
										class='input-group-addon btn  dropdown-toggle glyphicon glyphicon-filter'
										data-toggle='dropdown' aria-haspopup='true'
										aria-expanded='false' title='filter employee'></span>
									<div class='dropdown-menu dropdown-menu-left'
										style='background-color: #F0F0F0'>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetDashboardEmployees(this,"Dashboard Employee","All Dashboard Employee")'>Dashboard
												Employee</a>
										</div>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetAllEmployees(this,"no","All Employee")'>All
												Employee</a>
										</div>
										<!-- <div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetOtherEmployees(this,"no","Other Employee")'>Other
												Employees</a>
										</div> -->
									</div>
									<select id="dbEmployee" name="employeePid"
										class="form-control">
										<option value="Dashboard Employee">All Dashboard
											Employees</option>
									</select>
								</div>
							</div>
							<div class="col-sm-2">
								Document <select id="dbDocument" name="documentPid"
									class="form-control">
									<option value="no">All Document</option>
									<c:forEach items="${documents}" var="document">
										<option value="${document.pid}">${document.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								Account <select id="dbAccount" name="accountPid"
									class="form-control">
									<option value="no">All Account</option>
									<c:forEach items="${accounts}" var="account">
										<option value="${account.pid}">${account.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								Activity
								<div class=" input-group">
									<span
										class='input-group-addon btn  dropdown-toggle glyphicon glyphicon-filter'
										data-toggle='dropdown' aria-haspopup='true'
										aria-expanded='false' title='filter employee'></span>
									<div class='dropdown-menu dropdown-menu-left'
										style='background-color: #F0F0F0'>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='InvoiceWiseReport.getActivities("all")'>All
												Activity</a>
										</div>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='InvoiceWiseReport.getActivities("planed")'>Planned
												Activity</a>
										</div>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='InvoiceWiseReport.getActivities("unPlaned")'>UnPlanned
												Activity</a>
										</div>

									</div>
									<select id="dbActivity" name="employeePid"
										class="form-control">
										<option value="no">All Activity</option>
										<c:forEach items="${activities}" var="activity">
											<option value="${activity.pid}">${activity.name}</option>
										</c:forEach>
									</select>
								</div>
							</div>
							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="InvoiceWiseReport.showDatePicker()">
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
							<div class="input-group col-sm-2">
								<div class="col-sm-3">
									<br />
									<button type="button" class="btn btn-info entypo-search"
										style="font-size: 18px" onclick="InvoiceWiseReport.filter()"
										title="Apply"></button>
								</div>
								<!-- <div class="col-sm-3">
									<br />
									<button id="btnDownload" type="button" style="font-size: 18px"
										class="btn btn-orange entypo-download" title="Download Xls"></button>
								</div> -->
								<div class="col-sm-3">
									<br />
									<button id="btnTestdownload" type="button" 
										class="btn btn-success" >  Download Xls</button>
								</div>
							</div>
						</div>
					</form>

				</div>

				<div class="col-md-12 col-sm-12 clearfix"
					style="font-size: 14px; color: black;">
					<div class="col-sm-2">
						<label>Activities : </label> <label id="lblActivities">0</label>
					</div>
					<div class="col-sm-3">
						<label>Transaction Amount : </label> <label
							id="lblTransactionAmount">0</label>
					</div>
					<div class="col-sm-3">
						<label>Transaction Volume : </label> <label
							id="lblTransactionVolume">0</label>
					</div>
					<div class="col-sm-3">
						<label>Total Payments : </label> <label id="lblTotalPayments">0</label>
					</div>
				</div>
			</div>
			<div class="table-responsive">
				<table class="collaptable table  table-striped table-bordered"
					id="tblInvoiceWiseReport">
					<!--table header-->
					<thead>
						<tr>
							<th style="min-width: 80px;">Employee</th>
							<th>Account Profile</th>
							<th>Activity</th>
							<th>Punch In</th>
							<th>Client Date</th>
							<th>Time Spend (hh:mm:ss:mmm)</th>
							<th>Server Date</th>
							<th>GPS Location</th>
							<th>Enable/ Disable</th>
							<th>Tower Location</th>
							<th>Visit Type</th>
							<th>Sales/ Sales Order Amount Total</th>
							<th>Receipt Amount Total</th>
							<th>Vehicle Registration Number</th>
							<th>Remarks</th>
						</tr>
					</thead>
					<!--table header-->
					<tbody id="tBodyInvoiceWiseReport">
					</tbody>
				</table>
			</div>


			<hr />

			<div class="modal fade container" id="myModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Add Reason for
								Rejecting</h4>
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
									<label class="control-label" for="field_name">Reason</label>
									<textarea rows="4" cols="50" class="form-control" name="reason"
										id="field_reason" placeholder="Reason"></textarea>
								</div>
								<div>
									<label id="reasonAlert" style="display: none; color: red;">Please
										Enter Reason</label>
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
			<div class="modal fade container" id="accountProfileModal">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 65%;">
					<div class="modal-content" id="oldAccounts" style="display: block;">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Change Account
								Profile</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px;">
							<div class="form-group">
								<div id="divAccounts">
									<div class="row">
										<div class="col-md-12 col-sm-12 clearfix">
											<button type="button" class="btn btn-success"
												id="newAccountProfile" style="float: left;">New
												Account</button>

											<button type="button" class="btn btn-info" id="btnSearch"
												style="float: right;">Search</button>
											<input type="text" id="search" placeholder="Search..."
												class="form-control" style="width: 200px; float: right;">
										</div>
									</div>
									<br /> <label class="error-msg" style="color: red;"></label>
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th>Select</th>
												<th>Account Profile</th>
											</tr>
										</thead>
										<tbody id="tbodyAccountProfile">
											<c:forEach items="${accounts}" var="account">
												<tr>
													<td><input name='account' type="radio"
														value="${account.pid}" /></td>
													<td>${account.name}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>

						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
							<button id="changeAccount" class="btn btn-primary">Save</button>
						</div>
					</div>

					<div class="modal-content" id="newAccount" style="display: none;">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Create Account
								Profile</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px;">
							<div class="form-group">
								<div id="divAccounts">
									<div class="row">
										<div class="col-md-12 col-sm-12 clearfix">
											<div class="col-md-2">
												<button type="button" class="btn btn-default" id="return"
													title="Back" style="">
													<i class="entypo-left-open "></i>
												</button>
											</div>
											<div class="col-md-4">
												<select id="field_dynamicDocument" name="dynamicDocumentPid"
													class="form-control"><option value="-1">Select
														dynamic document</option>
												</select>
											</div>
											<div class="col-md-4">
												<select id="field_form" name="formPid" class="form-control"><option
														value="-1">select form</option>
												</select>
											</div>
											<div class="col-md-2">
												<button type="button" class="btn btn-success"
													id="loadAccountProfile" title="apply">Apply</button>
											</div>
										</div>
									</div>
									<br />
									<div class="alert alert-danger alert-dismissible" role="alert"
										style="display: none;">
										<button type="button" class="close"
											onclick="$('.alert').hide();" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
										<p></p>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_name">Name</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="name" id="field_name" maxlength="255"
											placeholder="Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_alias">Alias</label> <input
											type="text" class="form-control" name="alias"
											id="field_alias" maxlength="55" placeholder="Alias" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_accountType">Account
											Type</label> <select id="field_accountType" name="accountTypePid"
											class="form-control">
											<c:forEach items="${accountTypes}" var="accountType">
												<option value="${accountType.pid}">${accountType.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_priceLevel">Default
											Price Level</label> <select id="field_priceLevel"
											name="defaultPriceLevelPid" class="form-control"><option
												value="-1">Select Price Level</option>
											<c:forEach items="${priceLevels}" var="priceLevel">
												<option value="${priceLevel.pid}">${priceLevel.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_city">City</label> <input
											type="text" class="form-control" name="city" id="field_city"
											maxlength="100" placeholder="City" value="City" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_location">Location</label>
										<input type="text" class="form-control" name="location"
											id="field_location" maxlength="100" placeholder="Location" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_pin">PIN</label> <input
											type="number" class="form-control" name="pin" id="field_pin"
											maxlength="15" placeholder="Pin Number" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_phone1">Phone
											1</label> <input type="text" class="form-control" name="phone1"
											id="field_phone1" maxlength="20" placeholder="Phone 1" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_phone2">Phone
											2</label> <input type="text" class="form-control" name="phone2"
											id="field_phone2" maxlength="20" placeholder="Phone 2" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_email1">Email
											1</label> <input type="email" class="form-control" name="email1"
											id="field_email1" maxlength="100" placeholder="Email 1" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_email2">Email
											2</label> <input type="email" class="form-control" name="email2"
											id="field_email2" maxlength="100" placeholder="Email 2" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_address">Address</label>
										<textarea class="form-control" name="address" maxlength="250"
											id="field_address" placeholder="Address"></textarea>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_description">Description</label>
										<textarea class="form-control" name="description"
											maxlength="250" id="field_description"
											placeholder="Description"></textarea>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_creditDays">Credit
											Days</label> <input type="text" class="form-control"
											name="creditDays" id="field_creditDays" maxlength="5"
											placeholder="Credit Days" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_closingBalance">Closing
											Balance</label> <input type="number" step="0.01" class="form-control"
											name="closingBalance" id="field_closingBalance" maxlength="8"
											placeholder="closing balance" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_creditLimit">Credit
											Limits</label> <input type="text" class="form-control"
											name="creditLimit" id="field_creditLimit" maxlength="8"
											placeholder="Credit Limits" />
									</div>
									<div class="form-group">
										<label class="control-label"
											for="field_defaultDiscountPercentage">Default
											Discount Percentage</label> <input type="number" step="0.01"
											class="form-control" name="defaultDiscountPercentage"
											id="field_defaultDiscountPercentage" maxlength="8"
											placeholder="Default Discount Percentage" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_contactPerson">Contact
											Person</label>
										<textarea class="form-control" name="contactPerson"
											maxlength="250" id="field_contactPerson"
											placeholder="Contact Person"></textarea>
									</div>

									<div class="form-group">
										<label class="control-label" for="field_territory">Territory</label>
										<select id="field_territory" name="territoryPid"
											class="form-control">
											<c:forEach items="${territories}" var="territory">
												<option value="${territory.pid}">${territory.name}</option>
											</c:forEach>
										</select>
									</div>

								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
							<button id="createAndChangeAccount" class="btn btn-primary">Save</button>
						</div>
					</div>

					<!-- /.modal-content -->
				</div>

				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="imagesModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Images</h4>
						</div>
						<div class="modal-body">
							<!-- error message -->
							<div class="alert alert-danger alert-dismissible" role="alert"
								style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>
							<div id="divInvoiceWiseReportImages"
								style="overflow: auto; height: 500px;"></div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>


			<!-- Model Container Inventory Voucher-->
			<div class="modal fade container" id="viewModalInventoryVoucher">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Inventory
								Voucher Details</h4>
						</div>
						<div class="modal-body">
							<!-- error message -->
							<div class="alert alert-danger alert-dismissible" role="alert"
								style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>
							<table class="table  table-striped table-bordered">
								<tr>
									<td>Document Number</td>
									<td id="lbl_documentNumberIc"></td>
								</tr>
								<tr>
									<td>User</td>
									<td id="lbl_userIc"></td>
								</tr>
								<tr>
									<td>Document</td>
									<td id="lbl_documentIc"></td>
								</tr>
								<tr>
									<td>Date</td>
									<td id="lbl_documentDateIc"></td>
								</tr>
								<tr>
									<td>Receiver</td>
									<td id="lbl_receiverIc"></td>
								</tr>
								<tr>
									<td>Supplier</td>
									<td id="lbl_supplierIc"></td>
								</tr>
								<tr>
									<td>Document Total</td>
									<td id="lbl_documentTotalIc"></td>
								</tr>
								<tr>
									<td>Document Volume</td>
									<td id="lbl_documentVolumIc"></td>
								</tr>
								<tr>
									<td>Document Discount Amount</td>
									<td id="lbl_documentDiscountAmountIc"></td>
								</tr>
								<tr>
									<td>Document Discount Percentage</td>
									<td id="lbl_documentDiscountPercentageIc"></td>
								</tr>
							</table>
							<div class="table-responsive" id="divVoucherDetailsInventory">
								<table class="collaptable table table-striped table-bordered">
									<thead>
										<tr>
											<th>Product</th>
											<th>Quantity</th>
											<th>Free Quantity</th>
											<th>Selling Rate</th>
											<th>Tax %</th>
											<th>Discount Amount</th>
											<th>Discount %</th>
											<th>Remark</th>
											<th>Total</th>
										</tr>
									</thead>
									<tbody id="tblVoucherDetailsIc">

									</tbody>
								</table>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>


			<!-- Model Container AccountinVoucher-->
			<div class="modal fade container" id="viewModalAccountingVoucher">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Accounting
								Voucher Details</h4>
						</div>
						<div class="modal-body">
							<!-- error message -->
							<div class="alert alert-danger alert-dismissible" role="alert"
								style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>
							<table class="table  table-striped table-bordered">
								<tr>
									<td>Document Number</td>
									<td id="lbl_documentNumberAc"></td>
								</tr>
								<tr>
									<td>User</td>
									<td id="lbl_userAc"></td>
								</tr>
								<tr>
									<td>Account</td>
									<td id="lbl_accountAc"></td>
								</tr>
								<tr>
									<td>Document</td>
									<td id="lbl_documentAc"></td>
								</tr>
								<tr>
									<td>Date</td>
									<td id="lbl_createdDateAc"></td>
								</tr>
								<tr>
									<td>Total Amount</td>
									<td id="lbl_totalAmountAc"></td>
								</tr>
								<tr>
									<td>Outstanding Amount</td>
									<td id="lbl_outstandingAmountAc"></td>
								</tr>
								<tr>
									<td>Remarks</td>
									<td id="lbl_remarksAc"></td>
								</tr>
							</table>
							<div class="table-responsive" id="divVoucherDetailsAc">
								<table class="collaptable table  table-striped table-bordered">
									<thead>
										<tr>
											<th>Amount</th>
											<th>Mode</th>
											<th>Instrument Number</th>
											<th>Instrument Date</th>
											<th>Bank Name</th>
											<th>By Account</th>
											<th>To Account</th>
											<th>Income Expense Name</th>
											<th>Voucher Number</th>
											<th>Voucher Date</th>
											<th>Reference Number</th>
											<th>Provisional Receipt Number</th>
											<th>Remarks</th>
										</tr>
									</thead>
									<tbody id="tblVoucherDetailsAc">

									</tbody>
								</table>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>



			<!-- Model Container Dynamic Document-->
			<div class="modal fade container" id="viewModalDynamicDocument">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Dynamic Document
								Details</h4>
						</div>
						<div class="modal-body">
							<!-- error message -->
							<div class="alert alert-danger alert-dismissible" role="alert"
								style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>
							<table class="table  table-striped table-bordered">
								<tr>
									<td>Document Number</td>
									<td id="lbl_documentNumber"></td>
								</tr>
								<tr>
									<td>User</td>
									<td id="lbl_user"></td>
								</tr>
								<tr>
									<td>Account</td>
									<td id="lbl_account"></td>
								</tr>
								<tr>
									<td>Account Ph:</td>
									<td id="lbl_accountph"></td>
								</tr>
								<tr>
									<td>Activity</td>
									<td id="lbl_activity"></td>
								</tr>
								<tr>
									<td>Document</td>
									<td id="lbl_document"></td>
								</tr>
								<tr>
									<td>Date</td>
									<td id="lbl_documentDate"></td>
								</tr>
							</table>
							<div id="divDynamicDocumentDetails"
								style="overflow: auto; height: 300px;"></div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>


			<div class="modal fade container" id="viewGeoTagingModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Geo Location Taging</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">

							<input type="hidden" id="hdn_lbl">

							<table class='table table-striped table-bordered'>
								<thead>
									<tr>
										<th>old latitude</th>
										<th>old longitude</th>
										<th>old geo location</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td id="oldLatitude"></td>
										<td id="oldLongitude"></td>
										<td id="oldGeoLocation"></td>
									</tr>
								</tbody>
							</table>

							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th>new latitude</th>
												<th>new longitude</th>
												<th>new geo location</th>
											</tr>
										</thead>
										<tbody id="tblAccountProfileGeoLocation">
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveNewGeoLocation" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/executive-task-executions"
				var="urlInvoiceWiseReports"></spring:url>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<!-- tableExport.jquery.plugin -->
	<spring:url value="/resources/assets/js/tableexport/xlsx.core.min.js"
		var="jsXlsx"></spring:url>
	<spring:url value="/resources/assets/js/tableexport/FileSaver.min.js"
		var="fileSaver"></spring:url>
<%-- 	<spring:url value="/resources/assets/js/tableexport/tableexport.min.js" --%>
<%-- 		var="tableExport"></spring:url> --%>
	<script type="text/javascript" src="${jsXlsx}"></script>
	<script type="text/javascript" src="${fileSaver}"></script>
<%-- 	<script type="text/javascript" src="${tableExport}"></script> --%>
	
	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

	<spring:url value="/resources/app/invoice-wise-report.js"
		var="invoiceWiseRepoetJs"></spring:url>
	<script type="text/javascript" src="${invoiceWiseRepoetJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>
	<script type="text/javascript">
		var interimSave = "${interimSave}";
		var distanceTarvel = "${distanceTarvel}";
		// call from dash board
		$(document)
				.ready(
						function() {

							var employeePid = getParameterByName('user-key-pid');
							getEmployees(employeePid);
							var documentType = getParameterByName('document-name');
							if (documentType != null) {
								$(
										"#dbDocument option:contains('"
												+ documentType + "')").attr(
										'selected', 'selected');
							}
							$("#txtToDate").datepicker({
								dateFormat : "dd-mm-yy"
							});
							$("#txtFromDate").datepicker({
								dateFormat : "dd-mm-yy"
							});

							$('#myFormSubmit').on('click', function() {
								InvoiceWiseReport.reject();
							});
							$('#btnSaveNewGeoLocation').on('click', function() {
								InvoiceWiseReport.saveNewGeoLocation();
							});

							$('#btnSearch').click(
									function() {
										InvoiceWiseReport.searchTable($(
												"#search").val(),
												$('#tbodyAccountProfile'));
									});

							$('#newAccountProfile').click(function() {
								InvoiceWiseReport.newAccount();
							});

							$('#changeAccount').click(function() {
								InvoiceWiseReport.changeAccount();
							});
							$('#return').click(function() {
								InvoiceWiseReport.oldAccount();
							});

							$('#field_dynamicDocument').change(function() {
								InvoiceWiseReport.getForms();
							});

							$('#createAndChangeAccount').click(function() {
								InvoiceWiseReport.createAndChangeAccount();
							});

							$('#loadAccountProfile').click(function() {
								InvoiceWiseReport.loadAccountFromForm();
							});

							$('#btnDownload')
									.on(
											'click',
											function() {
												var tblInvoiceWiseReport = $("#tblInvoiceWiseReport tbody");
												if (tblInvoiceWiseReport
														.children().length == 0) {
													alert("no values available");
													return;
												}
												if (tblInvoiceWiseReport[0].textContent == "No data available") {
													alert("no values available");
													return;
												}
												InvoiceWiseReport.downloadXls();
											});
							$('#btnTestdownload')
							.on(
									'click',
									function() {
										var tblInvoiceWiseReport = $("#tblInvoiceWiseReport tbody");
										if (tblInvoiceWiseReport
												.children().length == 0) {
											alert("no values available");
											return;
										}
										if (tblInvoiceWiseReport[0].textContent == "No data available") {
											alert("no values available");
											return;
										}
										InvoiceWiseReport.testdownloadXls();
									});

							InvoiceWiseReport.filter();
							//if(documentType == null){
							//execute on normal page load

							//}
						});
	</script>

</body>
</html>