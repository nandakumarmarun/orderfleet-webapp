<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | EditedAccountProfile</title>
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
			<h2>Edited Account Profile</h2>
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
						onchange="EditedAccountProfile.showDatePicker()">
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
						onclick="EditedAccountProfile.filter()">Apply</button>
				</div>
				<br>
				<div class="col-sm-4 ">
					<div class="input-group">
						<input type="text" class="form-control" placeholder="Search...."
							id="search" style="height: 35px" /> <span
							class="input-group-addon btn btn-default" title="Search"
							id="btnSearch"> <i class=" entypo-search"
							style="font-size: 20px"> </i>
						</span>
					</div>
				</div>
			</div>
			<br>


			<div class="clearfix"></div>
			<hr />
			<div class="table-responsive">
				<table class="table  table-striped table-bordered"
					id="tblEditedAccountProfile">
					<thead>
						<tr>
							<th>Name</th>
							<th>Edited By</th>
							<th>Edited On</th>
							<th>Edited From</th>
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
						</tr>
					</thead>
					<tbody id="tBodyEditedAccountProfile">

					</tbody>
				</table>
			</div>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/edit-account-profile"
				var="urlEditAccountProfile"></spring:url>


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
							<h4 class="modal-title" id="myModalLabel">View
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
							<dl class="dl-horizontal">
								<dt>
									<span>Name</span>
								</dt>
								<dd>
									<span id="field_name"></span>
								</dd><hr/>
								<dt>
									<span>Alias</span>
								</dt>
								<dd>
									<span id="field_alias"></span>
								</dd><hr/>
								<dt>
									<span>Account Type</span>
								</dt>
								<dd>
									<span id="field_accountType"></span>
								</dd><hr/>
								<dt>
									<span>Price Level</span>
								</dt>
								<dd>
									<span id="field_priceLevel"></span>
								</dd><hr/>
								<dt>
									<span>City</span>
								</dt>
								<dd>
									<span id="field_city"></span>
								</dd><hr/>
								<dt>
									<span>Location</span>
								</dt>
								<dd>
									<span id="field_location"></span>
								</dd><hr/>
								<dt>
									<span>Pin</span>
								</dt>
								<dd>
									<span id="field_pin"></span>
								</dd><hr/>
								<dt>
									<span>Phone 1</span>
								</dt>
								<dd>
									<span id="field_phone1"></span>
								</dd><hr/>
								<dt>
									<span>Phone 2</span>
								</dt>
								<dd>
									<span id="field_phone2"></span>
								</dd><hr/>
								<dt>
									<span>Email 1</span>
								</dt>
								<dd>
									<span id="field_email1"></span>
								</dd><hr/>
								<dt>
									<span>Email 2</span>
								</dt>
								<dd>
									<span id="field_email2"></span>
								</dd><hr/>
								<dt>
									<span>WhatsApp No</span>
								</dt>
								<dd>
									<span id="field_whatsAppNo"></span>
								</dd><hr/>
								<dt>
									<span>Address</span>
								</dt>
								<dd>
									<span id="field_address"></span>
								</dd><hr/>
								<dt>
									<span>Description</span>
								</dt>
								<dd>
									<span id="field_description"></span>
								</dd><hr/>
								<dt>
									<span>Credit Days</span>
								</dt>
								<dd>
									<span id="field_creditDays"></span>
								</dd><hr/>
								<dt>
									<span>Credit Limit</span>
								</dt>
								<dd>
									<span id="field_creditLimit"></span>
								</dd><hr/>
								<dt>
									<span>Default Discount %</span>
								</dt>
								<dd>
									<span id="field_defaultDiscountPercentage"></span>
								</dd><hr/>
								<dt>
									<span>Contact Person</span>
								</dt>
								<dd>
									<span id="field_contactPerson"></span>
								</dd>><hr/>
							</dl>

						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- /.Model Container-->


		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/app/edit-account-profile.js"
		var="editAccountProfileJs"></spring:url>
	<script type="text/javascript" src="${editAccountProfileJs}"></script>
</body>
</html>