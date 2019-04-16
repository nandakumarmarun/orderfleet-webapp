<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Root Plan Details</title>
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
			<h2>Root Plan Details</h2>
			<div class="row col-xs-12">
			<div class="pull-left">
			<select id="rootPlanHeader" name="rootPlanHeader" class="form-control" onchange="RootPlanDetail.loadTasklistByHeader();">
											<option value="-1">Select Root Plan Header</option>
											<c:forEach items="${rootPlanHeaders}" var="rootPlanHeader">
											<option value="${rootPlanHeader.pid}">${rootPlanHeader.name}</option>
											</c:forEach>
										</select></div>
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="RootPlanDetail.showModalPopup($('#myModal'),'value',3);">Create new
						Root Plan Detail</button>
				</div>
			</div>
		
			<div class="clearfix"></div>
			
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>TaskList</th>
						<th>Root Plan Order</th>
					</tr>
				</thead>
				<tbody id="detailWithOrder">
					<c:forEach items="${rootPlanDetails}" var="rootPlanDetail"
						varStatus="loopStatus">
						<%-- <tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${rootPlanDetail.name}</td>
							<td>${rootPlanDetail.fromDate}</td>
							<td>${rootPlanDetail.toDate}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="RootPlanDetail.showModalPopup($('#viewModal'),'${rootPlanDetail.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="RootPlanDetail.showModalPopup($('#myModal'),'${rootPlanDetail.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="RootPlanDetail.showModalPopup($('#deleteModal'),'${rootPlanDetail.pid}',2);">Delete</button>
							</td>
						</tr> --%>
					</c:forEach>
					
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/root-plan-details" var="urlRootPlanDetail"></spring:url>

			<form id="rootPlanDetailForm" role="form" method="post" action="${urlRootPlanDetail}">
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
									RootPlanDetail</h4>
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
										<label class="control-label" for="field_rootPlanHeader">Root Plan Header</label> <select id="field_rootPlanHeader"
											name="rootPlanHeader" class="form-control">
											<option value="-1">Select Root Plan Header</option>
											<c:forEach items="${rootPlanHeaders}" var="rootPlanHeader">
											<option value="${rootPlanHeader.pid}">${rootPlanHeader.name}</option>
											</c:forEach>
										</select>
									</div>
										<table class='table table-striped table-bordered'
										id="allTaskList" style="overflow: auto;">
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">
													All</th>
												<th>TaskList</th>
												<th>Schedule Order</th>
											</tr>
										</thead>
										<tbody id="tblTaskList">
											
										</tbody>
									</table>
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

			<form name="viewForm" role="form">
				<!-- Model Container-->
				<div class="modal fade container" id="viewModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-detail">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="viewModalLabel">RootPlanDetail</h4>
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

									<dl class="dl-horizontal">
										<dt>
											<span>Name</span>
										</dt>
										<dd>
											<span id="lbl_name"></span>
										</dd>
										<hr />
										<dt>
											<span>From Date</span>
										</dt>
										<dd>
											<span id="lbl_fromDate"></span>
										</dd>
										<hr />
										<dt>
											<span>To Date</span>
										</dt>
										<dd>
											<span id="lbl_toDate"></span>
										</dd>
										<hr />
									</dl>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

			<form id="deleteForm" name="deleteForm" action="${urlRootPlanDetail}">
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
									<p>Are you sure you want to delete this Root Plan Detail?</p>
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

	<spring:url value="/resources/app/root-plan-detail.js" var="rootPlanDetailJs"></spring:url>
	<script type="text/javascript" src="${rootPlanDetailJs}"></script>
	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>
</body>
</html>