<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Customer Journey</title>
<style type="text/css">
.error {
	color: red;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="/WEB-INF/jsp/fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="/WEB-INF/jsp/fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<div class="row">
				<div class="col-md-6 col-sm-6 clearfix">
					<h2 id="title">Customer Journey</h2>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<div class="row" style="margin-top: 6%;">
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								Contacts <select id="dbAccount" name="accountPid"
									class="form-control selectpicker" data-live-search="true">
									<option value="-1">All Contacts</option>
									<c:forEach items="${accountProfiles}" var="accountProfile">
										<option value="${accountProfile.pid}">${accountProfile.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-1">
								<br />
								<button id="btnApply" type="button" class="btn btn-info">Apply</button>
							</div>
							<div class="col-sm-2">
								<br />
								<button type="button" class="btn btn-orange" id="btnDownload"
									title="download xlsx">
									<i class="entypo-download"></i> download
								</button>
							</div>
						</div>
					</form>
				</div>
			</div>

			<div class="clearfix"></div>
			<div class="table-responsive">
				<table class="table table-striped table-bordered"
					id="tblCustomerJourney">
					<thead id="tHeadCustomerJourney">
						<tr>
							<th>Date <i onclick="CustomerJourney.sortTable(0)"
								class="fa fa-sort float-right" aria-hidden="true"></i></th>
							<th>Contact</th>
							<th>Stage</th>
							<th>User</th>
							<th>Remarks</th>
							<th width="18%">Documents</th>
							<th>Amount</th>
						</tr>
					</thead>
					<tbody id="tBodyCustomerJourney">
						<tr>
							<td colspan='9' align='center'>Press apply button</td>
						</tr>
					</tbody>
				</table>
			</div>

			<!-- Model Container Dynamic Document-->
			<div class="modal fade custom-width" id="viewDetailsModal"
				style="display: none;">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 96%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">
								<b>Document Details</b>
							</h4>
						</div>
						<div class="modal-body">
							<!-- error message -->
							<div class="alert alert-danger alert-dismissible" role="alert"
								style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>
							<div class="row">
								<div class="col-md-12">
									<ul class="nav nav-tabs" role="tablist">
										<li class="active"><a href="#dd" role="tab"
											data-toggle="tab">Document Details</a></li>
										<!-- <li><a href="#attachments" role="tab" data-toggle="tab">Attachments</a></li> -->
									</ul>
									<div class="tab-content">
										<div class="tab-pane active" id="dd">
											<table class="table table-striped table-bordered">
												<tr>
													<td>Document Number</td>
													<td id="lbl_documentNumber"></td>
												</tr>
												<tr>
													<td>User</td>
													<td id="lbl_user"></td>
												</tr>
												<tr>
													<td>Account</td>
													<td id="lbl_account"></td>
												</tr>
												<tr>
													<td>Account Ph:</td>
													<td id="lbl_accountph"></td>
												</tr>
												<tr>
													<td>Activity</td>
													<td id="lbl_activity"></td>
												</tr>
												<tr>
													<td>Document</td>
													<td id="lbl_document"></td>
												</tr>
												<tr>
													<td>Date</td>
													<td id="lbl_documentDate"></td>
												</tr>
											</table>
											<div id="divDynamicDocumentDetails"
												style="overflow: auto; height: 300px;"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<hr />

			<!-- Footer -->
			<jsp:include page="/WEB-INF/jsp/fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="/WEB-INF/jsp/fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>
	
	<spring:url value="/resources/app/lead-to-sales/customer-journey.js"
		var="customerJourneyJs"></spring:url>
	<script type="text/javascript" src="${customerJourneyJs}"></script>
</body>
</html>