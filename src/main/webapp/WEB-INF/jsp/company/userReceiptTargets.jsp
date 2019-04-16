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

<title>SalesNrich | User Receipt Target</title>
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
			<h2>User Receipt Targets</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="UserReceiptTarget.showModalPopup($('#myModal'));">Create
						new User Receipt Target</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>User</th>
						<th>Start Date</th>
						<th>End Date</th>
						<th>Target Amount</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${userReceiptTargets}"
						var="userReceiptTarget" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${userReceiptTarget.userName}</td>
							<td>${userReceiptTarget.startDate}</td>
							<td>${userReceiptTarget.endDate}</td>
							<td>${userReceiptTarget.targetAmount}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="UserReceiptTarget.showModalPopup($('#viewModal'),'${userReceiptTarget.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="UserReceiptTarget.showModalPopup($('#myModal'),'${userReceiptTarget.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="UserReceiptTarget.showModalPopup($('#deleteModal'),'${userReceiptTarget.pid}',2);">Delete</button>
								<button type="button" class="btn btn-info"
									onclick="UserReceiptTarget.showModalPopup($('#documentsModal'),'${userReceiptTarget.pid}',3);">Assign
									Documents</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/user-receipt-targets"
				var="urlUserReceiptTarget"></spring:url>

			<div class="modal fade container" id="documentsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Documents</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="documentsCheckboxes">
									<table class='table table-striped table-bordered'>
										<c:forEach items="${documents}" var="document">
											<tr>
												<td><input name='document' type='checkbox'
													value="${document.pid}" /></td>
												<td>${document.name}</td>
											</tr>
										</c:forEach>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveDocument"
								value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>



			<form id="userReceiptTargetForm" role="form" method="post"
				action="${urlUserReceiptTarget}">
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
									User Receipt Target</h4>
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
									<label class="control-label" for="field_startDate">Start
										Date</label> <input type="text" class="form-control" name="startDate"
										id="field_startDate" maxlength="55" placeholder="Start Date" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_endDate">End
										Date</label> <input type="text" class="form-control" name="endDate"
										id="field_endDate" maxlength="55" placeholder="End Date" />
								</div>
								<div class="form-group">
									<input type="radio" name="type" value="amount"
										checked="checked" /> Amount &nbsp; <input type="radio"
										name="type" value="percentage" /> Percentage
								</div>
								<div class="form-group" id="divAmount">
									<label class="control-label" for="field_targetAmount">Target
										Amount</label> <input type="text" class="form-control"
										name="targetAmount" id="field_targetAmount"
										placeholder="Target Amount" />
								</div>
								<div class="form-group" style="display: none;" id="divPercentage">
									<label class="control-label" for="field_targetPercentage">Target
										Percentage</label> <input type="text" class="form-control"
										name="targetPercentage" id="field_targetPercentage"
										placeholder="Target Percentage" />
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
								<h4 class="modal-title" id="viewModalLabel">User Receipt
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
											<td>Target Amount</td>
											<td id="lbl_targetAmount"></td>
										</tr>
										<tr>
											<td>Target Percentage</td>
											<td id="lbl_targetPercentage"></td>
										</tr>
										<tr>
											<td>Start Date</td>
											<td id="lbl_startDate"></td>
										</tr>
										<tr>
											<td>End Date</td>
											<td id="lbl_endDate"></td>
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
				action="${urlUserReceiptTarget}">
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

	<spring:url value="/resources/app/user-receipt-target.js"
		var="userReceiptTargetJs"></spring:url>
	<script type="text/javascript" src="${userReceiptTargetJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui.js" var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#field_startDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});
			$("#field_endDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});
		});
	</script>
</body>
</html>