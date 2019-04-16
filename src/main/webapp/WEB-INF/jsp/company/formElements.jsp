<%@ taglib prefix="formElement"
	uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Questions</title>
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
			<h2>Questions</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="FormElement.showModalPopup($('#myModal'));">Create
						new Question</button>
				</div>
			</div>
			<br> <br>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn"
						onclick="FormElement.showModalPopup($('#enableFormElementModal'));">Deactivated
						Question</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Question</th>
						<th>Type</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${formElements}" var="formElement"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${formElement.name}</td>
							<td>${formElement.formElementTypeName}</td>
							<td><span
								class="label ${formElement.activated? 'label-success' : 'label-danger' }"
								onclick="FormElement.setActive('${formElement.name}','${formElement.pid}','${!formElement.activated}')"
								style="cursor: pointer;">${formElement.activated? "Activated" : "Deactivated" }</span></td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="FormElement.showModalPopup($('#viewModal'),'${formElement.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="FormElement.showModalPopup($('#myModal'),'${formElement.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="FormElement.showModalPopup($('#deleteModal'),'${formElement.pid}',2);">Delete</button>
								<button type="button" class="btn btn-info"
									onclick="FormElement.setDefaultValue('${formElement.pid}','${formElement.defaultValue}');">Default
									Value</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/formElements" var="urlFormElement"></spring:url>

			<form id="formElementForm" role="form" method="post"
				action="${urlFormElement}">
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
									Question</h4>
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
											<option value="Executives">Executives</option>
											<option value="OTHER">OTHER</option>
										</select>
										<button id="btnShowMasterData" type="button"
											class="btn btn-info" style="background-color: cadetblue;">Select
											Options</button>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_name">Options</label>
										<br> <input type="text" class="form-control"
											id="field_option" maxlength="400" placeholder="Option"
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

			<form name="viewFormElement" role="formElement">
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
								<h4 class="modal-title" id="viewModalLabel">Question</h4>
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

								<dl class="dl-horizontal">
									<dt>
										<span>Question</span>
									</dt>
									<dd>
										<span id="lbl_name"></span>
									</dd>
									<hr />
									<dt>
										<span>Type</span>
									</dt>
									<dd>
										<span id="lbl_type"></span>
									</dd>
									<hr />
								</dl>
								<table class="table  table-striped table-bordered"
									id="tb_Questions">
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

			<form id="deleteForm" name="deleteForm" action="${urlFormElement}">
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
								<p>Are you sure you want to delete this Question?</p>
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

			<div class="modal fade container" id="enableFormElementModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable Question</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>Question</th>
											</tr>
										</thead>
										<tbody id="tblEnableFormElement">
											<c:forEach items="${deactivatedFormElements}"
												var="formelement">
												<tr>
													<td><input name='formelement' type='checkbox'
														value="${formelement.pid}" /></td>
													<td>${formelement.name}</td>
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
								id="btnActivateFormElement" value="Activate" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>


			<!-- Model Container-->
			<div class="modal fade container" id="defaultValueModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Set Default Value</h4>
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
							<div class="modal-body">
								<div class="form-group" id="divTxtDefaultValue">
									<label class="control-label" for="txtDefaultValue">Default
										Value</label> <input class="form-control" id="txtDefaultValue"
										maxlength="900" autofocus="autofocus"
										placeholder="Default Value" />
								</div>
								<div class="form-group" id="divDbDefaultValue">
									<label class="control-label" for="dbDefaultValue">Default
										Value</label> <select id="dbDefaultValue" class="form-control">
										<option value="no">Select Default Value</option>
									</select>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
							<button id="btnDefaultValue" class="btn btn-primary">Save</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- /.Model Container-->

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/form-element.js" var="formElementJs"></spring:url>
	<script type="text/javascript" src="${formElementJs}"></script>
</body>
</html>