<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Sales Value Report XLS Upload</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Sales Value Report XLS Uploadd</h2>
			<div class="row">
				<div class="clearfix"></div>
				<hr />
				<div class="col-md-12">
					<div class="panel-body">
						<form role="form" class="form-horizontal form-groups-bordered">
							<div class="form-group">
								<label class="col-sm-2 control-label">Sales Value Report
									File</label>
								<div class="col-sm-4">
									<input type="file" id="txtSalesUpload"
										class="form-control  inline btn btn-primary"
										name="accountFile"
										data-label="<i class='glyphicon glyphicon-circle-arrow-up'></i> &nbsp;Browse Xls Files" />
								</div>
								<button type="button" class="btn btn-success " id="uploadXLS">Sales
									Value Report XLS File</button>
							</div>
						</form>

						<form role="form" class="form-horizontal form-groups-bordered">
							<div class="form-group">
								<label class="col-sm-2 control-label">Product Intake
									Report File</label>
								<div class="col-sm-4">
									<input type="file" id="txtIntakeUpload"
										class="form-control  inline btn btn-primary"
										name="Intake"
										data-label="<i class='glyphicon glyphicon-circle-arrow-up'></i> &nbsp;Browse Xls Files" />
								</div>
								<button type="button" class="btn btn-success " id="uploadIntakeXLS">Product Intake Report XLS File</button>
							</div>
						</form>
					</div>
				</div>
			</div>
			<hr />

			<div class="modal fade container" id="alertBox">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body" id="alertMessage"
							style="font-size: large;"></div>
						<div class="modal-footer">
							<button class="btn btn-info" data-dismiss="modal">Close</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<%-- 	<spring:url value="/web/banks" var="urlBank"></spring:url> --%>
		</div>
	</div>

	<spring:url value="/resources/assets/js/joinable.js" var="joinable"></spring:url>
	<script type="text/javascript" src="${joinable}"></script>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/sales-value-report-upload-xls.js"
		var="uploadXlsJs"></spring:url>
	<script type="text/javascript" src="${uploadXlsJs}"></script>
</body>
</html>