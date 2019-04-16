<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | AccountProfile DynamicDocument
	Accountprofiles</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Account Profiling</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="AccountProfileDynamicDocumentAccountprofile.showModalPopup($('#myModal'));">Create
						new</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Document Name</th>
						<th>Form Name</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${accountProfileDynamicDocumentAccountprofiles}"
						var="accountProfileDynamicDocumentAccountprofile"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${accountProfileDynamicDocumentAccountprofile.documentName}</td>
							<td>${accountProfileDynamicDocumentAccountprofile.formName == null ? "" : accountProfileDynamicDocumentAccountprofile.formName}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="AccountProfileDynamicDocumentAccountprofile.showModalPopup($('#myEditModal'),'${accountProfileDynamicDocumentAccountprofile.documentPid}','${accountProfileDynamicDocumentAccountprofile.formPid}');">edit
									details</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>


			<div id="accountProfileDynamicDocumentAccountprofileForm">
				<!-- Model Container-->
				<div class="modal fade container" id="myModal">
					<!-- model Dialog -->
					<div class="modal-dialog" style="width: 100%;">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Create a
									Account-Profile Dynamic-Document Account-profile</h4>
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
								<div class="row">
									<div class="col-sm-4">
										<select id="field_dynamicDocument" name="dynamicDocumentPid"
											class="form-control"><option value="-1">Select
												dynamic document</option>
											<c:forEach items="${dynamicdocuments}" var="dynamicDocument">
												<option value="${dynamicDocument.pid}">${dynamicDocument.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="col-sm-4 ">
										<select id="field_form" name="formPid" class="form-control"><option
												value="-1">select form</option>
										</select>
									</div>
								</div>
								<input type="hidden" id="accountProfileFields"
									value='${accountProfileFields}' />
								<div class="modal-body" style="overflow: auto; height: 70%">
									<table id="tbl_accountDynamicAccount"
										class="table  table-striped table-bordered">
										<thead>
											<tr>
												<th>Account field</th>
												<th>Question</th>
											</tr>
										</thead>
										<tbody id="tb_datas">
											<tr>
												<td>Name</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>

											<tr>
												<td>Alias</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>

											<tr>
												<td>City</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>

											<tr>
												<td>Location</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>

											<tr>
												<td>Pin</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>

											<tr>
												<td>Phone 1</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>
											<tr>
												<td>Phone 2</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>
											<tr>
												<td>E-Mail 1</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>
											<tr>
												<td>E-Mail 2</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>
											<tr>
												<td>Address</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>
											<tr>
												<td>Description</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>

											<tr>
												<td>Credit Days</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>

											<tr>
												<td>Closing Balance</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>

											<tr>
												<td>Default Discount Percentage</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>

											<tr>
												<td>Credit Limits</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>

											<tr>
												<td>Contact Person</td>
												<td><select id="field_priceLevel"
													name="defaultPriceLevelPid" class="form-control slectbox">
														<option value="-1">select question</option>
												</select></td>
											</tr>
										</tbody>
									</table>
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
			</div>


			<!-- Model Container-->
			<div class="modal fade container" id="myEditModal">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">view or edit
								Account-Profile Dynamic-Document Account-profile</h4>
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
							<input type="hidden" id="accountProfileFields"
								value='${accountProfileFields}' />
							<div class="modal-body" style="overflow: auto; height: 70%">
								<table id="tbl_editAccountDynamicAccount"
									class="table  table-striped table-bordered">
									<thead>
										<tr>
											<th>Account field</th>
											<th>Question</th>
										</tr>
									</thead>
									<tbody id="tb_editDatas">
										<tr>
											<td>Name</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>

										<tr>
											<td>Alias</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>

										<tr>
											<td>City</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>

										<tr>
											<td>Location</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>

										<tr>
											<td>Pin</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>

										<tr>
											<td>Phone 1</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>
										<tr>
											<td>Phone 2</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>
										<tr>
											<td>E-Mail 1</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>
										<tr>
											<td>E-Mail 2</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>
										<tr>
											<td>Address</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>
										<tr>
											<td>Description</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>

										<tr>
											<td>Credit Days</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>

										<tr>
											<td>Closing Balance</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>

										<tr>
											<td>Default Discount Percentage</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>

										<tr>
											<td>Credit Limits</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>

										<tr>
											<td>Contact Person</td>
											<td><select id="field_priceLevel"
												name="defaultPriceLevelPid" class="form-control slectbox">
													<option value="-1">select question</option>
											</select></td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>
						<div class="modal-footer">
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button id="myEditFormSubmit" class="btn btn-primary">Save</button>
							</div>
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

	<spring:url
		value="/resources/app/accountProfileDynamicDocumentAccountprofile.js"
		var="accountProfileDynamicDocumentAccountprofileJs"></spring:url>
	<script type="text/javascript"
		src="${accountProfileDynamicDocumentAccountprofileJs}"></script>

</body>
</html>