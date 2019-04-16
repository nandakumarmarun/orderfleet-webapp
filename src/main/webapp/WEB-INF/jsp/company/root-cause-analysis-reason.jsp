<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Root Cause Analysis Reason</title>
<style type="text/css">
.error {
	color: red;
}
</style>
</head>
<body class="page-body" data-url="">
	
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>

			<h2>Root Cause Analysis Reason</h2>
			<hr />
			<div class="row">
				<div class="col-sm-4">
					<button type="button" class="btn btn-success"
						onclick="RootCauseAnalysisReason.showModalPopup($('#myModal'));"
						title="Create new">
						<i class="entypo-plus-circled"></i> Create
					</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr/>
			
			<table class="table table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Alias</th>
						<th>Description</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tbody_rootCauseAnalysisReason">
					<c:if test="${empty rootCauseAnalysisReasons}">
					<tr><td align="center" colspan = 4>No Data Available</td></tr>
					</c:if>
					<c:forEach items="${rootCauseAnalysisReasons}" var="rootCauseAnalysisReason"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${rootCauseAnalysisReason.name}</td>
							<td>${rootCauseAnalysisReason.alias}</td>
							<td>${rootCauseAnalysisReason.description}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="RootCauseAnalysisReason.showModalPopup($('#myModal'),'${rootCauseAnalysisReason.pid}',1);">
									Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="RootCauseAnalysisReason.showModalPopup($('#deleteModal'),'${rootCauseAnalysisReason.pid}',2);">
									Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
			<spring:url value="/web/root-cause-analysis-reason" var="urlRootCauseAnalysisReason"></spring:url>

			<form id="rootCauseAnalysisReasonForm" role="form" method="post"
				action="${urlRootCauseAnalysisReason}">
				<!-- Model Container-->
				<div class="modal fade container" id="myModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Create or edit a
									Root Cause Analysis Reason</h4>
							</div>
							<div class="modal-body">
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>

								<div class="modal-body" style="overflow: auto;">
									<div class="form-group">
										<label class="control-label" for="field_name">Name</label> 
										<input autofocus="autofocus" type="text" class="form-control"
											name="name" id="field_name" maxlength="255"
											placeholder="Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_alias">Alias</label> 
										<input type="text" class="form-control" name="alias"
											id="field_alias" maxlength="55" placeholder="Alias" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_description">Description</label> 
										<textarea class="form-control" name="description"
										id="field_description" placeholder="Description"></textarea>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button id="myFormSubmit" class="btn btn-primary">Save</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>
			
			<form id="deleteForm" name="deleteForm" action="${urlRootCauseAnalysisReason}">
				<!-- Model Container-->
				<div class="modal fade container" id="deleteModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title">Confirm delete operation</h4>
							</div>
							<div class="modal-body">

								<div class="modal-body" style="overflow: auto;">
									<!-- error message -->
									<div class="alert alert-danger alert-dismissible" role="alert"
										style="display: none;">
										<button type="button" class="close"
											onclick="$('.alert').hide();" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
										<p></p>
									</div>
									<p>Are you sure you want to delete this Root Cause Analysis Reason?</p>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button class="btn btn-danger">Delete</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>
		</div>
	</div>
	
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	
	<spring:url value="/resources/app/root-cause-analysis-reason.js" var="rootCauseAnalysisReasonJs"></spring:url>
	<script type="text/javascript" src="${rootCauseAnalysisReasonJs}"></script>
	
</body>
</html>