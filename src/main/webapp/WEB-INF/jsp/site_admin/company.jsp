<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Company</title>
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
			<h2>Company</h2>
			<br>
			<div class="row col-xs-12">

				<div class="col-sm-4">
					<div class="col-sm-8">
						<select id="field_mainIndustry" name="mainIndustry"
							onchange="Company.onChangeIndustry();" class="form-control"><option
								value="-1">Select Industry</option>
							<c:forEach items="${industries}" var="industry">
								<option value="${industry}">${industry}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="pull-left">
					<form class="form-inline">
						<div class="form-group">
							<div class="input-group">
								<input type="text" id="searchCompany" placeholder="Search..."
									class="form-control" style="width: 200px;"><span
									class="input-group-btn">
									<button type="button" class="btn btn-info"
										id="btnSearchCompany" style="float: right;">Search</button>
								</span>
							</div>
						</div>
					</form>
				</div>
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="Company.showModalPopup($('#myModal'));">Create
						new Company</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Legal Name</th>
						<th>Alias</th>
						<th>Company Type</th>
						<th>Industry</th>
						<th>Location</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tBodyCompany">
					<c:forEach items="${companies}" var="company"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<c:choose>
								<c:when test="${company.id != '-1'}">
									<td>${company.legalName}</td>
									<td>${company.alias}</td>
									<td>${company.companyType}</td>
									<td>${company.industry}</td>
									<td>${company.location}</td>
									<td><span
										class="label ${company.isActivated ? 'label-success' : 'label-danger'}"
										onClick="Company.changeStatusCompany('${company.pid}','${!company.isActivated}');"
										style="cursor: pointer;">${company.isActivated ? "Activated" : "Deactivated"}</span>
									</td>
									<td>
										<button type="button" class="btn btn-blue"
											onclick="Company.showModalPopup($('#viewModal'),'${company.pid}',0);">View</button>
										<button type="button" class="btn btn-blue"
											onclick="Company.showModalPopup($('#myModal'),'${company.pid}',1);">Edit</button>
									</td>
								</c:when>
							</c:choose>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/company" var="urlCompany"></spring:url>
			<form id="companyForm" role="form" method="post"
				action="${urlCompany}">
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
									Company</h4>
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
								<div class="modal-body" style="overflow: auto; height: 60%;">
									<div class="form-group">
										<label class="control-label" for="field_legalName">Legal
											Name</label> <input autofocus="autofocus" type="text"
											class="form-control" name="legalName" id="field_legalName"
											maxlength="255" placeholder="Legal Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_alias">Alias</label> <input
											type="text" class="form-control" name="alias"
											id="field_alias" maxlength="55" placeholder="Alias" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_companyType">Company
											Type</label> <select id="field_companyType" name="companyType"
											class="form-control">
											<option value="-1">Select Company Type</option>
											<option value="MANUFACTURER">MANUFACTURER</option>
											<option value="DISTRIBUTOR">DISTRIBUTOR</option>
											<option value="RETAILER">RETAILER</option>
											<option value="SUPERSTOCKIST">SUPERSTOCKIST</option>
											<option value="SERVICE_PROVIDER">SERVICE_PROVIDER</option>
											<option value="GENERAL">GENERAL</option>
										</select>
									</div>

									<div class="form-group">
										<label class="control-label" for="field_industry">Industry
										</label> <select id="field_industry" name="industry"
											class="form-control"><option value="-1">Select
												Industry</option>
											<c:forEach items="${industries}" var="industry">
												<option value="${industry}">${industry}</option>
											</c:forEach>
										</select>

									</div>

									<div class="form-group">
										<label class="control-label" for="field_phoneNo">Phone
											Number</label> <input type="text" class="form-control" name="phoneNo"
											id="field_phoneNo" maxlength="20" placeholder="Phone Number" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_gstNo">GST
											Number</label> <input type="text" class="form-control" name="gstNo"
											id="field_gstNo" maxlength="55" placeholder="GST Number" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_address1">Address
											1</label>
										<textarea class="form-control" name="address1" maxlength="500"
											id="field_address1" placeholder="Address 1"></textarea>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_address2">Address
											2</label>
										<textarea class="form-control" name="address2" maxlength="500"
											id="field_address2" placeholder="Address 2"></textarea>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_country">Country</label>
										<select id="field_country" name="countryCode"
											class="form-control" onchange="Company.onChangeCountry();"><option
												value="-1">Select Country</option>
											<c:forEach items="${countries}" var="country">
												<option value="${country.code}">${country.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_state">State</label> <select
											id="field_state" name="stateCodeCode" class="form-control"
											onchange="Company.onChangeState();"><option
												value="-1">Select State</option>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_district">District</label>
										<select id="field_district" name="districtCode"
											class="form-control"><option value="-1">Select
												District</option>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_email">Email</label> <input
											type="email" class="form-control" name="email"
											id="field_email" maxlength="100" placeholder="Email" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_pinCode">PinCode</label>
										<input type="text" class="form-control" name="pinCode"
											id="field_pinCode" placeholder="Pincode" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_location">Location</label>
										<input type="text" class="form-control" name="location"
											id="field_location" placeholder="Location" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_profile_image">Image
										</label> <input id="profileImage" type="file"
											class="btn btn-default btn-block"> <img
											id="previewImage" src="" style="max-height: 100px;"
											alt="Image preview..."> <br>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_website">WebSite</label>
										<input type="text" class="form-control" name="website"
											id="field_website" placeholder="WebSite" />
									</div>
									<div class="form-group">

										<label class="control-label">On Premise</label> <input
											id="field_on_premise" name='onPremise' type='checkbox'
											class="form-group" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_smsApiKey">SMS
											Api Key</label> <input type="text" class="form-control"
											name="smsApiKey" id="field_smsApiKey" maxlength="55"
											placeholder="SMS Api Key" />
									</div>
								</div>

							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button id="myFormSubmit" class="btn btn-primary">Save</button>
								</div>
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
								<h4 class="modal-title" id="viewModalLabel">Company</h4>
							</div>
							<div class="modal-body">
								<div class="modal-body" style="overflow: auto; height: 60%;">
									<!-- error message -->
									<div class="alert alert-danger alert-dismissible" role="alert"
										style="display: none;">
										<button type="button" class="close"
											onclick="$('.alert').hide();" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
										<p></p>
									</div>

									<table class="table table-striped table-bordered ">
										<tr>
											<td>LegalName</td>
											<td><span id="lbl_legalName"></span></td>
											<td rowspan="7" align="center"><img alt="Profile Image"
												id="profileImageView" src="" class="img-circle" width="80" /></td>
										</tr>
										<tr>
											<td>Alias</td>
											<td><span id="lbl_alias"></span></td>
										</tr>
										<tr>
											<td>CompanyType</td>
											<td><span id="lbl_companyType"></span></td>
										</tr>
										<tr>
											<td>Industry</td>
											<td><span id="lbl_industry"></span></td>
										</tr>
										<tr>
											<td>Phone Number</td>
											<td><span id="lbl_phoneNo"></span></td>
										</tr>
										<tr>
											<td>GST Number</td>
											<td><span id="lbl_gstNo"></span></td>
										</tr>
										<tr>
											<td>Address</td>
											<td><span id="lbl_address"></span></td>
										</tr>

										<tr>
											<td>Country Name</td>
											<td><span id="lbl_countryName"></span></td>
										</tr>
										<tr>
											<td>State Name</td>
											<td><span id="lbl_stateName"></span></td>
										</tr>
										<tr>
											<td>District Name</td>
											<td><span id="lbl_districtName"></span></td>
										</tr>
										<tr>
											<td>Email</td>
											<td><span id="lbl_email"></span></td>
										</tr>
										<tr>
											<td>PinCode</td>
											<td><span id="lbl_pincode"></span></td>
										</tr>
										<tr>
											<td>location</td>
											<td><span id="lbl_location"></span></td>
										</tr>
										<tr>
											<td>WebSite</td>
											<td><span id="lbl_website"></span></td>
										</tr>
										<tr>
											<td>SMS Api Key</td>
											<td><span id="lbl_smsApiKey"></span></td>
										</tr>
									</table>


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

			<%-- 			<form id="deleteForm" name="deleteForm" action="${urlCompany}"> --%>
			<!-- 				Model Container -->
			<!-- 				<div class="modal fade container" id="activateModel"> -->
			<!-- 					model Dialog -->
			<!-- 					<div class="modal-dialog"> -->
			<!-- 						<div class="modal-content"> -->
			<!-- 							<div class="modal-header"> -->
			<!-- 								<button type="button" class="close" data-dismiss="modal" -->
			<!-- 									aria-label="Close"> -->
			<!-- 									<span aria-hidden="true">&times;</span> -->
			<!-- 								</button> -->
			<!-- 								<h4 class="modal-title">Confirm delete operation</h4> -->
			<!-- 							</div> -->
			<!-- 							<div class="modal-body"> -->

			<!-- 								<div class="modal-body" style="overflow: auto;"> -->
			<!-- 									error message -->
			<!-- 									<div class="alert alert-danger alert-dismissible" role="alert" -->
			<!-- 										style="display: none;"> -->
			<!-- 										<button type="button" class="close" -->
			<!-- 											onclick="$('.alert').hide();" aria-label="Close"> -->
			<!-- 											<span aria-hidden="true">&times;</span> -->
			<!-- 										</button> -->
			<!-- 										<p></p> -->
			<!-- 									</div> -->
			<!-- 									<p>Are you sure you want to activate this Company?</p> -->
			<!-- 								</div> -->
			<!-- 							</div> -->
			<!-- 							<div class="modal-footer"> -->
			<!-- 								<div class="modal-footer"> -->
			<!-- 									<button type="button" class="btn btn-default" -->
			<!-- 										data-dismiss="modal">No</button> -->
			<!-- 									<button class="btn btn-success">Yes</button> -->
			<!-- 								</div> -->
			<!-- 							</div> -->
			<!-- 						</div> -->
			<!-- 						/.modal-content -->
			<!-- 					</div> -->
			<!-- 					/.modal-dialog -->
			<!-- 				</div> -->
			<!-- 				/.Model Container -->
			<!-- 			</form> -->

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/company.js" var="companyJs"></spring:url>
	<script type="text/javascript" src="${companyJs}"></script>
</body>
</html>