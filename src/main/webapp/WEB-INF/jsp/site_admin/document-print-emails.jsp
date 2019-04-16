
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>
<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>Document Print Email</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Document Print Email</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="DocumentPrintEmail.showModalPopup($('#addModal'));">Create new Document Print Email</button>
				</div>
			</div>

			<div class="col-md-6 col-md-offset-3">
								<div class="form-group">
									<select id="field_company" name="companyPid"
										class="form-control"><option value="-1">Select
											Company</option>
										<c:forEach items="${companies}" var="company">
											<option value="${company.pid}">${company.legalName}</option>
										</c:forEach>
									</select>
								</div>
							</div>

			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						
						<th>Document</th>
						<th> Name</th>
						<th>Print File Path</th>
						<th>Email File Path</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody id="maintable">
				
				</tbody>
			</table>
			
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
<spring:url value="/web/document-print-email" var="urlDocumentPrintEmail"></spring:url>

<form id="documentPrintEmailForm" role="form" method="post"
				action="${urlDocumentPrintEmail}">
				<!-- Model Container-->
				<div class="modal fade container" id="addModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Create or edit a
									Document Print Email</h4>
							</div>
							<div class="modal-body" style="height: 500px; overflow: auto;">
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>
								
								<div class="form-group" id="companyselect">
									<select id="slt_field_company" class="form-control"><option
											value="-1">Select Company</option>
										<c:forEach items="${companies}" var="company">
											<option value="${company.pid}">${company.legalName}</option>
										</c:forEach>
									</select>
								</div>
								
								<div class="form-group" id="documentselect">
									<select id="field_document" 
										class="form-control"><option value="-1" id="docu">Select Document</option>
							<c:forEach items="${activityDocuments}" var="document">
								<option value="${document.pid}">${document.name}</option>
							</c:forEach>
									</select>
								</div>
									<div class="form-group">
										<label class="control-label" for="field_name">Name</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="name" id="field_name" maxlength="255"
											placeholder="Name" />
									</div>
								<div class="form-group">
									<label class="control-label" for="field_printFilePath">Print File Path</label> <input
										autofocus="autofocus" type="text" class="form-control"
										name="printFilePath" id="field_printFilePath" maxlength="255" placeholder="Print File Path" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_emailFilePath">Email File Path</label> <input
										type="text" class="form-control" name="emailFilePath" id="field_emailFilePath"
										maxlength="55" placeholder="Email File Path" />
								</div>

							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button id="myFormSubmit" class="btn btn-primary">Save</button>
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

	 <%-- <spring:url value="/resources/assets/js/joinable.js" var="joinable"></spring:url>
	<script type="text/javascript" src="${joinable}"></script> --%>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/document-print-email.js" var="documentPrintEmailJs"></spring:url>
	<script type="text/javascript" src="${documentPrintEmailJs}"></script>
</body>
</html>