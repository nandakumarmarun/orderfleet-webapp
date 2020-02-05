<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | AddGeoLocation</title>

<script
	src="https://maps.googleapis.com/maps/api/js?v=3.exp&key=AIzaSyC5Oej1-ZsPCIXgEIUhgcT3FEqZ32Y8VGA&signed_in=true&libraries=places">
</script>

<style type="text/css">
.error {
	color: red;
}

@media ( min-width : 1360px) and (max-width: 1370px) {
	.width83 {
		width: 83%
	}
}‹
</style>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Add Geo Location</h2>
			<form class="form-group">
				<div class="input-group col-md-10 width83" style="float: left;">
					<span class="input-group-addon btn btn-default"
						onclick="$(this).next('input').val('');$('#ofModalSearch').modal('show', {backdrop: 'static'});"><i
						class="glyphicon glyphicon-filter"></i></span> <input type="text"
						class="form-control" placeholder="Search" id="search"
						style="width: 200px;" />
					<button id="btnSearch" class="btn btn-info" type="button">Search</button>
				</div>
			</form>
			<div class="clearfix"></div>
			<hr />
			<br>
			<div class="table-responsive">
				<table class="table  table-striped table-bordered"
					id="tblAddGeoLocation">
					<thead>
						<tr>
							<th>Name</th>
							<th>Latitude</th>
							<th>Longitude</th>
							<th>Geo Location</th>
							<th>Tag Details</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody id="tBodyAddGeoLocation">
					</tbody>
				</table>
			</div>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<div class="modal fade custom-width" id="ofModalSearch"
				style="display: none;">
				<div class="modal-dialog" style="width: 96%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title">
								Search for: <b>Account Profile</b>
							</h4>
						</div>
						<div class="modal-body">
							<section class="search-results-env">
								<div class="row">
									<div class="col-md-12">
										<ul class="nav nav-tabs right-aligned">
											<li class="tab-title pull-left">
												<div>
													<button
														onclick="$('.search-results-panes').find('input[type=checkbox]:checked').removeAttr('checked');"
														type="button" class="btn btn-secondary">Clear All</button>
													<b>&nbsp;</b>
												</div>
											</li>
											<li class="active"><a href="#paccountType">Account
													Type</a></li>
											<!-- <li class="active"><a href="#pimportStatus">Import Status</a></li> -->
										</ul>
										<form class="search-bar">
											<div class="input-group">
												<input id="ofTxtSearch" type="text"
													class="form-control input-lg" name="search"
													placeholder="Type for search...">
												<div class="input-group-btn">
													<button class="btn btn-lg btn-primary btn-icon"
														style="pointer-events: none;">
														Search <i class="entypo-search"></i>
													</button>
												</div>
											</div>
										</form>
										<div class="form-group">
											<label class="control-label">Import Status</label>
											<div class="row">
												<div class="col-md-4">
													<label> <input type="radio" name="import"
														value="all" checked="checked"> All
													</label>
												</div>
												<div class="col-md-4">
													<label> <input type="radio" name="import"
														value="true"> Imported
													</label>
												</div>
												<div class="col-md-4">
													<label> <input type="radio" name="import"
														value="false"> Others
													</label>
												</div>
											</div>
										</div>
										<hr>
										<div class="search-results-panes">
											<div class="search-results-pane" id="paccountType"
												style="display: block;">
												<div class="row">
													<c:forEach items="${accountTypes}" var="accountType">
														<div class="col-md-4">
															<div class="checkbox">
																<label> <input type="checkbox"
																	value="${accountType.pid}">${accountType.name}
																</label>
															</div>
														</div>
													</c:forEach>
												</div>
											</div>

										</div>
									</div>
								</div>
							</section>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Close</button>
							<button type="button" class="btn btn-info"
								onclick="AddGeoLocation.filterByAccountType(); $('#ofModalSearch').modal('hide');">Apply</button>
						</div>
					</div>
				</div>
			</div>
			<!-- OF Modal Filter end -->

			<div class="modal fade container" id="viewModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable Account Profile</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<table class='table table-striped table-bordered'>
								<thead>
									<tr>
										<th>Old Latitude</th>
										<th>Old Longitude</th>
										<th>Old Geo Location</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td id="oldLatitude"></td>
										<td id="oldLongitude"></td>
										<td id="oldGeoLocation"></td>
									</tr>
								</tbody>
							</table>

							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th></th>
												<th>New Latitude</th>
												<th>New Longitude</th>
												<th>New Geo Location</th>
											</tr>
										</thead>
										<tbody id="tblAccountProfileGeoLocation">
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveNewGeoLocation" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="enableMapModal"
				style="width: 100%; z-index: 1041;">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 90%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<a href="#" data-rel="reload" id="fullscreen"
								data-toggle="tooltip" data-placement="top"
								data-original-title="Full Screen"><i class="entypo-window"></i></a>
						</div>
						<div class="modal-body" style="overflow: auto; height: 650px;">
							<table class='table table-striped table-bordered'>
								<thead>
									<tr>
										<th>old geo location</th>
										<th>new geo location</th>
									</tr>
									<tr>
										<td id="oldLocation"></td>
										<td id="newLocation"></td>
										<td id="newLat" style="display: none;"></td>
										<td id="newLng" style="display: none;"></td>
									</tr>
								</thead>
							</table>
							<div class="panel-body no-padding" style="height: 500px;">
								<div class="form-group">
									<input type="text" class="form-control" name="map_search"
										id="pac-input" placeholder="search..." />
								</div>
								<div id="map-canvas" style="height: 100%; width: 100%;"></div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveLocationName" value="Save" />
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

	<spring:url value="/resources/assets/js/jquery.table2excel.min.js"
		var="table2excelMin"></spring:url>
	<script type="text/javascript" src="${table2excelMin}"></script>

	<spring:url value="/resources/app/add-geo-location.js"
		var="addGeoLocationJs"></spring:url>
	<script type="text/javascript" src="${addGeoLocationJs}"></script>
</body>
</html>