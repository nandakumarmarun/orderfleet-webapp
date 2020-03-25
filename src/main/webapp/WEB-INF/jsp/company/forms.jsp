<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Forms</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Forms</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="Form.showModalPopup($('#myModal'));">Create new
						Form</button>
				</div>
			</div>
			<br> <br>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn"
						onclick="Form.showModalPopup($('#enableFormModal'));">Deactivated
						Form</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Description</th>
						<th>Multiple Record</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${forms}" var="form" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${form.name}</td>
							<td>${form.description == null ? "" : form.description}</td>
							<td>${form.multipleRecord}</td>
							<td><span
								class="label ${form.activated? 'label-success':'label-danger' }"
								onclick="Form.setActive('${form.name}','${ form.pid}','${!form.activated }')"
								style="cursor: pointer;">${form.activated? "Activated" : "Deactivated" }</span></td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="Form.showModalPopup($('#viewModal'),'${form.pid}',0,this);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="Form.showModalPopup($('#myModal'),'${form.pid}',1,this);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="Form.showModalPopup($('#deleteModal'),'${form.pid}',2,this);">Delete</button>
								<button type="button" class="btn btn-info"
									onclick="Form.showModalPopup($('#assignQuestionModal'),'${form.pid}',3,this);">Assign
									Questions</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/forms" var="urlForm"></spring:url>

			<form id="formForm" role="form" method="post" action="${urlForm}">
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
									Form</h4>
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
									<label class="control-label" for="field_name">Name</label> <input
										autofocus="autofocus" type="text" class="form-control"
										name="name" id="field_name" maxlength="255" placeholder="Name" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_jsCode">JS Code</label>
									<textarea class="form-control" id="field_jsCode" name="jsCode"
										placeholder="JS Code" style="height: 300px"></textarea>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_description">Description</label>
									<textarea class="form-control" name="description"
										maxlength="250" id="field_description"
										placeholder="Description"></textarea>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_multipleRecord">Multiple
										Record</label> <input autofocus="autofocus" type="checkbox"
										class="form-control" name="multipleRecord"
										id="field_multipleRecord" />
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
								<h4 class="modal-title" id="viewModalLabel">Form</h4>
							</div>
							<div class="modal-body" style="overflow: auto; height: 500px">
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
										<span>Name</span>
									</dt>
									<dd>
										<span id="lbl_name"></span>
									</dd>
									<hr />
									<dt>
										<span>Description</span>
									</dt>
									<dd>
										<span id="lbl_description"></span>
									</dd>
									<hr />
									<dt>
										<span>Multiple Record</span>
									</dt>
									<dd>
										<span id="lbl_multipleRecord"></span>
									</dd>
									<hr />
								</dl>
								<table class="table  table-striped table-bordered"
									id="tblQuestions">
								</table>
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

			<form id="deleteForm" name="deleteForm" action="${urlForm}">
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
								<!-- error message -->
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>
								<p>Are you sure you want to delete this Form?</p>
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

			<div class="modal fade container" id="assignQuestionModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content" style="width: fit-content;">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Questions</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divQuestions">
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
									<br />
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">
													All</th>
												<th>Question</th>
												<th>Sort Order</th>
												<th>Report Order</th>
												<th>Editable</th>
												<th>Validation Enabled</th>
												<th>Mandatory</th>
												<th>Visibility</th>
												<th>Dashboard Visibility</th>
											</tr>
										</thead>
										<tbody id="tbodyQuestions">
											<c:forEach items="${questions}" var="question">
												<tr>
													<td><input name='question' type='checkbox'
														value="${question.pid}" /></td>
													<td>${question.name}</td>
													<td><input type="number" class="sortOrder"
														id="${question.pid}" min="0" max="500" maxlength="3" /></td>
													<td><input type="number" class="reportOrder"
														id="reportOrder${question.pid}" min="0" max="500"
														maxlength="3" /></td>
													<td><input id="editable${question.pid}"
														name='editable' type='checkbox' checked="checked" /></td>
													<td><input id="validationEnabled${question.pid}"
														name='validationEnabled' type='checkbox' /></td>
													<td><input id="mandatory${question.pid}"
														name='mandatoryEnabled' type='checkbox' /></td>
													<td><input id="visibility${question.pid}"
														name='visibility' type='checkbox' checked="checked" /></td>
													<td><input id="dashboardVisibility${question.pid}"
														name='dashboardVisibility' type='checkbox' /></td>
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
								id="btnSaveQuestions" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="enableFormModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable Form</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>Form</th>
											</tr>
										</thead>
										<tbody id="tblEnableForm">
											<c:forEach items="${deactivatedForms}" var="form">
												<tr>
													<td><input name='form' type='checkbox'
														value="${form.pid}" /></td>
													<td>${form.name}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnActivateForm"
								value="Activate" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/form.js" var="formJs"></spring:url>
	<script type="text/javascript" src="${formJs}"></script>
</body>
</html>