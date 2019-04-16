<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Day Plan Execution Config</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Day Plan Execution Config</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="modal-body">
				<div class="form-group">
					<div>
						<table class='table table-striped table-bordered'>
							<tbody>
								<tr>
									<th>Enabled</th>
									<th>Page</th>
									<th colspan="2">Order <input style="float: right;"
										class="btn btn-success" type="button" id="btnSave"
										value="Save" /></th>
								</tr>
							</tbody>
							<c:forEach items="${dayPlanExecutionConfigs}"
								var="dayPlanExecutionConfig">
								<tr>
									<td><input name='dayPlanExecutionConfig' type='checkbox'
										<c:if test="${dayPlanExecutionConfig.enabled eq true}">checked=checked</c:if>
										value="${dayPlanExecutionConfig.name}" /></td>
									<td>${dayPlanExecutionConfig.name}</td>
									<td><input type="text"
										value="${dayPlanExecutionConfig.sortOrder}"
										id="txtOrder${dayPlanExecutionConfig.name}" /></td>
									<td> <c:if test="${dayPlanExecutionConfig.id != null}"><button type="button" class="btn btn-white"
											onclick="DayPlanExecutionConfig.showModalPopup('${dayPlanExecutionConfig.id}');">
											Assign Users</button></c:if>  </td>

								</tr>
							</c:forEach>
						</table>
					</div>
				</div>
				<label class="error-msg" style="color: red;"></label>
			</div>
			<hr />
			<div class="modal-body">
				<table class='table table-striped table-bordered'>
					<tr>
						<td>ACCOUNT PURCHASE HISTORY DURATION(Month) ?</td>
						<td><select id="dbDuration" class="form-control">
								<option value="1">1</option>
								<option value="2">2</option>
								<option value="3">3</option>
								<option value="4">4</option>
								<option value="5">5</option>
								<option value="6">6</option>
						</select></td>
						<td><input style="float: right;" class="btn btn-success"
							type="button" id="btnSaveDuration" value="Save" /></td>
					</tr>
				</table>
			</div>


			<div class="modal fade container" id="assignUsersModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Users</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divUsers">
									<table class='table table-striped table-bordered'>
										<c:forEach items="${users}" var="user">
											<tr>
												<td><input name='user' type='checkbox'
													value="${user.pid}" /></td>
												<td>${user.firstName}</td>
											</tr>
										</c:forEach>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveUsers"
								value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/day_plan_execution_config.js"
		var="dayPlanExecutionConfigJs"></spring:url>
	<script type="text/javascript" src="${dayPlanExecutionConfigJs}"></script>
</body>
</html>