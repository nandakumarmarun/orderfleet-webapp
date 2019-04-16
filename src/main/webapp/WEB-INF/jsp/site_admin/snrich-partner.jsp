<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Partners</title>
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
			<h2>Partner</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="Partner.showModalPopup($('#myModal'));"
						title="Create new">
						<i class="entypo-plus-circled"></i> Create
					</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Address 1</th>
						<th>Address 2</th>
						<th>E-Mail</th>
						<th>Phone</th>
						<th>Country</th>
						<th>State</th>
						<th>District</th>
						<th>Location</th>
						<th>Created Date</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:if test = "${empty partners}">
						<tr><td colspan = 12 style = "text-align: center;">No Data Available</td></tr>
					</c:if>
					<c:forEach items="${partners}" var="partner" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td><span style="cursor: pointer; color: #0072bc" title="click to view"
								onclick="Partner.showModalPopup($('#viewModal'),'${partner.pid}',0);">${partner.name== null ? "" : partner.name}</span></td>
							<td>${partner.address1== null ? "" : partner.address1}</td>
							<td>${partner.address2== null ? "" : partner.address2}</td>
							<td>${partner.email == null ? "" : partner.email}</td>
							<td>${partner.phone == null ? "" : partner.phone}</td>
							<td>${partner.country == null ? "" : partner.country}</td>
							<td>${partner.state == null ? "" : partner.state}</td>
							<td>${partner.district == null ? "" : partner.district}</td>
							<td>${partner.location == null ? "" : partner.location}</td>
							<td>${partner.createdDate == null ? "" : partner.createdDate}</td>
							<td><span
								class="label ${partner.activated? 'label-success':'label-danger' }"
								onclick="Partner.setActive('${partner.pid}','${ !partner.activated}')"
								style="cursor: pointer;">${partner.activated? "Activated" : "Deactivated"}</span></td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="Partner.showModalPopup($('#myModal'),'${partner.pid}',1);" title="Edit"><i class="entypo-pencil"></i></button>

							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/snrich-partners" var="urlSnrichPartner"></spring:url>

			<form id="partnerForm" role="form" method="post"
				action="${urlSnrichPartner}">
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
									Partner</h4>
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
										<label class="control-label" for="field_name">Name
										</label> <input autofocus="autofocus" type="text"
											class="form-control" name="name" id="field_name"
											maxlength="255" placeholder="Name" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_address1">Address 1
										</label>
										<textarea class="form-control" name="address1" maxlength="500"
											id="field_address1" placeholder="Address 1"></textarea>
									</div>

									<div class="form-group">
										<label class="control-label" for="field_address2">Address 2
										</label>
										<textarea class="form-control" name="address2" maxlength="500"
											id="field_address2" placeholder="Address 2"></textarea>
									</div>

									<div class="form-group">
										<label class="control-label" for="field_country">Country</label>
										<select id="field_country" name="countryCode"
											class="form-control" onchange="Partner.onChangeCountry();"><option
												value="-1">Select Country</option>
											<c:forEach items="${countries}" var="country">
												<option value="${country.code}">${country.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_state">State</label> <select
											id="field_state" name="stateCode" class="form-control"
											onchange="Partner.onChangeState();"><option
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
										<label class="control-label" for="field_phone">Phone</label> <input
											type="text" class="form-control" name="phone"
											id="field_phone" maxlength="100" placeholder="Phone Number" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_location">Location</label>
										<input type="text" class="form-control" name="location"
											id="field_location" placeholder="Location" />
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
								<h4 class="modal-title" id="viewModalLabel">Partner</h4>
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

									<table class="table table-striped table-bordered ">
										<tr>
											<td>Name</td>
											<td><span id="lbl_name"></span></td>
										</tr>
										<tr>
											<td>Address 1</td>
											<td><span id="lbl_address1"></span></td>
										</tr>
										<tr>
											<td>Address 2</td>
											<td><span id="lbl_address2"></span></td>
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
											<td>Phone</td>
											<td><span id="lbl_phone"></span></td>
										</tr>
										<tr>
											<td>location</td>
											<td><span id="lbl_location"></span></td>
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

			<form id="deleteForm" name="deleteForm" action="${urlPartner}">
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
									<p>Are you sure you want to delete this Partner?</p>
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

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/snrich-partner.js" var="snrichPartnerJs"></spring:url>
	<script type="text/javascript" src="${snrichPartnerJs}"></script>
</body>
</html>