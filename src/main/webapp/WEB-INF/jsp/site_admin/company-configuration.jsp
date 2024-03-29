<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
		<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
			<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

				<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util" %>

					<html lang="en">

					<head>
						<title>SalesNrich | Company Configuration</title>

						<jsp:include page="../fragments/m_head.jsp"></jsp:include>

						<!-- jQuery UI-->
						<spring:url value="/resources/assets/css/jquery-ui.css" var="jqueryUiCss"></spring:url>
						<link href="${jqueryUiCss}" rel="stylesheet">

					</head>

					<body class="page-body" data-url="">
						<div class="page-container">
							<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
							<div class="main-content">
								<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
								<hr />
								<h2>Company Configuration</h2>
								<div class="clearfix"></div>
								<div class="row col-xs-12">
									<div class="pull-right">
										<button type="button" class="btn btn-success" id="companysModal">Create/Update
											Company Configuration</button>
									</div>
								</div>
								<div class="clearfix"></div>
								<hr />
								<table class="table  table-striped table-bordered">
									<thead>
										<tr>
											<th>Company Name</th>
											<th>Distance Traveled</th>
											<th>Location Variance</th>
											<th>Interim Save</th>
											<th>Refresh ProductGroup Product</th>
											<th>Stage Change For Accounting Voucher</th>
											<th>New Customer Alias</th>
											<th>Chat Reply</th>
											<th>Sales Pdf Download</th>
											<th>Visit Based Transaction</th>
											<th>Sales Management</th>
											<th>Receipts Management</th>
											<th>Sales Edit Enabled</th>
											<th>Gps Variance Query</th>
											<th>Send Sales Order Email</th>
											<th>Send Sales Order Sap</th>
											<th>Pieces To Quantity</th>
											<th>Send Sales Order Odoo</th>
											<th>Send Transactions Sap Pravesh</th>
											<th>Add Compound Unit</th>
											<th>Update Stock Location</th>
											<th>Send To Odoo</th>
											<th>Product Group Tax</th>
											<th>Alias To Name</th>
											<th>Description TO Name</th>
											<th>Stock api</th>
											<th>Employee Create Btn</th>
											<th>Modern Special Config</th>
											<th>Sales Order Status</th>
											<th>Update Reciept</th>
											<th>Send To Focus</th>
											<th>Send SalesOrder Email Automatically</th>
											<th>CRM Enabled</th>
											<th>Outstanding Datewise Sorting</th>
											<th>EnableStockCalculations</th>
											<th>EnableOutStanding</th>
											<th>EnableKiloCalc</th>
											<th>Enable Distance Slab Calculation</th>
											<th>Enable New DashBoard</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${companyConfigurations}" var="companyConfiguration"
											varStatus="loopStatus">
											<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
												<td>${companyConfiguration.companyName}</td>
												<td>${companyConfiguration.distanceTraveled}</td>
												<td>${companyConfiguration.locationVariance}</td>
												<td>${companyConfiguration.interimSave}</td>
												<td>${companyConfiguration.refreshProductGroupProduct}</td>
												<td>${companyConfiguration.stageChangeAccountingVoucher}</td>
												<td>${companyConfiguration.newCustomerAlias}</td>
												<td>${companyConfiguration.chatReply}</td>
												<td>${companyConfiguration.salesPdfDownload}</td>
												<td>${companyConfiguration.visitBasedTransaction}</td>
												<th>${companyConfiguration.salesManagement}</th>
												<th>${companyConfiguration.receiptsManagement}</th>
												<th>${companyConfiguration.salesEditEnabled}</th>
												<th>${companyConfiguration.gpsVarianceQuery}</th>
												<th>${companyConfiguration.sendSalesOrderEmail}</th>
												<th>${companyConfiguration.sendSalesOrderSap}</th>
												<th>${companyConfiguration.piecesToQuantity}</th>
												<th>${companyConfiguration.sendSalesOrderOdoo}</th>
												<th>${companyConfiguration.sendTransactionsSapPravesh}</th>
												<th>${companyConfiguration.addCompoundUnit}</th>
												<th>${companyConfiguration.updateStockLocation}</th>
												<th>${companyConfiguration.sendToOdoo}</th>
												<th>${companyConfiguration.enableProductGroupTax}</th>
												<th>${companyConfiguration.aliasToName}</th>
												<th>${companyConfiguration.descriptionToName}</th>
												<th>${companyConfiguration.stockApi}</th>
												<th>${companyConfiguration.employeeCreateBtn}</th>
												<th>${companyConfiguration.modernSpecialConfig}</th>
												<th>${companyConfiguration.salesOrderStatus}</th>
												<th>${companyConfiguration.updateReciept}</th>
												<th>${companyConfiguration.sendToFocus}</th>
												<th>${companyConfiguration.sendEmailAutomaticaly}</th>
												<th>${companyConfiguration.crmEnable}</th>
												<th>${companyConfiguration.outstandingDateSorting}</th>
												<th>${companyConfiguration.enableStockCalculations}</th>
												<th>${companyConfiguration.enableOutStanding}</th>
												<th>${companyConfiguration.kilometercalculationsenable}</th>
												<th>${companyConfiguration.enableDistanceSlabCalc}</th>
												<th>${companyConfiguration.enableNewDashboard}</th>
												<td><button type="button" class="btn btn-danger"
														onclick="CompanyConfiguration.deletes('${companyConfiguration.companyPid}');">Delete</button>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
								<hr />
								<!-- Footer -->
								<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
								<hr />

								<div class="modal fade container" id="assignCompanyConfigurationsModal">
									<!-- model Dialog -->
									<div class="modal-dialog">
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal"
													aria-label="Close">
													<span aria-hidden="true">&times;</span>
												</button>
												<h4 class="modal-title" id="myModalLabel">Assign Company
													Configuration</h4>
											</div>
											<div class="modal-body" style="overflow: auto; height: 500px">

												<div class="col-md-6 col-md-offset-3">
													<div class="form-group">
														<select id="dbCompany" name="companyPid"
															class="form-control selectpicker" data-live-search="true">
															<option value="-1">Select Company</option>
															<c:forEach items="${companies}" var="company">
																<option value="${company.pid}">${company.legalName}
																</option>
															</c:forEach>
														</select>
													</div>
												</div>

												<div class="form-group">
													<div id="divSyncOperations">
														<table class='table table-striped table-bordered'>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Distance
																			Traveled</label> <input
																			id="distanceTraveled"
																			name='checkDistanceTraveled' type='checkbox'
																			class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Location
																			Variance</label> <input
																			id="locationVariance"
																			name='checksLocationVariance'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Interim
																			Save</label> <input id="interimSave"
																			name='checksInterimSave' type='checkbox'
																			class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Refresh
																			ProductGroup
																			Product</label> <input
																			id="refreshProductGroupProduct"
																			name='checksRefreshProductGroupProduct'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Stage Change For
																			Accounting Voucher</label> <input
																			id="stageChangeAccountingVoucher"
																			name='checksStageChangeAccountingVoucher'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">New Customer
																			Alias</label> <input id="newCustomerAlias"
																			name='checksNewCustomerAlias'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Chat Reply</label>
																		<input id="chatReply" name='checksChatReply'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Sales Pdf
																			Download</label> <input
																			id="salesPdfDownload"
																			name='checksSalesPdfDownload'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Visit Based
																			Transation</label>
																		<input id="visitBasedTransaction"
																			name='checksVisitBasedTransation"'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Sales
																			Management</label> <input
																			id="salesManagement"
																			name='checksSalesManagement"'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Receipts
																			Management</label> <input
																			id="receiptsManagement"
																			name='checksReceiptsManagement"'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Sales Edit
																			Enabled</label> <input id="salesEditEnabled"
																			name='checksSalesEditEnabled"'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Gps Variance
																			Query</label> <input id="gpsVarianceQuery"
																			name='checksGpsVarianceQuery"'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Send Sales Order
																			Email</label>
																		<input id="sendSalesOrderEmail"
																			name='checksSendSalesOrderEmail"'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Send Sales Order
																			Sap</label> <input id="sendSalesOrderSap"
																			name='checksSendSalesOrderSap"'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Pieces To
																			Quantity</label> <input
																			id="piecesToQuantity"
																			name='checksPiecesToQuantity"'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Send Sales Order
																			Odoo</label>
																		<input id="sendSalesOrderOdoo"
																			name='checksSendSalesOrderOdoo"'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Send Transactions
																			Sap
																			Pravesh</label> <input
																			id="sendTransactionsSapPravesh"
																			name='checksSendTransactionsSapPravesh"'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Add Compound
																			Unit</label> <input id="addCompoundUnit"
																			name='checksAddCompoundUnit"'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>


															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Update Stock
																			Location</label>
																		<input id="updateStockLocation"
																			name='checksUpdateStockLocation'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Send To
																			Odoo</label> <input id="sendToOdoo"
																			name='checksSendToOdoo' type='checkbox'
																			class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Product Group
																			Tax</label> <input id="productGroupTax"
																			name='checksProductGroupTax' type='checkbox'
																			class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Alias To
																			Name</label> <input id="aliasToName"
																			name='checksAliasToName' type='checkbox'
																			class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Description TO
																			Name</label> <input id="descriptionToName"
																			name='checksdescriptionToName'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Stock Api</label>
																		<input id="stockApi" name='checksstockApi'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Employee Create
																			Btn</label> <input id="employeeCreateBtn"
																			name='checksemployeeCreateBtn'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Modern Special
																			Config</label>
																		<input id="modernSpecialConfig"
																			name='checksmodernSpecialConfig'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Sale Order
																			status</label> <input id="salesOrderStatus"
																			name='checksmodernSpecialConfig'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Update
																			Receipt</label> <input id="updateReciept"
																			name='checksmodernSpecialConfig'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">SendToFocus</label>
																		<input id="sendToFocus"
																			name='checksmodernSpecialConfig'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Send SalesOrder
																			Email Automatically</label> <input
																			id="sendEmailAutomatically"
																			name='checksendEmailAutomatically'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Crm Enabled</label>
																		<input id="CrmEnabled" name='checkCrmEnabled'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Outstanding
																			DateWise Sorting</label> <input
																			id="outstandingDateSorting"
																			name='checkOutstandingDateSorting'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label"> Disable Stock
																			Calculations</label> <input
																			id="enableStockCalculations"
																			name='checkenableStockCalculations'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label"> Enable Kilometer
																			Calculations</label> <input
																			id="enablekiloCalc"
																			name='checkEnablekiloCalc' type='checkbox'
																			class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Enable
																			OutStanding</label>
																		<input id="enableOutStanding"
																			name='checksEnableOutStanding"'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-group">
																		<label class="control-label">Enable
																			Distance Slab Calculation </label>
																		<input id="enableDistanceSlabCalc"
																			name='checksenableDistanceSlabCalc'
																			type='checkbox' class="form-control" />
																	</div>
																</td>
															</tr>
															<tr>
                                                            	<td>
                                                            		<div class="form-group">
                                                            			<label class="control-label">Enable New DashBoard </label>
                                                            			<input id="enableNewDashboard" name='checksenableNewDashboard'
                                                            			type='checkbox' class="form-control" />
                                                            		</div>
                                                            	</td>
                                                            </tr>
														</table>
													</div>
												</div>
												<label class="error-msg" style="color: red;"></label>
											</div>
											<div class="modal-footer">
												<input class="btn btn-success" type="button"
													id="btnSaveCompanyConfigurations" value="Save" />
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
											<div class="modal-body" id="alertMessage" style="font-size: large;"></div>
											<div class="modal-footer">
												<button id="btnDelete" class="btn btn-danger"
													data-dismiss="modal">Ok</button>
												<button class="btn btn-info" data-dismiss="modal">Close</button>
											</div>
										</div>
										<!-- /.modal-content -->
									</div>
									<!-- /.modal-dialog -->
								</div>

							</div>
						</div>
						<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

						<spring:url value="/resources/app/company-configuration.js" var="companyConfigurationJs">
						</spring:url>
						<script type="text/javascript" src="${companyConfigurationJs}"></script>

						<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js" var="jqueryUI"></spring:url>
						<script type="text/javascript" src="${jqueryUI}"></script>
					</body>

					</html>