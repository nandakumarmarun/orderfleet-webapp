<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<title>SalesNrich |User Mobile Configuration</title>

<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<!-- jQuery UI-->
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
			<h2>User Mobile Configuration</h2>
			<div class="clearfix"></div>
			<div class="form-group">
            					<div class="col-sm-3">
            						Company<select id="dbCompany" name="companyPid"
            							class="form-control selectpicker" data-live-search="true">
            							<option value="no">Select Company</option>
            							<c:forEach items="${companies}" var="company">
            								<option value="${company.pid}">${company.legalName}</option>
            							</c:forEach>
            						</select>
            					</div>
            					<div class="col-sm-3">
            						User <select id="dbUser" name="userPid"
            							class="form-control">
            							<option value="no">All User</option>
            						</select>
            					</div>
            					<div class="col-sm-3">
            						<br />
            						<button type="button" class="btn btn-orange" style="width: 65px;"
            							onclick="UserMobileConfiguration.getMobileConfigurationConfig()">Load</button>
            					</div>
            				</div>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success" id="mobilesModal">Create/Update
						User Mobile Configuration</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>UserName</th>
						<th>CompanyName</th>
						<th>LiveRouting</th>
                    	<th>Action</th>

					</tr>
				</thead>
				<tbody id ="tblConfig">
					<c:forEach items="${mobileConfigurations}"
						var="mobileConfiguration" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${mobileConfiguration.userName}</td>
							<td>${mobileConfiguration.companyName}</td>
							<td>${mobileConfiguration.liveRouting}</td>
						<td>
						<button type="button" class="btn btn-danger"
									onclick="UserMobileConfiguration.deleteConfig('${mobileConfiguration.pid}');">Delete</button></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<hr />

			<div class="modal fade container"
				id="assignMobileConfigurationsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign User Mobile
								Configuration</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">

							<div class="col-md-6 col-md-offset-3">
								<div class="form-group">
									<select id="dbCompanys" name="companyPid"
										class="form-control selectpicker" data-live-search="true"><option
											value="-1">Select Company</option>
										<c:forEach items="${companies}" var="company">
											<option value="${company.pid}">${company.legalName}
											</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
                                          	User <select id="dbUsers" name="userPid"	class="form-control">
                                           <option value="no">All User</option>
                                          </select>
                                  	</div>
							</div>

							<div class="form-group">
								<div id="divSyncOperations">
									<table class='table table-striped table-bordered'>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Live Routing
														</label> <input id="liveRouting" name='check'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>

									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveMobileConfigurations" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="alertBox">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body" id="alertMessage"
							style="font-size: large;"></div>
						<div class="modal-footer">
							<button id="btnDelete" class="btn btn-danger"
								data-dismiss="modal">Ok</button>
							<button class="btn btn-info" data-dismiss="modal">Close</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/user-mobile-configuration.js"
		var="userMobileConfigurationJs"></spring:url>
	<script type="text/javascript" src="${userMobileConfigurationJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
		var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

</body>
</html>