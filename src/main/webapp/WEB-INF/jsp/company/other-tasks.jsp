<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Other Tasks</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2 id="lblPageHeading">Other Tasks</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button id="btnNewQuestion" type="button" class="btn btn-success">Create
						new Question</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<div class="form-group">
				<div id="divQuestions">
					<div class="row">
						<div class="col-sm-5">
							<select id="sbForm" name="formPid" class="form-control">
								<option value="-1">Select Form</option>
								<c:forEach items="${forms}" var="form">
									<option value="${form.pid}">${form.name}</option>
								</c:forEach>
							</select>
						</div>
						<!-- <div class="col-sm-4">
							<input type="radio" value="all" name="filter" checked="checked">
							&nbsp;All&nbsp;&nbsp; <input type="radio" value="selected"
								name="filter"> &nbsp;Selected&nbsp;&nbsp; <input
								type="radio" value="unselected" name="filter">
							&nbsp;Unselected&nbsp;&nbsp;
						</div> -->
						<div class="col-sm-7">
							<div class="input-group">
								<input id="ofKeyupSearch" type="text"
									class="form-control" name="search"
									placeholder="Type for search...">
								<div class="input-group-btn">
									<button class="btn btn-primary btn-icon"
										style="pointer-events: none;">
										Search <i class="entypo-search"></i>
									</button>
								</div>
							</div>
							<!-- <div class="input-group">
								<input type="text" id="search" class="form-control"
									placeholder="Search..."> <span class="input-group-btn">
									<button class="btn btn-info" id="btnSearch" type="button">Go!</button>
								</span>
							</div> -->
						</div>
					</div>
				</div>
				<br />
				<table id="tblQuestions" class='table table-striped table-bordered ofKeyupTable'
					style="display: none">
					<thead>
						<tr>
							<!-- <th><input type="checkbox" class="allcheckbox"> All</th> -->
							<th>Question</th>
							<!-- <th>Sort Order</th>
							<th>Report Order</th>
							<th>Editable</th>
							<th>Validation Enabled</th> -->
							<th>Action</th>
						</tr>
					</thead>
					<tbody id="tbodyQuestions">
						<c:forEach items="${questions}" var="question">
							<tr>
								<%-- <td><input name='question' type='checkbox'
									value="${question.pid}" /></td> --%>
								<td>${question.name}</td>
								<%-- <td><input type="number" class="sortOrder"
									id="${question.pid}" min="0" max="500" maxlength="3" /></td>
								<td><input type="number" class="reportOrder"
									id="reportOrder${question.pid}" min="0" max="500" maxlength="3" /></td>
								<td><input id="editable${question.pid}" name='editable'
									type='checkbox' checked="checked" /></td>
								<td><input id="validationEnabled${question.pid}"
									name='validationEnabled' type='checkbox' /></td> --%>
								<td>
									<button type="button" class="btn btn-blue"
										onclick="$('#hdnQuestionPid').val('${question.pid}');$('#sbUsers').val('-1');$('#tBodyAccountProfile').html('');$('#assignUserAccountModal').modal('show');">Assign
										User and Account Profiles</button> <br /> <br />
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<hr />

		<form id="formElementForm" role="form" method="post"
			action="/web/other-tasks">
			<!-- Model Container-->
			<div class="modal fade container" id="newQuestionModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Create general question</h4>
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

							<div class="modal-body" style="overflow: auto; height: 500px;">
								<div class="form-group">
									<label class="control-label" for="field_name">Question</label>
									<textarea class="form-control" name="name" id="field_name"
										maxlength="1000" autofocus="autofocus" placeholder="Name"></textarea>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_type">Type</label> <select
										id="field_type" name="formElementTypeId" class="form-control">
										<option value="-1">Select Question Type</option>
										<c:forEach items="${formElementTypes}" var="formElementType">
											<option value="${formElementType.id}">${formElementType.name}</option>
										</c:forEach>
									</select>
								</div>
								<div id="divMasterTable" class="form-group"
									style="display: none;">
									<label class="control-label" for="field_master">Select
										Master</label> <br> <select id="field_master"
										class="form-control" style="width: 79%; display: inline;">
										<option value="-1">Select Master</option>
										<option value="ProductCategory">Product Category</option>
										<option value="ProductGroup">Product Group</option>
										<option value="ProductProfile">Product Profile</option>
										<option value="AccountProfile">Account Profile</option>
										<option value="OTHER">OTHER</option>
									</select>
									<button id="btnShowMasterData" type="button"
										class="btn btn-info" style="background-color: cadetblue;">Select
										Options</button>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_name">Options</label> <br>
									<input type="text" class="form-control" id="field_option"
										maxlength="400" placeholder="Option"
										style="width: 90%; display: inline;" />
									<button id="btnAddOption" type="button" class="btn btn-info">Add</button>
								</div>
								<table class="table  table-striped table-bordered">
									<thead>
										<tr>
											<th colspan="2"
												style="font-size: medium; b: n; font-weight: bold;">Options</th>
										</tr>
									</thead>
									<tbody id="tblOptions">
									</tbody>
								</table>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
							<button id="myFormElementSubmit" class="btn btn-primary">Save</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- /.Model Container-->
		</form>

		<div class="modal fade container" id="masterOptionsModal">
			<!-- model Dialog -->
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">Select Options</h4>
					</div>
					<div class="modal-body" style="overflow: auto; height: 530px">
						<div class="form-group" style="display: no;" id="divMasterType">
							<label class="control-label" for="dbMasterType">Master
								Type</label> <select id="dbMasterType" class="form-control">

							</select>
						</div>
						<div class="form-group">
							<div id="divMasterOptions">
								<table id="tblMasterOptions"
									class='table table-striped table-bordered'>

								</table>
							</div>
						</div>
						<label class="error-msg" style="color: red;"></label>
					</div>
					<div class="modal-footer">
						<button class="btn" data-dismiss="modal">Cancel</button>
						<input class="btn btn-success" type="button"
							id="btnAddMasterOptions" value="Add" />
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
		</div>

		<!-- Assign user&account -->
		<div class="modal fade container" id="assignUserAccountModal">
			<!-- model Dialog -->
			<div class="modal-dialog" style="width: 100%">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<input type="hidden" id="hdnQuestionPid" value="" />
						<h4 class="modal-title">Assign question to user and account</h4>
					</div>

					<div class="modal-body" style="overflow: auto; height: 500px">
						<div class="form-group">
							<div id="divTaskAssignment">
								<div class="row">
									<div class="col-sm-3">
										<select id="sbUsers" class="form-control">
											<option value="-1">Select User</option>
											<c:forEach items="${users}" var="user">
												<option value="${user.pid}">${user.firstName}
													(${user.login})</option>
											</c:forEach>
										</select>
									</div>
									<div class="col-sm-4">
										<input type="radio" value="all" name="popUpfilter"
											checked="checked"> &nbsp;All&nbsp;&nbsp; <input
											type="radio" value="selected" name="popUpfilter">
										&nbsp;Selected&nbsp;&nbsp; <input type="radio"
											value="unselected" name="popUpfilter">
										&nbsp;Unselected&nbsp;&nbsp;
									</div>
									<div class="col-sm-5">
										<div class="input-group">
											<input type="text" id="txtPopUpsearch" class="form-control"
												placeholder="Search..."> <span
												class="input-group-btn">
												<button class="btn btn-info" id="btnPopUpSearch"
													type="button">Go!</button>
											</span>
										</div>
									</div>
								</div>
								<br />
								<table id="tblAccounts" class='table table-striped table-bordered'>
									<thead>
										<tr>
											<th><label><input type="checkbox"
													class="allcheckbox" value="">All</label></th>
											<th>Name</th>
											<th>Description</th>
											<th>Address</th>
										</tr>
									</thead>
									<tbody id="tBodyAccountProfile">
									</tbody>
								</table>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<input class="btn btn-success" type="button" id="btnSaveAccountProfiles"
							value="Save" />
						<button class="btn" data-dismiss="modal">Cancel</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
		</div>

		<!-- Footer -->
		<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	<spring:url value="/resources/app/form-element.js" var="formElementJs"></spring:url>
	<script type="text/javascript" src="${formElementJs}"></script>
	<spring:url value="/resources/app/otherTasks.js" var="otherTasksJs"></spring:url>
	<script type="text/javascript" src="${otherTasksJs}"></script>

</body>
</html>