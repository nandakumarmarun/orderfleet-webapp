<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Static Js Code</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Billing Js Code</h2>
			<div class="row">

				<div class="clearfix"></div>
				<hr />
				<div class="col-md-12">
					<div class="modal-body">
						<div class="alert alert-danger alert-dismissible" role="alert"
							style="display: none;">
							<button type="button" class="close" onclick="$('.alert').hide();"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<p></p>
						</div>
						<div class="form-group">
							<label class="control-label">Company</label> <select
								onchange="StaticJsCode.onChangeDocument();" id="dbCompany"
								class="form-control selectpicker" data-live-search="true">
								<option value="no">Select Company</option>
								<c:forEach items="${companies}" var="company">
									<option value="${company.pid}">${company.legalName}</option>
								</c:forEach>
							</select>
						</div>
						<div class="form-group">
							<label class="control-label">JS Code</label>
							<textarea class="form-control" maxlength="15000" id="txtJsCode" rows="15"
								placeholder="JS Code"></textarea>
						</div>
					</div>
					<div class="modal-footer">
						<button id="myFormSubmit" class="btn btn-primary">Save</button>
					</div>
				</div>
			</div>
			<hr />

			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/billing-js-code.js" var="staticJsCode"></spring:url>
	<script type="text/javascript" src="${staticJsCode}"></script>
</body>
</html>