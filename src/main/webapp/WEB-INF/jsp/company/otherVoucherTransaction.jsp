<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>
<title>SalesNrich | Other Voucher</title>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<script type="text/javascript">
	$(document).ready(function() {
		//select current user employee
		$("#dbEmployee").val("${currentEmployeePid}");
		$('[data-toggle="tooltip"]').tooltip(); 
	});
</script>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2 id="tittleName">Other Voucher</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="form-group">
					<div class="col-sm-2">
						Employee <select id="dbEmployee" class="form-control">
							<option value="no">Select Employee</option>
							<c:forEach items="${employees}" var="employee">
								<option value="${employee.pid}">${employee.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-3">
						Document<select id="dbDocument" name="userPid"
							class="form-control">
							<option value="no">Select Document</option>
							<c:forEach items="${activityDocuments}" var="document">
								<option value="${document.pid}">${document.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-2">
						Account <select id="dbAccount" name="accountPid"
							class="form-control">
							<option value="no">All Account</option>
						</select>
					</div>
					<div class="col-sm-5">
						<br /> <span data-toggle="tooltip" style="width: 65px; float: left; height: 31px;"
							class="input-group-addon btn btn-green"
							onclick="OtherVoucherTransaction.Account();" title="new account"><i
							class="glyphicon glyphicon-user"></i></span>&nbsp;
						<button type="button" data-toggle="tooltip" class="btn btn-orange" style="width: 65px;"
							onclick="OtherVoucherTransaction.loadForms()" title="create new document">New</button>
						<button type="button" data-toggle="tooltip" class="btn btn-info"
							onclick="OtherVoucherTransaction.search()" title="search document">Search</button>
							<button type="button" class="btn btn-green"
							onclick="OtherVoucherTransaction.returnToTransaction()">Return To Txn</button>
					</div>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<div id="divDynamicForms" class="row" style="display: none;">

				<div class="col-md-12" id="divlabel">
					<div class="col-sm-4">
						Previous Document Number : <label id="divPreviousDocumentNumber"
							style="font-weight: bold;">---</label>
					</div>
					<div class="col-sm-4">
						Document Name : <label id="divDocumentName"
							style="font-weight: bold;">---</label>
					</div>
					<div class="col-sm-4">
						Document Number : <label id="divDocumentNumber"
							style="font-weight: bold;">---</label>
					</div>
				</div>

				<!-- Submit button up-->
				<div class="col-md-12" id="divsubmit">
					<div class="form-group">
						<div class="col-sm-offset-11 col-sm-1"
							style="margin-bottom: 17px;">
							<button type="button"
								class="btnSubmitForms btn btn-default btn-success">Submit</button>
						</div>
					</div>
				</div>

				<!-- error alert -->
				<div class="col-md-12">
					<div class="alert alert-danger alert-dismissible" role="alert"
						style="display: none;">
						<button type="button" class="close" onclick="$('.alert').hide();"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<p></p>
					</div>
				</div>
				<!-- forms -->
				<div id="divForms" class="col-md-12"></div>

				<!-- Submit button down-->
				<div class="col-md-12" id="divsubmit2">
					<div class="form-group">
						<div class="col-sm-offset-11 col-sm-1"
							style="margin-bottom: 17px;">
							<button type="button"
								class="btnSubmitForms btn btn-default btn-success">Submit</button>
						</div>
					</div>
				</div>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>


			<!-- Model Container-->
			<div class="modal fade container" id="searchModal">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Other Vouchers</h4>
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
							<label>Document :</label>&nbsp; <label id="lblDocument"
								style="color: black; font-weight: bold;"></label> &nbsp;&nbsp; <label>Account
								:</label>&nbsp; <label id="lblAccount"
								style="color: black; font-weight: bold;"></label>
							<div class="table-responsive">
								<table class="collaptable table  table-striped table-bordered">
									<thead>
										<tr>
											<th>Date</th>
											<th>Document</th>
											<th>Document Number</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody id="tblDynamicDocuments">
									</tbody>
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


			<!-- Model Container-->
			<div class="modal fade container" id="printModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Print</h4>
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
							<input id="hdnPrintDocPid" type="hidden" value=""> <select
								id="sbPrint" class="form-control"></select>
							<div class="modal-footer">
								<button type="button" class="btn btn-success"
									onclick="OtherVoucherTransaction.print();">Print</button>
								<button type="button" onclick="$('#hdnPrintDocPid').val('');"
									class="btn btn-default" data-dismiss="modal">Cancel</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
			</div>
			<!-- Model Container-->
			<div class="modal fade container" id="emailModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Send Mail</h4>
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
							<input type="hidden" id="hdnDynamicDocumentPid" /> <select
								id="sbEmailPrint" class="form-control"></select>
							<div class="form-group">
								<label class="control-label" for="field_email_subject">Subject</label>
								<input autofocus="autofocus" type="text" class="form-control"
									name="subject" id="field_email_subject"
									placeholder="Email Subject" />
							</div>
							<div class="form-group">
								<label class="control-label" for="field_name">To Email
									Id's (Please separate email addresses with a comma and do not
									use spaces.)</label> <input autofocus="autofocus" type="text"
									class="form-control" name="email" id="field_email"
									maxlength="500" placeholder="Email" />
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-success"
								onclick="OtherVoucherTransaction.sendMail();">Send</button>
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="addAccountModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content" style="width: 120%">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Account Profile</h4>
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

							<div>
								<button type="button" id="addNewAccountProfile"
									class="btn btn-info" style="float: left;">Add Account</button>
								<button type="button" class="btn btn-info" id="btnSearch"
									style="float: right;">Search</button>
								<input type="text" id="search" placeholder="Search..."
									class="form-control" style="width: 200px; float: right;">
							</div>
						</div>
						<br>
						<hr>
						<div class="table-responsive">
							<table class='table table-striped table-bordered'
								id="allAccounts">
								<thead>
									<tr>
										<th>Name</th>
										<th>Address</th>
										<th>E-mail</th>
										<th>Phone</th>
										<th>Select</th>
									</tr>
								</thead>
								<tbody id="tblAccountProfiles">
								</tbody>
							</table>
						</div>
						<div class="modal-footer"></div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

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
								Account Profile</h4>
						</div>
						<div class="modal-body" style="height: 500px; overflow: auto;">
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
							<c:forEach items="${accountTypes}" var="accountType">
								<input type="hidden" class="form-control" name="accountTypePid"
									id="field_accountType" value="${accountType.pid}" />
							</c:forEach>

							<div class="form-group">
								<label class="control-label" for="field_address">Address</label>
								<textarea class="form-control" name="address" maxlength="250"
									id="field_address" placeholder="Address"></textarea>
							</div>
							<div class="form-group">
								<label class="control-label" for="field_city">City</label> <input
									type="text" class="form-control" name="city" id="field_city"
									maxlength="100" placeholder="City" />
							</div>

							<div class="form-group">
								<label class="control-label" for="field_email1">Email </label> <input
									type="email" class="form-control" name="email1"
									id="field_email1" maxlength="100" placeholder="Email " />
							</div>
							<div class="form-group">
								<label class="control-label" for="field_phone1">Phone </label> <input
									type="text" class="form-control" name="phone1"
									id="field_phone1" maxlength="20" placeholder="Phone " />
							</div>
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

			<div class="modal fade container" id="oploadImage">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Upload Image</h4>
						</div>
						<div id="divImgeUpload"></div>
						<div class="modal-body"
							style="height: 500px; overflow-x: hidden; overflow-y: scroll;" >
							<div class="modal-body">
								<dl class="dl-horizontal">
									<dt>
										<span>Document Name</span>
									</dt>
									<dd>
										<span id="lbl_docName"></span>
									</dd>
								</dl>

								<div class="col-md-6 col-md-offset-3">
									<div class="form-group">
										<select id="dbForm" name="formPid" class="form-control">
											<option value="no">Select Form</option>
										</select>
									</div>
								</div>

								<div class="col-md-12">
									<div class="panel-body">
										<form role="form" class="form-horizontal form-groups-bordered">
											<div class="form-group">
												<label class="col-sm-3 control-label">Image Upload</label>
												<div class="col-sm-5">
													<div class="fileinput fileinput-new"
														data-provides="fileinput">
														<div class="fileinput-new thumbnail"
															style="width: 200px; height: 150px;"
															data-trigger="fileinput">
															<img id="previewImage" src=""
																alt="">
														</div>
														<div class="fileinput-preview fileinput-exists thumbnail"
															style="max-width: 200px; max-height: 150px"></div>
														<div>
															<span class="btn btn-green btn-file"> <span id="btnSltImg"
																class="fileinput-new entypo-picture"> Select
																	image</span> <input type="file" name="image" id="field_image"
																accept="image/*">
															</span>
														</div>
													</div>
												</div>
											</div>
											<div class="modal-footer">
												<a class="btn btn-success" id="btnSaveFilledFormImage">
													<i class="entypo-floppy" title="Save"></i>
												</a> <a class="btn btn-danger" id="btnRemoveImage"> <i
													class="entypo-Trash" title="Remove"></i>
												</a>
											</div>
										</form>

									</div>
								</div>
								<div>
									<span id="showImages"></span>
								</div>
								<label class="error-msg" style="color: red;"></label>
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

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
		var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

	<spring:url value="/resources/app/other-voucher-transaction.js"
		var="otherVoucherTransactionJs"></spring:url>
	<script type="text/javascript" src="${otherVoucherTransactionJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>
</body>
</html>