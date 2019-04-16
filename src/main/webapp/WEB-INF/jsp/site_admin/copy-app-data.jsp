<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Copy Application Data</title>
<spring:url value="/resources/assets/css/bootstrap-multiselect.css"
	var="bootstrapMultiselectCss"></spring:url>
<link href="${bootstrapMultiselectCss}" rel="stylesheet">
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Copy User Data</h2>
			<div class="row">
				<div class="col-md-12">
					<select id="sbCompany" class="form-control"><option value="-1">Select a company</option>
						<c:forEach items="${companies}" var="company">
							<option value="${company.pid}">${company.legalName}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<br />
			<div class="row">
				<div class="col-md-4">
					<label class="control-label">Select Copy From User</label>
					<select class="form-control" id="sbFromUser"><option value="-1">Select a user</option></select>
				</div>
				<div class="col-md-4">
					<label class="control-label">Select Copy To Users</label>
					<select class="form-control" id="sbToUsers"
									multiple="multiple"></select>
				</div>
				<div class="col-md-4">
					<br />
					<button id="btnCopyUserData" class="btn btn-info">Copy data</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-primary">
						<div class="panel-heading">
							<div class="panel-title">Copy User Related Data</div>
							<div class="panel-options">
								<a href="#" data-rel="collapse"><i class="entypo-down-open"></i></a>
								<a href="#" data-rel="close" class="bg"><i
									class="entypo-cancel"></i></a>
							</div>
						</div>
						<div class="panel-body">
							<p>
								<input id="cbWebMobileMenuItems" type="checkbox" checked="checked" /><strong>Web &amp; Mobile Menu Items</strong>
							</p>
							<div class="bs-example bs-baseline-top">
								<form>
									<label class="checkbox-inline"> <input type="checkbox" name="cbWMMenuItems"
										value="webMenu" checked="checked">User Menu Items
									</label> <label class="checkbox-inline"> <input type="checkbox" name="cbWMMenuItems"
										value="mobileMenu" checked="checked">User Mobile Menu Item Group
									</label>
								</form>
							</div>
						</div>
						<div class="panel-body">
							<p>
								<input id="cbUserWiseFC" type="checkbox" checked="checked" /><strong>User Wise Feature Configuration</strong>
							</p>
							<div class="bs-example">
								<form>
									<label class="checkbox-inline"> <input type="checkbox" name="cbUserFC"
										value="activity" checked="checked">Activities
									</label> <label class="checkbox-inline"> <input type="checkbox" name="cbUserFC"
										value="document" checked="checked">Documents
									</label> <label class="checkbox-inline"> <input type="checkbox" name="cbUserFC"
										value="favouriteDocument" checked="checked">User Favourite Documents
									</label>
								</form>
							</div>
						</div>
						<div class="panel-body">
							<p>
								<input id="cbUserWiseDC" type="checkbox" checked="checked" /><strong>User Wise Data Configuration</strong>
							</p>
							<div class="bs-example">
								<form>
									<label class="checkbox-inline"> <input type="checkbox" name="cbUserDC"
										value="productGroup" checked="checked">Product Group Association
									</label> <label class="checkbox-inline"> <input type="checkbox" name="cbUserDC"
										value="productCategory" checked="checked">Product Category
										Association
									</label> <label class="checkbox-inline"> <input type="checkbox" name="cbUserDC"
										value="stockLocation" checked="checked">Stock Locations Association
									</label> <label class="checkbox-inline"> <input
										type="checkbox" value="priceLevel" checked="checked" name="cbUserDC">Price Level
										Association
									</label>
								</form>
							</div>
						</div>
						<div class="panel-body">
							<p>
								<input id="cbEmployeeManagement" type="checkbox" checked="checked" /><strong>Employee Management</strong>
							</p>
							<div class="bs-example">
								<form>
									<label class="checkbox-inline"> <input type="checkbox" name="cbEM"
										value="employeeUser" checked="checked">Create Employee &amp; Associate User
									</label>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	
	<spring:url value="/resources/assets/js/bootstrap-multiselect.js"
		var="bootstrapMultiselectJs"></spring:url>
	<script type="text/javascript" src="${bootstrapMultiselectJs}"></script>
	<spring:url value="/resources/app/copy-app-data.js"
		var="copyAppDataJs"></spring:url>
	<script type="text/javascript" src="${copyAppDataJs}"></script>
</body>
</html>