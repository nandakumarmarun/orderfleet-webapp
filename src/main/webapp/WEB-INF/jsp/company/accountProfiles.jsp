<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>


<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | AccountProfile</title>
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



			<h2>Account Profiles</h2>
			<hr />

			<div class="row">
				<div class="col-sm-4">
					<button type="button" class="btn btn-success"
						onclick="AccountProfile.showModalPopup($('#myModal'));"
						title="Create new">
						<i class="entypo-plus-circled"></i> Create
					</button>

					<button type="button" class="btn btn-orange" id="btnDownload"
						title="download xlsx">
						<i class="entypo-download"></i> download
					</button>

					<button type="button" class="btn btn-info"
						onclick="$(this).next('input').val('');$('#ofModalSearch').modal('show', {backdrop: 'static'});"
						title="filter">
						<i class="glyphicon glyphicon-filter"></i> filter
					</button>
				</div>

				<div class="col-sm-4">
					<div class="form-group col-sm-5">
						<select id="slt_status" class="form-control" title="sort">
							<option value="All">All</option>
							<option value="Active">Active</option>
							<option value="Deactive">Deactive</option>
							<option value="MultipleActivate">Multiple Activate</option>
						</select>
					</div>
				</div>

				<%--  <div class="col-sm-2">
								Counrty <select id="dbCountry" name="country"
									class="form-control">
									<option value="no">All</option>
									<c:forEach items="${countries}" var="country">
										<option value="${country.id}">${country.name}</option>
									</c:forEach>
								</select>
							</div>
							
							<div class="col-sm-2">
								States <select id="dbState" name="state"
									class="form-control">
									<option value="no">All</option>
									<c:forEach items="${states}" var="state">
										<option value="${state.id}">${state.name}</option>
									</c:forEach>
								</select>
							</div>
							
							<div class="col-sm-2">
								Districts <select id="dbDistrict" name="district"
									class="form-control">
									<option value="no">All</option>
									<c:forEach items="${districts}" var="district">
										<option value="${district.id}">${district.name}</option>
									</c:forEach>
								</select>
							</div>  --%>




				<div class="col-sm-4 ">
					<div class="input-group">
						<input type="text" class="form-control"
							placeholder="Search By Name..." id="search" style="height: 35px" />
						<span class="input-group-addon btn btn-default" title="Search"
							id="btnSearch"> <i class=" entypo-search"
							style="font-size: 20px"> </i>
						</span>
					</div>
				</div>




				<div class="col-sm-2">
					<button type="button" class="btn btn-primary"
						id="setLocationRadius">Set Default Location Radius</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table table-striped table-bordered of-tbl-search"
				id="tblAccountProfile">
				<thead>
					<tr>
						<th>Name</th>
						<th>Type</th>
						<th>Closing Balance</th>
						<th>Address</th>
						<th>Phone1</th>
						<th>Email1</th>
						<th>WhatsApp No</th>
						<th>Account Status</th>
						<th>GSTIN</th>
						<th>GST Registration Type</th>
						<th>Created Date</th>
						<th>Last Updated Date</th>
						<th>Created By</th>
						<th>Stage</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tBodyAccountProfile">
				</tbody>
			</table>
			<%-- <div class="row-fluid">
				<util:pagination thispage="${pageAccountProfile}"></util:pagination>
			</div> --%>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/accountProfiles" var="urlAccountProfile"></spring:url>

			<form id="accountProfileForm" role="form" method="post"
				action="${urlAccountProfile}">
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
									<label class="control-label" for="field_customerId">Customer
										Id</label> <input type="text" class="form-control" name="customerId"
										id="field_customerId" maxlength="55" placeholder="Customer Id" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_accountType">Account
										Type</label> <select id="field_accountType" name="accountTypePid"
										class="form-control">
										<!-- <option value="-1">Select
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
									<label class="control-label" for="dbCountrycreate">Counrty
									</label> <select id="dbCountrycreate" name="countryid"
										class="form-control"><option value="-1">Select
											Country</option>
										<c:forEach items="${countries}" var="country">
											<option value="${country.id}">${country.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="dbStatecreate">State
									</label> <select id="dbStatecreate" name="stateid" class="form-control"><option
											value="-1">Select State</option>
										<c:forEach items="${states}" var="state">
											<option value="${state.id}">${state.name}</option>
										</c:forEach>
									</select>
								</div>

								<div class="form-group">
									<label class="control-label" for="dbDistrictcreate">District
									</label> <select id="dbDistrictcreate" name="districtid"
										class="form-control"><option value="-1">Select
											District</option>
										<c:forEach items="${districts}" var="district">
											<option value="${district.id}">${district.name}</option>
										</c:forEach>
									</select>
								</div>

								<!-- Modern district state issue -->

								<!-- 								<div class="form-group"> -->
								<!-- 									<label class="control-label" for="dbCountrycreate">Counrty -->
								<!-- 									</label> <select id="dbCountrycreate" name="countryid" -->
								<!-- 										class="form-control"><option value="-1">Select -->
								<!-- 											Country</option> -->
								<%-- 										<c:forEach items="${countries}" var="country"> --%>
								<%-- 											<option value="${country.id}">${country.name}</option> --%>
								<%-- 										</c:forEach> --%>
								<!-- 									</select> -->
								<!-- 								</div> -->
								<!-- 								<div class="form-group"> -->
								<!-- 									<label class="control-label" for="dbStatecreate">State -->
								<!-- 									</label> <select id="dbStatecreate" name="stateid" class="form-control"><option -->
								<!-- 											value="-1">Select State</option> -->
								<%-- 										<c:forEach items="${states}" var="state"> --%>
								<%-- 											<option value="${state.id}">${state.name}</option> --%>
								<%-- 										</c:forEach> --%>
								<!-- 									</select> -->
								<!-- 								</div> -->

								<!-- 								<div class="form-group"> -->
								<!-- 									<label class="control-label" for="dbDistrictcreate">District -->
								<!-- 									</label> <select id="dbDistrictcreate" name="districtid" -->
								<!-- 										class="form-control"><option value="-1">Select -->
								<!-- 											District</option> -->
								<%-- 										<c:forEach items="${districts}" var="district"> --%>
								<%-- 											<option value="${district.id}">${district.name}</option> --%>
								<%-- 										</c:forEach> --%>
								<!-- 									</select> -->
								<!-- 								</div> -->
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
									<label class="control-label" for="field_whatsAppNo">WhatsApp
										No</label> <input class="form-control" name="whatsAppNo"
										id="field_whatsAppNo" maxlength="100"
										placeholder="WhatsApp No" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_address">Address</label>
									<textarea class="form-control" name="address" maxlength="250"
										id="field_address" placeholder="Address"></textarea>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_tinNo">GSTIN</label> <input
										type="text" class="form-control" name="tinNo" id="field_tinNo"
										maxlength="30" placeholder="GSTIN" />
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
								<div class="form-group">
									<label class="control-label" for="field_locationradius">Location
										Radius</label> <input type="number" class="form-control"
										name="locationRadius" id="fld_locationradius" maxlength="100"
										placeholder="Location Radius" />
								</div>
								<div>
									<label class="error-msg" style="color: red;"></label>
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

			<!-- <!-- Model Container-->
			<!-- <div class="modal fade container" id="locationRadiusModal">
				model Dialog
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Location Radius</h4>
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
							<div class="form-group">
								<label class="control-label" for="field_locationradius">Location
									Radius</label> <input autofocus="autofocus" type="number"
									class="form-control" name="name" id="field_locationradius"
									maxlength="255" placeholder="Location Radius" />
							</div>
							<div>
								<label class="error-msg" style="color: red;"></label>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button id="saveLocationRadius" class="btn btn-success">Save</button>
							</div>
						</div>
						/.modal-content
					</div>
					/.modal-dialog
				</div> -->
			-->
			<!--  Model Container -->


			<form name="locationRadius" role="form">
				<!-- Model Container-->
				<div class="modal fade container" id="locationRadiusModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="locationRadiusModalLabel">Locaton
									Radius</h4>
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
									<div class="form-group">
										<label class="control-label" for="field_locationradius">Location
											Radius</label> <input autofocus="autofocus" type="number"
											class="form-control" name="name" id="field_locationradius"
											maxlength="255" placeholder="Location Radius" />
									</div>

								</div>
							</div>
							<div>
								<label class="error-msg" style="color: red;"></label>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button id="saveLocationRadius" class="btn btn-success">Save</button>

							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->

			</form>

			<div class="modal fade container" id="assignLocationModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content" style="width: 120%">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Locations</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divLocations">
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
												<th><label><input type="checkbox"
														class="allcheckbox" value="">All</label></th>
												<th>Name</th>
											</tr>
										</thead>
										<tbody id="tBodyLocations">
											<c:forEach items="${locations}" var="location">
												<tr>
													<td><input name='location' type='checkbox'
														value="${location.pid}" style="display: block;" /></td>
													<td>${location.name}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveLocations" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>





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
								<h4 class="modal-title" id="viewModalLabel">Account Profile</h4>
							</div>
							<div class="modal-body" style="height: 500px; overflow: auto;">
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
									<table class="table  table-striped table-bordered"
										id="tbAccountProfile">
										<tr>
											<td>Name</td>
											<td id="lbl_name"></td>
										</tr>
										<tr>
											<td>Alias</td>
											<td id="lbl_alias"></td>
										</tr>
										<tr>
											<td>Customer Id</td>
											<td id="lbl_customerId"></td>
										</tr>
										<tr>
											<td>Type</td>
											<td id="lbl_accountType"></td>
										</tr>
										<tr>
											<td>Price Level</td>
											<td id="lbl_priceLevel"></td>
										</tr>
										<tr>
											<td>City</td>
											<td id="lbl_city"></td>
										</tr>
										<tr>
											<td>Location</td>
											<td id="lbl_location"></td>
										</tr>
										<tr>
                                       	<td>Address</td>
                                      	<td id="lbl_address"></td>
                                        </tr>
										<tr>
											<td>PIN</td>
											<td id="lbl_pin"></td>
										</tr>
										<tr>
											<td>Phone 1</td>
											<td id="lbl_phone1"></td>
										</tr>
										<tr>
											<td>Phone 2</td>
											<td id="lbl_phone2"></td>
										</tr>
										<tr>
											<td>Email 1</td>
											<td id="lbl_email1"></td>
										</tr>
										<tr>
											<td>Email 2</td>
											<td id="lbl_email2"></td>
										</tr>
										<tr>
											<td>WhatsApp No</td>
											<td id="lbl_whatsAppNo"></td>
										</tr>
										<tr>
											<td>Address</td>
											<td id="lbl_address"></td>
										</tr>
										<tr>
											<td>GSTIN</td>
											<td id="lbl_tinNo"></td>
										</tr>
										<tr>
											<td>Description</td>
											<td id="lbl_description"></td>
										</tr>
										<tr>
											<td>Credit Days</td>
											<td id="lbl_creditDays"></td>
										</tr>
										<tr>
											<td>Credit Limit</td>
											<td id="lbl_creditLimit"></td>
										</tr>
										<tr>
											<td>Default Discount Percentage</td>
											<td id="lbl_defaultDiscountPercentage"></td>
										</tr>
										<tr>
											<td>Closing Balance</td>
											<td id="lbl_closingBalance"></td>
										</tr>
										<tr>
											<td>Contact Person</td>
											<td id="lbl_contactPerson"></td>
										</tr>
										<tr>
											<td>Location Radius</td>
											<td id="lbl_locationRadius"></td>
										</tr>
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
				<!-- /.Model Container-->
			</form>

			<form id="deleteForm" name="deleteForm" action="${urlAccountProfile}">
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
									<p>Are you sure you want to delete this Account Profile ?</p>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button class="btn btn-danger">Delete</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

			<div class="modal fade container" id="enableAccountProfileModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable Account Profile</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>Account Profile</th>
											</tr>
										</thead>
										<tbody id="tblEnableAccountProfile">
											<c:forEach items="${deactivatedAccountProfiles}"
												var="accountprofile">
												<tr>
													<td><input name='accountprofile' type='checkbox'
														value="${accountprofile.pid}" /></td>
													<td>${accountprofile.name}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnActivateAccountProfile" value="Activate" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

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
										<div class="form-group">
											<label class="control-label">Import Status</label>
											<div class="row">
												<div class="col-md-4">
													<label> <input type="radio" name="import"
														value="all" checked="checked"> All
													</label>
												</div>
												<div class="col-md-4">
													<label> <input type="radio" name="import"
														value="true"> Imported
													</label>
												</div>
												<div class="col-md-4">
													<label> <input type="radio" name="import"
														value="false"> Others
													</label>
												</div>
											</div>
										</div>
										<hr>
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
								onclick="AccountProfile.filterByAccountType(); $('#ofModalSearch').modal('hide');">Apply</button>
						</div>
					</div>
				</div>
			</div>
			<!-- OF Modal Filter end -->

			<form name="location" role="form">
				<!-- Model Container-->
				<div class="modal fade container" id="locationModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="viewModalLabel">Locations</h4>
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
									<table class="table  table-striped table-bordered">
										<thead>
											<tr>
												<th>Name</th>
											</tr>
										</thead>
										<tbody id="tbllocation">

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
				<!-- /.Model Container-->
			</form>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/app/account-profile.js"
		var="accountProfileJs"></spring:url>
	<script type="text/javascript" src="${accountProfileJs}"></script>
</body>
</html>