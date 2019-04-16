<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | File Upload</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>File Upload</h2>
			<div class="row">
				<div class="col-md-3 col-md-offset-3">
					<div class="form-group">
						<select id="field_company" name="companyPid" class="form-control"><option
								value="-1">Select Company</option>
							<c:forEach items="${companies}" var="company">
								<option value="${company.pid}">${company.legalName}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="clearfix"></div>
				<hr />
				<div class="col-md-12">
					<div class="panel-body">
						<form role="form" class="form-horizontal form-groups-bordered">
							<div class="form-group">
								<label class="col-sm-2 control-label">Account Profile</label>
								<div class="col-sm-4">
									<input type="file" id="txtAccountFile"
										class="form-control file2 inline btn btn-primary"
										name="accountFile"
										data-label="<i class='glyphicon glyphicon-circle-arrow-up'></i> &nbsp;Browse Xls Files" />
								</div>
								<button type="button" class="btn btn-success "
									id="assignAccountColumnNumbers">Save Account Profiles</button>
							</div>
						</form>
					</div>
				</div>

				<div class="col-md-12">
					<div class="panel-body">
						<form role="form" class="form-horizontal form-groups-bordered">
							<div class="form-group">
								<label class="col-sm-2 control-label">Product Profile</label>
								<div class="col-sm-4">
									<input type="file" id="txtProductFile"
										class="form-control file2 inline btn btn-primary"
										name="productFile"
										data-label="<i class='glyphicon glyphicon-circle-arrow-up'></i> &nbsp;Browse Xls Files" />
								</div>
								<button type="button" class="btn btn-success "
									id="assignProductColumnNumbers">Save Product Profiles</button>
							</div>
						</form>
					</div>
				</div>
			</div>
			<hr />
			<div class="modal fade container" id="accountColumnNumbers">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Account Column Numbers</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accounttColumn">
									<div>
										<p style="font-size: large; color: #d82b2b;">column number
											start with 0</p>
									</div>
									<table class='table table-striped table-bordered'>
										<tr>
											<td>Account Name</td>
											<td><input type="number" id="nameColumnNumber"
												maxlength="2" placeholder="Name Column Number" /></td>
										</tr>
										<tr>
											<td>Address</td>
											<td><input type="number" id="addressColumnNumber"
												maxlength="2" placeholder="Address Column Number" /></td>
										</tr>
										<tr>
											<td>City</td>
											<td><input type="number" id="cityColumnNumber"
												maxlength="2" placeholder="City Column Number" /></td>
										</tr>
										<tr>
											<td>Location</td>
											<td><input type="number" id="locationColumnNumber"
												maxlength="2" placeholder="Location Column Number" /></td>
										</tr>
										<tr>
											<td>Pin</td>
											<td><input type="number" id="pinColumnNumber"
												maxlength="2" placeholder="Pin Column Number" /></td>
										</tr>
										<tr>
											<td>Phone</td>
											<td><input type="number" id="phoneColumnNumber"
												maxlength="2" placeholder="Phone Column Number" /></td>
										</tr>
										<tr>
											<td>E-mail</td>
											<td><input type="number" id="eMailColumnNumber"
												maxlength="2" placeholder="e-Mail Column Number" /></td>
										</tr>
										<tr>
											<td>Description</td>
											<td><input type="number" id="descriptionColumnNumber"
												maxlength="2" placeholder="Description Column Number" /></td>
										</tr>
										<tr>
											<td>Contact Person</td>
											<td><input type="number" id="contactPersonColumnNumber"
												maxlength="2" placeholder="Contact Person Column Number" /></td>
										</tr>
										<tr>
											<td>Account Type</td>
											<td><input type="number" id="accountTypeColumnNumber"
												maxlength="2" placeholder="Account Type Column Number" /></td>
										</tr>
										<tr>
											<td>Territory</td>
											<td><input type="number" id="territoryColumnNumber"
												maxlength="2" placeholder="Territory Column Number" /></td>
										</tr>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-success " id="uploadAccount">Save</button>
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="productColumnNumbers">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Product Column Numbers</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="productColumn">
									<div>
										<p style="font-size: large; color: #d82b2b;">column number
											start with 0</p>
									</div>
									<table class='table table-striped table-bordered'>
										<tr>
											<td>Product Name</td>
											<td><input type="number" id="productNameColumnNumber"
												maxlength="2" placeholder="Name Column Number" /></td>
										</tr>
										<tr>
											<td>Alias</td>
											<td><input type="number" id="productAliasColumnNumber"
												maxlength="2" placeholder="Alias Column Number" /></td>
										</tr>
										<tr>
											<td>Description</td>
											<td><input type="number"
												id="productDescriptionColumnNumber" maxlength="2"
												placeholder="Description Column Number" /></td>
										</tr>
										<tr>
											<td>Price</td>
											<td><input type="number" id="productPriceColumnNumber"
												maxlength="2" placeholder="Price Column Number" /></td>
										</tr>
										<tr>
											<td>Stock Unit Quantity(SKU)</td>
											<td><input type="number" id="productSkuColumnNumber"
												maxlength="2" placeholder="SKU Column Number" /></td>
										</tr>
										<tr>
											<td>Unit Quantity</td>
											<td><input type="number"
												id="productUnitQuantityColumnNumber" maxlength="2"
												placeholder="Unit Quantity Column Number" /></td>
										</tr>
										<tr>
											<td>Tax Rate</td>
											<td><input type="number" id="productTaxRateColumnNumber"
												maxlength="2" placeholder="Tax Rate Column Number" /></td>
										</tr>
										<tr>
											<td>Size</td>
											<td><input type="number" id="productSizeColumnNumber"
												maxlength="2" placeholder="Size Column Number" /></td>
										</tr>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-success " id="uploadProduct">Save</button>
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="alertBox">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body" id="alertMessage"
							style="font-size: large;"></div>
						<div class="modal-footer">
							<button class="btn btn-info" data-dismiss="modal">Close</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<%-- 	<spring:url value="/web/banks" var="urlBank"></spring:url> --%>
		</div>
	</div>

	<spring:url value="/resources/assets/js/joinable.js" var="joinable"></spring:url>
	<script type="text/javascript" src="${joinable}"></script>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/upload-xls.js" var="uploadXlsJs"></spring:url>
	<script type="text/javascript" src="${uploadXlsJs}"></script>
</body>
</html>