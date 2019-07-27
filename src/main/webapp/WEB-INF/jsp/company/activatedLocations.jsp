<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Activated Locations</title>
</head>
<style type="text/css">
.error {
	color: red;
}
</style>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Activated Locations</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="modal-body" style="overflow: auto; height: 500px">
				<div class="form-group">
					<div id="divLocations">
						<div class="row">
							<div class="col-md-12 col-sm-12 clearfix">
								<input type="radio" value="all" name="filter">
								&nbsp;All&nbsp;&nbsp; <input type="radio" value="selected"
									name="filter"> &nbsp;Selected&nbsp;&nbsp; <input
									type="radio" value="unselected" name="filter">
								&nbsp;Unselected&nbsp;&nbsp;

								<button type="button" class="btn btn-info"
									id="btnSearchLocations" style="float: right;">Search</button>
								<input type="text" id="searchLocations" placeholder="Search..."
									class="form-control" style="width: 200px; float: right;">
							</div>
						</div>
						<br>
						<table class='table table-striped table-bordered'>
							<thead>
								<tr>
									<th><label><input type="checkbox"
											class="allcheckbox" value="">All</label></th>
									<th>Name</th>
								</tr>
							</thead>
							<tbody id="tBodyLocation">
								<c:forEach items="${locations}" var="location">
									<tr>
										<td><input name='location' type='checkbox'
											value="${location.pid}" style="display: block;" /></td>
										<td>${location.name}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<label class="error-msg" style="color: red;"></label>
			</div>
			<div class="modal-footer">
				<input class="btn btn-success" type="button" id="btnSaveLocations"
					value="Save" />
				<button class="btn" data-dismiss="modal">Cancel</button>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/activatedLocation.js"
		var="activatedLocationJs"></spring:url>
	<script type="text/javascript" src="${activatedLocationJs}"></script>
</body>
</html>