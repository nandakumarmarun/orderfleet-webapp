<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<title>SalesNrich | SalesTargetGroupUserTarget</title>
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
			<h2>Sales Target Group UserTargets</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="SalesTargetGroupUserTarget.showModalPopup($('#myModal'));">Create
						new SalesTargetGroup UserTarget</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>User</th>
						<th>SalesTargetGroup</th>
						<th>Volume</th>
						<th>Amount</th>
						<th>From Date</th>
						<th>To Date</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${salesTargetGroupUserTargets}"
						var="salesTargetGroupUserTarget" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${salesTargetGroupUserTarget.userName}</td>
							<td>${salesTargetGroupUserTarget.salesTargetGroupName}</td>
							<td>${salesTargetGroupUserTarget.volume}</td>
							<td>${salesTargetGroupUserTarget.amount}</td>
							<td>${salesTargetGroupUserTarget.fromDate}</td>
							<td>${salesTargetGroupUserTarget.toDate}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="SalesTargetGroupUserTarget.showModalPopup($('#viewModal'),'${salesTargetGroupUserTarget.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="SalesTargetGroupUserTarget.showModalPopup($('#myModal'),'${salesTargetGroupUserTarget.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="SalesTargetGroupUserTarget.showModalPopup($('#deleteModal'),'${salesTargetGroupUserTarget.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/sales-target-group-userTargets"
				var="urlSalesTargetGroupUserTarget"></spring:url>

			<form id="salesTargetGroupUserTargetForm" role="form" method="post"
				action="${urlSalesTargetGroupUserTarget}">
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
									SalesTargetGroup UserTarget</h4>
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
									<label class="control-label" for="field_user">User</label> <select
										id="field_user" name="userPid" class="form-control"><option
											value="-1">Select User</option>
										<c:forEach items="${users}" var="user">
											<option value="${user.pid}">${user.firstName}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_salesTargetGroup">Sales
										TargetGroup</label> <select id="field_salesTargetGroup"
										name="salesTargetGroupPid" class="form-control">
										<option value="-1">Select SalesTargetGroup</option>
										<c:forEach items="${salesTargetGroups}" var="salesTargetGroup">
											<option value="${salesTargetGroup.pid}">${salesTargetGroup.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_volume">Volume</label>
									<input type="text" class="form-control" name="volume"
										id="field_volume" placeholder="Volume" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_amount">Amount</label>
									<input type="Text" class="form-control" name="amount"
										id="field_amount" placeholder="Amount" />
								</div>

								<div class="form-group">
									<label class="control-label" for="field_fromDate">From
										Date</label> <input type="text" class="form-control" name="fromDate"
										id="field_fromDate" maxlength="55" placeholder="From Date" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_toDate">To Date</label>
									<input type="text" class="form-control" name="toDate"
										id="field_toDate" maxlength="55" placeholder="To Date" />
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
								<h4 class="modal-title" id="viewModalLabel">Activity User
									Target</h4>
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
										<tr>
											<td>User</td>
											<td id="lbl_user"></td>
										</tr>
										<tr>
											<td>Sales Target Group</td>
											<td id="lbl_salesTargetGroup"></td>
										</tr>
										<tr>
											<td>Volume</td>
											<td id="lbl_volume"></td>
										</tr>
										<tr>
											<td>Amount</td>
											<td id="lbl_amount"></td>
										</tr>

										<tr>
											<td>Start Date</td>
											<td id="lbl_fromDate"></td>
										</tr>
										<tr>
											<td>End Date</td>
											<td id="lbl_toDate"></td>
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

			<form id="deleteForm" name="deleteForm"
				action="${urlSalesTargetGroupUserTarget}">
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
									<p>Are you sure you want to delete this Activity User
										Target ?</p>
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
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/sales_target_group_userTarget.js"
		var="salesTargetGroupUserTargetJs"></spring:url>
	<script type="text/javascript" src="${salesTargetGroupUserTargetJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui.js" var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#field_fromDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});
			$("#field_toDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});
		});
	</script>
</body>
</html>