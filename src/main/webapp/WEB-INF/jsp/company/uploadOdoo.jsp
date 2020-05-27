<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Odoo Upload</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Odoo Upload</h2>
			<div class="row">
				
				<hr />
				<div class="col-md-12">
					<div class="panel-body">
						<form role="form" class="form-horizontal form-groups-bordered">
							<div class="form-group">
								<label class="col-sm-2 control-label">Account Profile</label>
								<button type="button" class="btn btn-success "
									id="uploadAccountProfiles">Upload Account Profiles</button>
							</div>	
						</form>
					</div>
				</div>

				<div class="col-md-12">
					<div class="panel-body">
						<form role="form" class="form-horizontal form-groups-bordered">
							<div class="form-group">
								<label class="col-sm-2 control-label">Product Profile</label>
								<button type="button" class="btn btn-success "
									id="uploadProductProfileProfiles">Upload Product Profiles</button>
							</div>
						</form>
					</div>
				</div>
				<div class="col-md-12">
					<div class="panel-body">
						<form role="form" class="form-horizontal form-groups-bordered">
							<div class="form-group">
								<label class="error-msg" style="color: red;"></label>
							</div>
						</form>
					</div>
				</div>
			</div>
			<hr />
		
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<%-- 	<spring:url value="/web/banks" var="urlBank"></spring:url> --%>
		</div>
	</div>

	<spring:url value="/resources/assets/js/joinable.js" var="joinable"></spring:url>
	<script type="text/javascript" src="${joinable}"></script>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/upload-odoo.js" var="uploadOdooJs"></spring:url>
	<script type="text/javascript" src="${uploadOdooJs}"></script>
</body>
</html>