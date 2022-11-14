<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Territory Wise Account</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
<style type="text/css">
.error {
	color: red;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Territory Wise Account</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								Territory <select id="dbTerrtory" name="terrtoryPid"
									class="form-control">
									<option value="no">All </option>
									<c:forEach items="${locations}" var="location">
										<option value="${location.pid}">${location.name}</option>
									</c:forEach>
								</select>
							</div>

							<div class="col-sm-2">
								Activated <select id="dbActivated" name="activated"
									class="form-control">
									<option value="true">Activated</option>
									<option value="false">Deactivated</option>
								</select>
							</div>
							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-info"
									onclick="TerritoryWiseAccount.filter()">Apply</button>
							</div>
							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-success"
									id="buttonActivated"
									onclick="TerritoryWiseAccount.changeStatus();"></button>
							</div>
							<div class="col-sm-1">
								<br>
								<button id="btnDownload" type="button" class="btn btn-success">Download
									Xls</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="table-responsive">
				<table id="tblTerritoryWiseAccounts"
					class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th><input type="checkbox" class="allcheckbox"
								id="selectAll" />&nbsp;&nbsp;All</th>
							<th>Account Name</th>
							<th>Address</th>
							<th>Phone</th>
							<th>Alias</th>
							<th>Description</th>
						</tr>
					</thead>
					<tbody id="tBodyTerritoryWiseAccount">
					</tbody>
				</table>
			</div>
			
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
			
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/territory-wise-account.js"
		var="territoryWiseAccountJs"></spring:url>
	<script type="text/javascript" src="${territoryWiseAccountJs}"></script>
	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>
	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

</body>
</html>