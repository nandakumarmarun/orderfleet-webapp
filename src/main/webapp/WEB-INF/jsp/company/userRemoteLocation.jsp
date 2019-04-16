<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Remote Location</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>User Remote Location</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="UserRemoteLocation.showModalPopup($('#myModal'),'',1);">Create
						new User Remote Location</button>
				</div>

				<div class="form-group col-sm-3">
					<select id="dbCompany" class="form-control"><option
							value="no">Filter By Company</option>
						<c:forEach items="${companyNames}" var="company">
							<option value="${company}">${company}</option>
						</c:forEach>
					</select>
				</div>

			</div>
			<div class="clearfix"></div>
			<hr />
			<table id="tbl_data" class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Company</th>
						<th>Login</th>
						<th>Expire Date</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${userRemoteLocations}" var="userRemoteLocation"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'} content">
							<td>${userRemoteLocation.companyName }</td>
							<td>${userRemoteLocation.login }</td>
							<td><javatime:format
									value="${userRemoteLocation.expireDate}" style="MS" /></td>
							<td><span
								class="label ${userRemoteLocation.activated? 'label-success':'label-danger' }"
								onclick="UserRemoteLocation.changeActivated('${userRemoteLocation.id}','${ !userRemoteLocation.activated}')"
								style="cursor: pointer;">${userRemoteLocation.activated? "Activated" : "Deactivated"}</span></td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="UserRemoteLocation.showModalPopup($('#expireDateModal'),'${userRemoteLocation.id},${userRemoteLocation.login },${userRemoteLocation.expireDate},${userRemoteLocation.companyName }',2);">Edit</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/user-remote-location"
				var="urlUserRemoteLocation"></spring:url>

			<form id="userRemoteLocationForm" role="form" method="post"
				action="${urlUserRemoteLocation}">
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
									UserRemoteLocation</h4>
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
										<label class="control-label" for="field_companyName">Company
											Name</label> <input autofocus="autofocus" type="text"
											class="form-control" name="companyName"
											id="field_companyName" maxlength="255"
											placeholder="Company Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_login">Login</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="login" id="field_login" maxlength="255"
											placeholder="Login" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_alias">Expire
											Date</label>
										<div class="input-group">
											<input type="text" class="form-control" id="txtExpireDate"
												placeholder="Select Date"
												style="background-color: #fff; z-index: inherit;"
												readonly="readonly" />

											<div class="input-group-addon">
												<a href="#"><i class="entypo-calendar"></i></a>
											</div>
										</div>
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

			<div class="modal fade" role="dialog" id="expireDateModal">
				<div class="modal-dialog">

					<!-- Modal content-->
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal">&times;</button>
							<h4 class="modal-title">Update Expire Date</h4>
						</div>

						<div class="modal-body" style="height: 32%;">
							<div class="form-group">
								<label class="control-label" for="field_companyName">Company
									Name</label> <input autofocus="autofocus" type="text"
									class="form-control" name="companyName"
									id="field_companyName_e" maxlength="255"
									placeholder="Company Name" />
							</div>

							<div class="form-group">
								<label class="control-label" for="field_login">Login</label> <input
									autofocus="autofocus" type="text" class="form-control"
									name="login" id="field_login_e" maxlength="255"
									placeholder="Login" />
							</div>
							<div class="input-group">
								<input type="text" class="form-control" id="txtDate"
									placeholder="Select Date"
									style="background-color: #fff; z-index: inherit;"
									readonly="readonly" />

								<div class="input-group-addon">
									<a href="#"><i class="entypo-calendar"></i></a>
								</div>
							</div>
						</div>
						<div class="modal-footer">

							<input class="btn btn-success" type="button"
								id="btnConfirmExpireDate" value="Confirm" />
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Close</button>
						</div>
					</div>

				</div>
			</div>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/app/user-remote-location.js"
		var="userRemoteLocationsJs"></spring:url>
	<script type="text/javascript" src="${userRemoteLocationsJs}"></script>
</body>
</html>