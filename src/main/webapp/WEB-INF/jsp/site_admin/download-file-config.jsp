
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich |Download File Config</title>
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
			<hr />
			<h2>Download File Config</h2>
			<div class="row col-xs-12"></div>
			<hr>
			<div class="row">
				<div class="form-group">
					<div class="col-sm-3">
						Company<select id="dbCompany" name="companyPid"
							class="form-control selectpicker" data-live-search="true">
							<option value="no">All Companies</option>
							<c:forEach items="${companies}" var="company">
								<option value="${company.pid}">${company.legalName}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-3">
						Download Configuration for <select id="dbDownloadConfig" name="downloadConfigPid" class="form-control">
							<c:forEach items="${downloadConfig}" var="type">
								<option value="${type}">${type}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-3">
						<br />
						<button type="button" class="btn btn-orange" style="width: 65px;"
							onclick="DownloadFileConfig.getUserDeviceKey()">Load</button>
					</div>
				</div>
				<div class="col-sm-3">
					<div class="input-group">
						<input type="text" class="form-control" placeholder="Search"
							id="search" /> <span class="input-group-btn">
							<button class="btn btn-default" type="button" id="btnSearch">Search</button>
						</span>

					</div>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<form>
				<table class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th><input type="checkbox" class="allcheckbox">All</th>
							<th>Column</th>
							<th>Column Name</th>
							<th>Sort Order</th>
						</tr>
					</thead>
					<tbody id="tableUserDeviceKey">
						<c:forEach items="${downloadColumns}" var="colName">
							<tr>
								<td><input type="checkbox" class="chk" name="downloadConfiguration"></td>
								<td><label id="col-${colName}">${colName}</label></td>
								<td><input type="text" value="${colName}" id="colName-${colName}"></td>
								<td><input type="number" min="0" value="0" id="sort-${colName}"></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<div class="col-sm-10"></div>
				<div class="col-sm-2">
					<input class="btn btn-info btn-lg btn-outline" type="button"
						onClick="DownloadFileConfig.saveUserDeviceKey()" value="Update" />
				</div>
			</form>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/download-file-config.js"
		var="downloadFileConfigJs"></spring:url>
	<script type="text/javascript" src="${downloadFileConfigJs}"></script>
</body>
</html>