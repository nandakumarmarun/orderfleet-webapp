<%@ taglib prefix="formElement"
	uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Sub Form Elements</title>
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
			<h2>Sub Form Elements</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">

						<div class="form-group">
							<div class="col-sm-2">
								Documents<select id="dbDocuments"
									name="form" class="form-control">
									<option value="-1">--Select--</option>
									<c:forEach items="${documents}" var="document">
										<option value="${document.pid}">${document.name}</option>
									</c:forEach>
								</select>
							</div>
						
							<div class="col-sm-2">
								Forms<select id="dbForms"
									name="form" class="form-control">
									<option value="-1">--Select--</option>
									<%-- <c:forEach items="${forms}" var="form">
										<option value="${form.pid}">${form.name}</option>
									</c:forEach> --%>
								</select>
							</div>
							
							<div class="col-sm-2">
								Form Element Types<select id="dbFormElementTypes"
									name="formElementType" class="form-control">
									<!-- <option value="-1">--Select--</option> -->
									<c:forEach items="${formElementTypes}" var="formElementType">
										<option value="${formElementType.id}">${formElementType.name}</option>
									</c:forEach>
								</select>
							</div>

							<div class="col-sm-2">
								Form Elements<select id="dbFormElements" name="formElementPid"
									class="form-control">
									<option value="-1">--Select--</option>
								</select>
							</div>

							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-info" id="btnApply">Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Values</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody id='tblFormElementValues'>

				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/formElements" var="urlFormElement"></spring:url>

			<div class="modal fade container" id="subFormElementModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content" style="width: 120%">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Sub Form
								Elements</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divFormElements">
									<div class="row">
										<div class="col-md-12 col-sm-12 clearfix">
											<input type="radio" value="all" name="filter">
											&nbsp;All&nbsp;&nbsp; <input type="radio" value="selected"
												name="filter"> &nbsp;Selected&nbsp;&nbsp; <input
												type="radio" value="unselected" name="filter">
											&nbsp;Unselected&nbsp;&nbsp;

											<button type="button" class="btn btn-info" id="btnSearch"
												style="float: right;">Search</button>
											<input type="text" id="search" placeholder="Search..."
												class="form-control" style="width: 200px; float: right;">
										</div>
									</div>
									<br>
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><label><input type="checkbox"
														class="allcheckbox" value="">All</label></th>
												<th>Name</th>
												<th>Form Element Type</th>
											</tr>
										</thead>
										<tbody id="tBodyFormElement">

										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveFormElements" value="Save" />
							<span id="spFormElements" style="visibility: hidden;">Saving sub form elements..</span>
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

	<spring:url value="/resources/app/sub-form-element.js"
		var="subFormElementJs"></spring:url>
	<script type="text/javascript" src="${subFormElementJs}"></script>
</body>
</html>