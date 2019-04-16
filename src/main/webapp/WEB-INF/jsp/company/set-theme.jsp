<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<title>SalesNrich | Set Theme</title>

<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<!-- jQuery UI-->
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
			<h2>Set Company Theme</h2>
			<div class="clearfix"></div>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success" id="mobilesModal">Save
						Company Theme</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />

			<div class="form-group">
				<label class="col-sm-5 control-label"></label>

				<div class="col-sm-5 ">
					<div class="radio radio-replace" id="white">
						<input type="radio" name="theme" value="white.css" > <label>white.css</label>
					</div>
					<br />
					<div class="radio radio-replace" id="black">
						<input type="radio" name="theme" value="black.css" > <label>black.css</label>
					</div>
					<br />
					<div class="radio radio-replace" id="blue">
						<input type="radio" name="theme" value="blue.css" > <label>blue.css</label>
					</div>
					<br />
					<div class="radio radio-replace" id="cafe">
						<input type="radio" name="theme" value="cafe.css" > <label>cafe.css</label>
					</div>
					<br />
					<div class="radio radio-replace"  id="facebook">
						<input type="radio" name="theme" value="facebook.css"> <label>facebook.css</label>
					</div>
					<br />
					<div class="radio radio-replace"  id="green">
						<input type="radio" name="theme" value="green.css"> <label>green.css</label>
					</div>
					<br />
					<div class="radio radio-replace" id="purple">
						<input type="radio" name="theme" value="purple.css" > <label>purple.css</label>
					</div>
					<br />
					<div class="radio radio-replace" id="red">
						<input type="radio" name="theme" value="red.css" > <label>red.css</label>
					</div>
					<br />
					<div class="radio radio-replace" id="yellow">
						<input type="radio" name="theme" value="yellow.css" > <label>yellow.css</label>
					</div>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<hr />
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/set-theme.js" var="companyThemeJs"></spring:url>
	<script type="text/javascript" src="${companyThemeJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
		var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>


</body>
</html>