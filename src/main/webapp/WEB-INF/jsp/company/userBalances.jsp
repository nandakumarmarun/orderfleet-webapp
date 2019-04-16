<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | UserBalances</title>
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
			<h2>User Balances</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="UserBalance.showModalPopup($('#myModal'));">Create new
						User Balance</button>
				</div>
			</div>
			
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>User</th>
						<th>Date</th>
						<th>Amount</th>
						<th>Remarks</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					 <c:forEach items="${userBalances}" var="userBalance"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${userBalance.userName}</td>
								<td><javatime:format value="${userBalance.dateTime}" style="MS" /></td>
							<td>${userBalance.amount}</td>
							<td>${userBalance.remarks == null ? "" : userBalance.remarks}</td>
							
							<td>
								<button type="button" class="btn btn-blue"
									onclick="UserBalance.showModalPopup($('#viewModal'),'${userBalance.id}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="UserBalance.showModalPopup($('#myModal'),'${userBalance.id}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="UserBalance.showModalPopup($('#deleteModal'),'${userBalance.id}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach> 
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/user-balances" var="urlUserBalance"></spring:url>

			<form id="userBalanceForm" role="form" method="post" action="${urlUserBalance}">
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
									User Balance</h4>
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
										<label class="control-label" for="field_name">User</label> <select id="userPid" name="userPid"
											class="form-control">
												<option value="-1">Select User</option>
												<c:forEach items="${users}" var="user">
													<option value="${user.pid}">${user.firstName}</option>
												</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<div class="input-group">
											<input type="text" class="form-control" id="txtDate"
												placeholder="Select Date"
												style="background-color: #fff; z-index: 99999999999999;"
												readonly="readonly" />

											<div class="input-group-addon">
												<a href="#"><i class="entypo-calendar"></i></a>
											</div>
										</div>
									</div>

									<div class="form-group">
										<label class="control-label" for="field_amount">Amount</label> <input
											type="number" class="form-control" name="amount"
											id="field_amount"  placeholder="Amount" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_remarks">Remarks</label> <input
											type="text" class="form-control" name="remark"
											id="field_remarks" maxlength="55" placeholder="Remark" />
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
								<h4 class="modal-title" id="viewModalLabel">User Balance</h4>
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

									<dl class="dl-horizontal">
										<dt>
											<span>User</span>
										</dt>
										<dd>
											<span id="lbl_user"></span>
										</dd>
										<hr />
										<dt>
											<span>Date</span>
										</dt>
										<dd>
											<span id="lbl_date"></span>
										</dd>
										<hr />
										<dt>
											<span>Amount</span>
										</dt>
										<dd>
											<span id="lbl_amount"></span>
										</dd>
										<hr />
										<dt>
											<span>Remarks</span>
										</dt>
										<dd>
											<span id="lbl_remarks"></span>
										</dd>
										<hr />
									</dl>
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

			<form id="deleteForm" name="deleteForm" action="${urlUserBalance}">
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
									<p>Are you sure you want to delete this User Balance?</p>
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

	<spring:url value="/resources/app/user-balance.js" var="userBalanceJs"></spring:url>
	<script type="text/javascript" src="${userBalanceJs}"></script>
</body>
</html>