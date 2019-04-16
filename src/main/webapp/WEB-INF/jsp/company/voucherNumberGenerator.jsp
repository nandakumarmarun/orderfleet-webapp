<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Voucher Number Generators</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Voucher Number Generators</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="VoucherNumberGenerator.showModalPopup($('#myModal'));">Create
						new Voucher Number Generator</button>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
					<th>User</th>
						<th>Document</th>
						<th>Prefix</th>
						<th>Start With</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${VoucherNumberGenerators}"
						var="VoucherNumberGenerator" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
						<td>${VoucherNumberGenerator.userName}</td>
							<td>${VoucherNumberGenerator.documentName}</td>
							<td>${VoucherNumberGenerator.prefix == null ? "" : VoucherNumberGenerator.prefix}</td>
							<td>${VoucherNumberGenerator.startWith == 0 ? 0 : VoucherNumberGenerator.startWith}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="VoucherNumberGenerator.showModalPopup($('#viewModal'),'${VoucherNumberGenerator.id}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="VoucherNumberGenerator.showModalPopup($('#myModal'),'${VoucherNumberGenerator.id}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="VoucherNumberGenerator.showModalPopup($('#deleteModal'),'${VoucherNumberGenerator.id}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/voucher-number-generator"
				var="urlVoucherNumberGenerator"></spring:url>

			<form id="VoucherNumberGeneratorForm" role="form" method="post"
				action="${urlVoucherNumberGenerator}">
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
									Voucher Number Generator</h4>
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
									<label class="control-label" for="field_user">User</label>
										<select id="dbUser" name="userPid" class="form-control"
											onchange="VoucherNumberGenerator.onChangeUser()">
											<option value="no">Select User</option>
											<c:forEach items="${users}" var="user">
												<option value="${user.pid}">${user.firstName}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
									<label class="control-label" for="field_document">Document</label>
										<select id="dbDocument" name="documentPid"
											class="form-control">
											<option value="no">Select Document</option>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_prefix">Prefix</label>
										<input type="text" class="form-control" name="prefix"
											id="field_prefix" maxlength="10" placeholder="Prefix" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_startWith">startWith</label>
										<input type="number" class="form-control" name="startWith"
											id="field_startWith" value="0" placeholder="startWith" />
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
								<h4 class="modal-title" id="viewModalLabel">Voucher Number Generator</h4>
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
											<span>Document</span>
										</dt>
										<dd>
											<span id="lbl_document"></span>
										</dd>
										<hr />
										<dt>
											<span>Prefix</span>
										</dt>
										<dd>
											<span id="lbl_prefix"></span>
										</dd>
										<hr />
										<dt>
											<span>startWith</span>
										</dt>
										<dd>
											<span id="lbl_startWith"></span>
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

			<form id="deleteForm" name="deleteForm"
				action="${urlVoucherNumberGenerator}">
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
									<p>Are you sure you want to delete this
										Voucher Number Generator?</p>
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

	<spring:url value="/resources/app/voucher-number-generator.js"
		var="VoucherNumberGeneratorJs"></spring:url>
	<script type="text/javascript" src="${VoucherNumberGeneratorJs}"></script>
</body>
</html>