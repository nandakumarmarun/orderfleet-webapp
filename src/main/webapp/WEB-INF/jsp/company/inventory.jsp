<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Inventory</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Inventory</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="col-md-12 col-sm-12 clearfix">
					<h1>TODO</h1>
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								<select id="sbEmployee" name="empPid" class="form-control">
									<option value="no">Employees</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${userPid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>
							<div id="divActivity" class="col-sm-2" style="display: none">
								<select id="sbActivity" name="activityPid" class="form-control">
									<option value="no">All Activity</option>
									<c:forEach items="${activities}" var="activity">
										<option value="${activity.pid}">${activity.name}</option>
									</c:forEach>
								</select>
							</div>
							<div id="divAccount" class="col-sm-2" style="display: none">
								<select id="sbAccount" name="accountPid" class="form-control">
									<option value="no">All Account</option>
									<c:forEach items="${accounts}" var="account">
										<option value="${account.pid}">${account.name}</option>
									</c:forEach>
								</select>
							</div>
							<div id="divDocument" class="col-sm-2" style="display: none">
								<select id="sbDocument" name="documentPid" class="form-control">
									<option value="no">All Document</option>
									<c:forEach items="${documents}" var="document">
										<option value="${document.pid}">${document.name}</option>
									</c:forEach>
								</select>
							</div>
							<br />
							<div id="divRemarks" style="display: none">
								<textarea id="txtRemarks" rows="4" cols="52"></textarea>
							</div>
  
							<!-- <div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick="Verification.filter()">Apply</button>
							</div> -->
						</div>
					</form>
				</div>
			</div>
			<br>
			<table class="collaptable table  table-striped table-bordered">
				<thead>
					<tr>
						<th>XXX</th>
						<th>XXXX</th>
					</tr>
				</thead>
				<!--table header-->
				<tbody id="tBodyInventory">

				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<%-- <spring:url value="/resources/app/inventory.js" var="inventoryJs"></spring:url>
	<script type="text/javascript" src="${inventoryJs}"></script>
 --%>
	<%-- <spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>
 --%>
</body>
</html>