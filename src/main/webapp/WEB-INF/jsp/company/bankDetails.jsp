<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Bank Details</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Bank Details</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="BankDetails.showModalPopup($('#myModal'));">Create
						new Bank Details</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Bank Name</th>
						<th>Branch</th>
						<th>Account Number</th>
						<th>Account Holder Name</th>
						<th>Ifsc Code</th>
						<th>Swift Code</th>
						<th>Phone Number</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${bankDetails}" var="bankDetail"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${bankDetail.bankName}</td>
							<td>${bankDetail.branch == null ? "" : bankDetail.branch}</td>
							<td>${bankDetail.accountNumber == null ? "" : bankDetail.accountNumber}</td>
							<td>${bankDetail.accountHolderName == null ? "" : bankDetail.accountHolderName}</td>
							<td>${bankDetail.ifscCode == null ? "" : bankDetail.ifscCode}</td>
							<td>${bankDetail.swiftCode == null ? "" : bankDetail.swiftCode}</td>
							<td>${bankDetail.phoneNumber == null ? "" : bankDetail.phoneNumber}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="BankDetails.showModalPopup($('#viewModal'),'${bankDetail.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="BankDetails.showModalPopup($('#myModal'),'${bankDetail.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="BankDetails.showModalPopup($('#deleteModal'),'${bankDetail.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/bankDetails" var="urlBankDetails"></spring:url>

			<form id="bankDetailsForm" role="form" method="post"
				action="${urlBankDetails}">
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
									Bank Details</h4>
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
										<label class="control-label" for="field_bank_name">Bank
											Name</label> <input autofocus="autofocus" type="text"
											class="form-control" name="bankName" id="field_bank_name"
											maxlength="255" placeholder="Bank Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_branch">Branch</label>
										<input type="text" class="form-control" name="branch"
											id="field_branch" maxlength="55" placeholder="Branch" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_account_number">Account
											Number</label> <input type="text" class="form-control"
											name="accountNumber" id="field_account_number"
											placeholder="Account Number" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_account_holder_name">Account
											Holder Name</label> <input type="text" class="form-control"
											name="accountHolderName" id="field_account_holder_name"
											placeholder="Account Holder Name" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_ifsc_code">IFSC
											Code</label> <input type="text" class="form-control" name="ifscCode"
											id="field_ifsc_code" placeholder="IFSC Code" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_swift_code">Swift
											Code</label> <input type="text" class="form-control" name="swiftCode"
											id="field_swift_code" placeholder="Swift Code" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_phone_number">Phone
											Number</label> <input type="text" class="form-control"
											name="phoneNumber" id="field_phone_number"
											placeholder="Phone Number" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_qr_image">QR
											Code </label> <input id="qrImage" type="file"
											class="btn btn-default btn-block"> <img
											id="previewImage" src="" style="max-height: 100px;"
											alt="Image preview..."> <br>
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
								<h4 class="modal-title" id="viewModalLabel">Bank</h4>
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
											<span>Bank Name</span>
										</dt>
										<dd>
											<span id="lbl_bank_name"></span>
										</dd>
										<hr />
										<dt>
											<span>Branch</span>
										</dt>
										<dd>
											<span id="lbl_branch"></span>
										</dd>
										<hr />
										<dt>
											<span>Account Number</span>
										</dt>
										<dd>
											<span id="lbl_account_number"></span>
										</dd>
										<hr />
										<dt>
											<span>Account Holder Name</span>
										</dt>
										<dd>
											<span id="lbl_account_holder_name"></span>
										</dd>
										<hr />
										<dt>
											<span>IFSC Code</span>
										</dt>
										<dd>
											<span id="lbl_ifsc_code"></span>
										</dd>
										<hr />
										<dt>
											<span>Swift Code</span>
										</dt>
										<dd>
											<span id="lbl_swift_code"></span>
										</dd>
										<hr />
										<dt>
											<span>Phone Number</span>
										</dt>
										<dd>
											<span id="lbl_phone_number"></span>
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

			<form id="deleteForm" name="deleteForm" action="${urlBank}">
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
									<p>Are you sure you want to delete this Bank Detail?</p>
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

			<!-- 			<div class="modal fade container" id="enableBankModal"> -->
			<!-- 				model Dialog -->
			<!-- 				<div class="modal-dialog"> -->
			<!-- 					<div class="modal-content"> -->
			<!-- 						<div class="modal-header"> -->
			<!-- 							<button type="button" class="close" data-dismiss="modal" -->
			<!-- 								aria-label="Close"> -->
			<!-- 								<span aria-hidden="true">&times;</span> -->
			<!-- 							</button> -->
			<!-- 							<h4 class="modal-title">Enable Bank</h4> -->
			<!-- 						</div> -->
			<!-- 						<div class="modal-body" style="overflow: auto; height: 500px"> -->
			<!-- 							<div class="form-group"> -->
			<!-- 								<div id="accountsCheckboxes"> -->
			<!-- 									<table class='table table-striped table-bordered'> -->
			<!-- 										<thead> -->
			<!-- 											<tr> -->
			<!-- 												<th><input type="checkbox" class="allcheckbox">All</th> -->
			<!-- 												<th>Bank</th> -->
			<!-- 											</tr> -->
			<!-- 										</thead> -->
			<!-- 										<tbody id="tblEnableBank"> -->
			<%-- 											<c:forEach items="${deactivatedBanks}" var="bank"> --%>
			<!-- 												<tr> -->
			<!-- 													<td><input name='bank' type='checkbox' -->
			<%-- 														value="${bank.pid}" /></td> --%>
			<%-- 													<td>${bank.name}</td> --%>
			<!-- 												</tr> -->
			<%-- 											</c:forEach> --%>
			<!-- 										</tbody> -->
			<!-- 									</table> -->
			<!-- 								</div> -->
			<!-- 							</div> -->
			<!-- 							<label class="error-msg" style="color: red;"></label> -->
			<!-- 						</div> -->
			<!-- 						<div class="modal-footer"> -->
			<!-- 							<input class="btn btn-success" type="button" id="btnActivateBank" -->
			<!-- 								value="Activate" /> -->
			<!-- 							<button class="btn" data-dismiss="modal">Cancel</button> -->
			<!-- 						</div> -->
			<!-- 					</div> -->
			<!-- 					/.modal-content -->
			<!-- 				</div> -->
			<!-- 				/.modal-dialog -->
			<!-- 			</div> -->

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/bankDetails.js" var="bankDetailsJs"></spring:url>
	<script type="text/javascript" src="${bankDetailsJs}"></script>
</body>
</html>