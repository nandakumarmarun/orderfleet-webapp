<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<title>SalesNrich | Task Execution Save Offline</title>

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
			<h2>Task Execution Save offline</h2>
			<div class="clearfix"></div>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						id="taskExecutionsModal">Create Task Execution Save
						Offline</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Company Name</th>
						<th>Save Offline</th>
						<!-- <th>Actions</th> -->
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${saveofflineCompany}" var="saveOffline"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${saveOffline.company.legalName}</td>
							<td>${saveOffline.value}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<%-- <div class="row-fluid">
				<util:pagination thispage="${syncOperations}"></util:pagination>
			</div> --%>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<hr />

			<div class="modal fade container" id="assignTaskExecutionsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Task
								Execution Save offline</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">

							<div class="col-md-6 col-md-offset-3">
								<div class="form-group">
									<select id="dbCompany" name="companyPid" class="form-control"><option
											value="-1">Select Company</option>
										<c:forEach items="${companies}" var="company">
											<option value="${company.pid}">${company.legalName}</option>
										</c:forEach>
									</select>
								</div>
							</div>

							<div class="form-group">
								<div id="divSyncOperations">
									<table class='table table-striped table-bordered'>




										<tr>
											<td><div class="form-group">
													<label class="control-label"> Save Offline</label> <input
														id="checked" name='check' type='checkbox' value="TRUE"
														class="form-control" />
												</div></td>
										</tr>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveTaskExecutions" value="Save" />
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

	<spring:url value="/resources/app/task-execution-save-offline.js"
		var="taskExecutionSaveOfflineJs"></spring:url>
	<script type="text/javascript" src="${taskExecutionSaveOfflineJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
		var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>


</body>
</html>