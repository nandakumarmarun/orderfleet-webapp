<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | AccountNameTextSettings</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Account Name Text Settings</h2>

			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-info" id="btnDefValues">Load
						Values</button>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<div class="modal-body">
				<div class="form-group">
					<div>
						<table class='table table-striped table-bordered'>
							<thead>
								<tr>
									<th>Label</th>
									<th>Enabled</th>
								</tr>
							</thead>
							<tbody id="tbl_AccountText">
							<c:forEach items="${accountNameTextSettings}"
								var="accountNameText">
								<tr>
									<td>${accountNameText.name}</td>
									<td><input name='accountNameText' type='checkbox'
										<c:if test="${accountNameText.enabled eq true}">checked=checked</c:if>
										value="${accountNameText.pid}" /></td>

								</tr>
							</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<label class="error-msg" style="color: red;"></label>
			</div>
			<div class="modal-footer">
				<input class="btn btn-success" type="button" id="btnSave"
					value="Save" />
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/account-name-text-settings.js"
		var="activityGroupJs"></spring:url>
	<script type="text/javascript" src="${activityGroupJs}"></script>
</body>
</html>