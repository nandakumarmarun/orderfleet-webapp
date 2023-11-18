<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | VerifyAccountProfile</title>
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
			<h2>Verify Account Profile</h2>
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
						onchange="VerifyAccountProfile.showDatePicker()">
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
						onclick="VerifyAccountProfile.filter()">Apply</button>
				</div>
			</div>
			<br>

			<div class="row">
				<div class="col-sm-4">
					<button type="button" class="btn btn-success"
						onclick="VerifyAccountProfile.ImportedStatus();">Verify</button>
					<button type="button" class="btn"
						onclick="VerifyAccountProfile.Deactivated();">Delete</button>
					<button id="btnDownload" type="button" class="btn btn-success">Download
						Xls</button>
				</div>


				<div class="col-sm-4">
					<input name='includeInactive' id="includeInactive" type='checkbox'
						value='no' onchange="VerifyAccountProfile.filter();" /> Include
					Inactive
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
					id="tblVerifyAccountProfile">
					<thead>
						<tr>
							<th><input type="checkbox" class="allcheckbox"> All
								Select</th>
							<th>Created By</th>
							<th>Created By(Emp)</th>
							<th>Name</th>
							<th>Alias</th>
							<th>Address</th>
							<th>Type</th>
							<th>Phone</th>
							<th>Email</th>
							<th>City</th>
							<th>Location</th>
							<th>Date</th>
							<th>GST Number</th>
							<th>Activated Status</th>
							<th>Actions</th>
							<c:forEach items="${attributes}" var="attribute">
                            <th> ${attribute.question}</th>
                            </c:forEach>

						</tr>
					</thead>
					<tbody id="tBodyVerifyAccountProfile">

					</tbody>
				</table>
			</div>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
			<spring:url value="/web/verify-account-profile" var="urlVerifyAccountProfile"></spring:url>

			<form id="accountProfileForm" role="form" method="post"
				action="${urlVerifyAccountProfile}">
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
									Account Profile</h4>
							</div>
							<div class="modal-body" style="height: 500px; overflow: auto;">
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
										name="name" id="field_name" maxlength="255" placeholder="Name" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_alias">Alias</label> <input
										type="text" class="form-control" name="alias" id="field_alias"
										maxlength="55" placeholder="Alias" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_accountType">Account
										Type</label> <select id="field_accountType" name="accountTypePid"
										class="form-control"><!-- <option value="-1">Select
											Account Type</option> -->
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
										maxlength="100" placeholder="City" />
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
									<label class="control-label" for="field_phone1">Phone 1</label>
									<input type="text" class="form-control" name="phone1"
										id="field_phone1" maxlength="20" placeholder="Phone 1" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_phone2">Phone 2</label>
									<input type="text" class="form-control" name="phone2"
										id="field_phone2" maxlength="20" placeholder="Phone 2" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_email1">Email 1</label>
									<input type="email" class="form-control" name="email1"
										id="field_email1" maxlength="100" placeholder="Email 1" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_email2">Email 2</label>
									<input type="email" class="form-control" name="email2"
										id="field_email2" maxlength="100" placeholder="Email 2" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_whatsAppNo">WhatsApp No</label>
									<input class="form-control" name="whatsAppNo"
										id="field_whatsAppNo" maxlength="100" placeholder="WhatsApp No" />
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
									<label class="control-label" for="field_creditDays">Creditt
										Days</label> <input type="text" class="form-control" name="creditDays"
										id="field_creditDays" maxlength="5" placeholder="Credit Days" />
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
										for="field_defaultDiscountPercentage">Default Discount
										Percentage</label> <input type="number" step="0.01"
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
								onclick="VerifyAccountProfile.filterByAccountType(); $('#ofModalSearch').modal('hide');">Apply</button>
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

	<spring:url value="/resources/app/verify-account-profile.js"
		var="verifyAccountProfileJs"></spring:url>
	<script type="text/javascript" src="${verifyAccountProfileJs}"></script>
</body>
</html>