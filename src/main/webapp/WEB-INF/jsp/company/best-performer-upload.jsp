<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Best Performer Upload</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2 id="title">Best Performer Upload</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<spring:url value="/web/best-performer-upload" var="urlCompany"></spring:url>
			<form id="bestPerformanceForm" role="form" method="POST"
				class="form-inline" action="${urlCompany}">
				<div class="form-group">
					<input type="hidden" value="${bestperformer.pid}"
						id="bestPerformerPid"> <input id="bestPerformerImageInput"
						type="file" style="display: initial">
					<button id="myFormSubmit" class="btn btn-success">Save</button>
					<br> <img id="previewImage" src=""
						style="max-height: 100px; margin-top: 18px; display: none;"
						alt="Image preview..."> <br>

				</div>
			</form>

			<img id="bestPerformerImage"
				style="max-height: 500px; max-width: 500px">
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>

	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/best-performer-upload.js"
		var="bestPerformerUploadJS"></spring:url>
	<script type="text/javascript" src="${bestPerformerUploadJS}"></script>

</body>
</html>