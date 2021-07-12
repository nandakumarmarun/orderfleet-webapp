<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | EmployeeProfile Sales Ledger</title>
<style type="text/css">
.error {
	color: red;
}
</style>
<spring:url
	value="/resources/assets/plugin/jstree/themes/default/style.min.css"
	var="jstreeCss"></spring:url>
<link href="${jstreeCss}" rel="stylesheet">
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Employee Sales Ledgers</h2>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12  clearfix">
					<form role="form" class="form-horizontal">
						<div class="form-group">
							<div class="col-sm-3">
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
									<select id="dbEmployee" name="employeePid" class="form-control">
										<option value="Dashboard Employee">All Dashboard
											Employees</option>
									</select>
								</div>
							</div>
							<div class="input-group col-sm-2">
								<div class="col-sm-3">
									<button type="button" class="btn btn-info entypo-search"
										style="font-size: 18px"
										onclick="EmployeeProfileSalesLedger.filter()" title="Apply"></button>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tbodyemployeeProfileSalesLedgers">
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/employeeProfile-salesLedgers"
				var="urlEmployeeProfileSalesLedger"></spring:url>

			<!-- 			<div class="modal fade container" id="salesLedgersModal"> -->
			<!-- 				model Dialog -->
			<!-- 				<div class="modal-dialog"> -->
			<!-- 					<div class="modal-content"> -->
			<!-- 						<div class="modal-header"> -->
			<!-- 							<button type="button" class="close" data-dismiss="modal" -->
			<!-- 								aria-label="Close"> -->
			<!-- 								<span aria-hidden="true">&times;</span> -->
			<!-- 							</button> -->
			<!-- 							<h4 class="modal-title">Assign SalesLedgers</h4> -->
			<!-- 						</div> -->
			<!-- 						<div class="modal-body" style="overflow: auto; height: 500px"> -->
			<!-- 							<label class="error-msg" style="color: red;"></label> <label><input -->
			<!-- 								id="cbSelectAll" type="checkbox" class="form-control" value="">Select -->
			<!-- 								All</label> -->
			<!-- 							<div class="form-group"> -->
			<!-- 								<div id="tree-container"></div> -->
			<!-- 							</div> -->
			<!-- 							<label class="error-msg" style="color: red;"></label> -->
			<!-- 						</div> -->
			<!-- 						<div class="modal-footer"> -->
			<!-- 							<input class="btn btn-success" type="button" id="btnSaveSalesLedger" -->
			<!-- 								value="Save" /> -->
			<!-- 							<button class="btn" data-dismiss="modal">Cancel</button> -->
			<!-- 						</div> -->
			<!-- 					</div> -->
			<!-- 					/.modal-content -->
			<!-- 				</div> -->
			<!-- 				/.modal-dialog -->
			<!-- 			</div> -->

			<div class="modal fade container" id="salesLedgersModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Sales Ledger</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="salesLedgerCheckboxes">
									<div class="row">
										<div class="col-md-12 clearfix">
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
									<div class="row">
										<div class="col-md-12 clearfix">
											<button id="btnNotAssigned" type="button"
												class="btn btn-success">Load Not Assigned Sales Ledgers</button>
										</div>
									</div>

									<br>
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">
													All</th>
												<th>SalesLedger</th>
											</tr>
										</thead>
										<tbody id="tblSalesLedger">
											<c:forEach items="${salesLedgers}" var="salesLedger">
												<tr>
													<td><input name='salesLedger' type='checkbox'
														value="${salesLedger.pid}" style="display: block;" /></td>
													<td>${salesLedger.name}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveSalesLedger"
								value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
			</div>


		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	<spring:url value="/resources/assets/plugin/jstree/jstree.min.js"
		var="jstreeJS"></spring:url>
	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<spring:url value="/resources/app/employee-salesLedger.js"
		var="employeeProfileSalesLedger"></spring:url>


	<script type="text/javascript" src="${jstreeJS}"></script>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>
	<script type="text/javascript" src="${employeeProfileSalesLedger}"></script>

	<script type="text/javascript">
		$(document).ready(function() {
			var employeePid = getParameterByName('user-key-pid');
			getEmployees(employeePid);
			EmployeeProfileSalesLedger.filter();
		});
	</script>

</body>
</html>