<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<title>SalesNrich | Activities</title>
<style type="text/css">
.error {
	color: red;
}

@media ( min-width : 1360px) and (max-width: 1370px) {
	.width83 {
		width: 83%
	}
}
</style>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<h2>Activities</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="col-sm-4">
					<security:authorize access="hasAnyRole('MASTER_DATA_MANAGER')">
						<button type="button" class="btn btn-success"
							onclick="Activity.showModalPopup($('#myModal'));"
							title="Create new">
							<i class="entypo-plus-circled"></i> Create
						</button>
					</security:authorize>
					<!-- <button type="button" class="btn " onclick=""
						title="import to table">
						<i class="entypo-upload-cloud"></i> Import
					</button> -->
				</div>

				<div class="col-sm-4">
					<div class="form-group col-sm-5">
						<select id="slt_status" class=" form-control  " title="filter">
							<option value="All">All</option>
							<option value="Active">Active</option>
							<option value="Deactive">Deactive</option>
							<option value="MultipleActivate">Multiple Activate</option>
						</select>
					</div>
				</div>

				<div class="col-sm-4 ">
					<div class="input-group">
						<input type="text" class="form-control" placeholder="Search...."
							id="searchText" style="height: 35px" /> <span
							class="input-group-addon btn btn-default" title="Search"
							id="btnMainSearch"> <i class=" entypo-search"
							style="font-size: 20px"> </i>
						</span>
					</div>
				</div>
			</div>
			<hr />

			<table class="table">
				<thead>
					<tr>
						<th>NAME</th>
						<th>ALIAS</th>
						<th>DESCRIPTION</th>
						<th>STATUS</th>
						<th>ACTIONS</th>
					</tr>
				</thead>
				<tbody id=activityTbody>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/activities" var="urlActivity"></spring:url>

			<form id="activityForm" role="form" method="post"
				action="${urlActivity}">
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
									Activity</h4>
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
									<label class="control-label" for="field_alias">Alias</label> <input
										type="text" class="form-control" name="alias" id="field_alias"
										maxlength="55" placeholder="Alias" />
								</div>
								<div class="form-group">
									<label class="control-label" for="contactManagement">Contact
										Management</label> <input type="text" class="form-control"
										name="contactManagement" id="contactManagement" maxlength="55"
										placeholder="Contact Management" readonly="readonly" />
								</div>
								<div class="form-group">
									<label class="control-label" for="hasDefaultAccount">Has
										Default Account ?</label> <input type="checkbox" class="form-control"
										name="hasDefaultAccount" id="hasDefaultAccount"
										style="width: 4%;" />
								</div>
								<div class="form-group">
									<label class="control-label" for="completePlans">Should
										Complete Plans ?</label> <input type="checkbox" class="form-control"
										name="completePlans" id="completePlans" style="width: 4%;" />
								</div>
								<div class="form-group">
									<label class="control-label" for="targetDisplayOnDayplan">Target
										Display On Dayplan</label> <input type="checkbox" class="form-control"
										name="targetDisplayOnDayplan" id="targetDisplayOnDayplan"
										style="width: 4%;" />
								</div>
								<div class="form-group">
									<label class="control-label" for="hasSecondarySales">Has
										Secondary Sales ?</label> <input type="checkbox" class="form-control"
										name="hasSecondarySales" id="hasSecondarySales"
										style="width: 4%;" />
								</div>
								<div class="form-group">
									<label class="control-label" for="geoFencing">Geo Fencing
										 </label> <input type="checkbox" class="form-control"
										name="geoFencing" id="geoFencing"
										style="width: 4%;" />
								</div>
								<div class="form-group">
									<label class="control-label" for="hasTelephonicOrder">Has Telephonic Order
										 </label> <input type="checkbox" class="form-control"
										name="hasTelephonicOrder" id="hasTelephonicOrder" 
										style="width: 4%;" />
								</div>
								<div class="form-group">
									<label class="control-label" for="emailTocomplaint">Email To Complaint
										 </label> <input type="checkbox" class="form-control"
										name="emailTocomplaint" id="emailTocomplaint" 
										style="width: 4%;" />
								</div>
								<div class="form-group">
									<label class="control-label" for="secondarySalesThroughApi">
										Secondary Sales Through Api ?</label> <input type="checkbox" class="form-control"
										name="secondarySalesThroughApi" id="secondarySalesThroughApi"
										style="width: 4%;" />
								</div>
								<div class="form-group">
                                	<label class="control-label" for="kmcalculationdisabled">
                                	Kilometer Calculation Disabled </label> <input type="checkbox" class="form-control"
                                	name="kmcalculationdisabled" id="kmcalculationdisabled"
                                	style="width: 4%;" />
                                </div>
                                <div class="form-group">
                                    <label class="control-label" for="autoTaskCreation">
                                    Auto Task Creation </label> <input type="checkbox" class="form-control"
                                    name="autoTaskCreationid" id="autotaskcreation"
                                    style="width: 4%;"/>
                                </div>
								<div class="form-group">
									<label class="control-label" for="field_locationRadius">Location
										Radius </label> <input type="number" class="form-control"
										name="locationRadius" id="field_locationRadius" max="10000"
										placeholder="Location Radius" />
								</div>


								<div class="form-group">
									<label class="control-label" for="field_description">Description</label>
									<textarea class="form-control" name="description"
										maxlength="250" id="field_description"
										placeholder="Description"></textarea>
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
								<h4 class="modal-title" id="viewModalLabel">Activity</h4>
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
										<span>Alias</span>
									</dt>
									<dd>
										<span id="lbl_alias"></span>
									</dd>
									<hr />
									<dt>
										<span>Description</span>
									</dt>
									<dd>
										<span id="lbl_description"></span>
									</dd>
									<hr />
								</dl>
								<table class="table  table-striped table-bordered"
									id="tblAccountTypes">
								</table>
								<table class="table  table-striped table-bordered"
									id="tblDocuments">
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

			<form id="deleteForm" name="deleteForm" action="${urlActivity}">
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
								<p>Are you sure you want to delete this Activity?</p>
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


			<div class="modal fade container" id="assignAccountTypesModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Account
								Types</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divAccountTypes">
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
												<th>Account Type</th>
												<th>Receiver/Supplier Type</th>
											</tr>
										</thead>
										<tbody id="tbodyAccountTypes">
											<c:forEach items="${accountTypes}" var="accountType">
												<tr>
													<td><input name='accountType' type='checkbox'
														value="${accountType.pid}" style="display: block;" /></td>
													<td>${accountType.name}</td>
													<td>${accountType.receiverSupplierType}</td>
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
								id="btnSaveAccountTypes" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>


			<div class="modal fade container" id="assignDocumentsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Documents</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divDocuments">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th></th>
												<th>Document</th>
												<th>Sort Order</th>
												<th>Required</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${documents}" var="document">
												<tr>
													<td><input name='document' type='checkbox'
														value="${document.pid}" /></td>
													<td>${document.name}</td>
													<td><input type="number" class="sortOrder"
														id="sortOrder${document.pid}" min="0" max="500"
														maxlength="3" value="0" /></td>
													<td><input id="required${document.pid}"
														name='required' type='checkbox' checked="checked" /></td>
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
								id="btnSaveDocuments" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="enableActivityModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable Activity</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>Activity</th>
											</tr>
										</thead>
										<tbody id="tblEnableActivity">
											<c:forEach items="${deactivatedActivity}" var="activity">
												<tr>
													<td><input name='activity' type='checkbox'
														value="${activity.pid}" /></td>
													<td>${activity.name}</td>
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
								id="btnActivateActivity" value="Activate" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>


			<div class="modal fade container" id="assignStagesModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Stages</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divStages">
									<div class="row">
										<div class="col-md-12 col-sm-12 clearfix">
											<input type="radio" value="all" name="filter">
											&nbsp;All&nbsp;&nbsp; <input type="radio" value="selected"
												name="filter"> &nbsp;Selected&nbsp;&nbsp; <input
												type="radio" value="unselected" name="filter">
											&nbsp;Unselected&nbsp;&nbsp;

											<button type="button" class="btn btn-info"
												id="btnStageSearch" style="float: right;">Search</button>
											<input type="text" id="searchStage" placeholder="Search..."
												class="form-control" style="width: 200px; float: right;">
										</div>
									</div>
									<br />
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">
													All</th>
												<th>Stages</th>
											</tr>
										</thead>
										<tbody id="tbodyStages">
											<c:forEach items="${stages}" var="stage">
												<tr>
													<td><input name='chkStage' type='checkbox'
														value="${stage.pid}" style="display: block;" /></td>
													<td>${stage.name}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveStages"
								value="Save" />
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

	<spring:url value="/resources/app/activity.js" var="activityJs"></spring:url>
	<script type="text/javascript" src="${activityJs}"></script>
</body>
</html>