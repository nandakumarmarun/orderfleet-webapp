<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>

			<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util" %>

				<html lang="en">

				<head>
					<jsp:include page="../fragments/m_head.jsp"></jsp:include>
					<title>SalesNrich | Users</title>
				</head>

				<style type="text/css">
					.error {
						color: red;
					}

					.btndlt {
						width: 71px;
						justify-content: center;
					}

					.action {
						display: flex;
						align-items: center;
						justify-content: center;
					}
				</style>

				<body class="page-body" data-url="">

					<div class="page-container">
						<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>

						<div class="main-content">
							<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>

							<hr />

							<h2>Billing Slabs</h2>
							<hr />

							<div class="row col-xs-6">
								<div class="pull-left">
									<button type="button" style="height:35px;" class="btn btn-success"
										onclick="Slab.showModalPopup($('#myModal'));">Create new Slab</button>
								</div>
							</div>

							<form class="form-inline">
								<div class="form-group">
									<div class="input-group">
										<input type="text" id="search" placeholder="Search..."
											class="form-control" style="width: 400px; height:35px;"><span
											class="input-group-btn">
											<button type="button" class="btn btn-info" style="height:35px;" id="btnSearch"
												style="float: right;">Search</button>
										</span>
									</div>
								</div>
							</form>

							<div class="row col-xs-12">
							</div>

							<div class="clearfix"></div>
							<hr />
							<div class="table-responsive">
								<table class=" collaptable table table-striped table-bordered">
									<thead>
										<tr>
											<th colspan='4'>Company</th>
										</tr>
									</thead>
									<tbody id="tBodyUser">

									</tbody>
								</table>
							</div>


							<hr />
							<!-- Footer -->
							<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

							<spring:url value="Web/billing-slabs/save-slab" var="urlUser"></spring:url>

							<form id="userForm" role="form" method="post" action="${urlUser}">
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

												<h4 class="modal-title" id="myModalLabel">Create slabs</h4>

											</div>

											<div class="modal-body">

												<div class="alert alert-danger alert-dismissible" role="alert"
													style="display: none;">

													<button type="button" class="close" onclick="$('.alert').hide();"
														aria-label="Close">
														<span aria-hidden="true">&times;</span>
													</button>
													<p></p>

												</div>

												<div class="modal-body" style="overflow: auto;">

													<div class="form-group">
														<label class="control-label" for="field_company">Company</label>
														<select id="field_company" name="companyPid"
															class="form-control selectpicker" data-live-search="true">
															<option value="-1">Select
																Company</option>
															<c:forEach items="${companies}" var="company">
																<option value="${company.pid}">${company.legalName}
																</option>
															</c:forEach>
														</select>
													</div>

													<div class="form-group">
														<label class="control-label" for="field_min_user">Minimum
															Users</label> <input type="text" class="form-control"
															name="minUser" id="field_min_user" maxlength="55"
															placeholder="Enter Minimum user" />
													</div>

													<div class="form-group">
														<label class="control-label" for="field_max_user">Maximum
															Users</label> <input autofocus="autofocus" type="text"
															class="form-control" name="maxUser" id="field_max_user"
															maxlength="255" placeholder="Enter Maximum user" />
													</div>

													<div class="form-group">
														<label class="control-label" for="field_slab_rate">Slab
															Rate</label> <input autofocus="autofocus" type="text"
															class="form-control" name="slabRate" id="field_slab_rate"
															maxlength="255" placeholder="Enter Slab Rate" />
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

							<form id="deleteForm" name="deleteForm" action="${urlUser}">
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
													<p>Are you sure you want to delete this User?</p>
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

					<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js" var="aCollapTable">
					</spring:url>
					<script type="text/javascript" src="${aCollapTable}"></script>
					<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
					<spring:url value="/resources/app/billingslab.js" var="momentJs"></spring:url>
					<spring:url value="/resources/assets/js/moment.js" var="userManagementJs"></spring:url>
					<script type="text/javascript" src="${userManagementJs}"></script>
					<script type="text/javascript" src="${momentJs}"></script>
				</body>

				</html>