<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Stock Details</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Stock Details</h2>
			<%-- <div class="row col-xs-12">
				<security:authorize access="hasAnyRole('MASTER_DATA_MANAGER')">
					<div class="pull-right">
						<button type="button" class="btn btn-success"
							onclick="Document.showModalPopup($('#myModal'));">Create
							new Document</button>
					</div>
				</security:authorize>
			</div> --%>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
							<label class="control-label" for="dbEmployee">Employee</label>
								<select id="dbEmployee" name="employeePid" class="form-control">
									<!-- <option value="no">Select Employee</option> -->
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-1" style="margin-top: 23px">
								<button type="button" class="btn btn-info"
									id="btnApply">Apply</button>
							</div>
<!-- 							<div class="col-sm-2" style="margin-top: 23px">
								<button type="button" class="btn btn-success"
									id="btnPrint">Print</button>
							</div> -->
							<div class="col-sm-2" style="margin-top: 23px">
								<button type="button" class="btn btn-success"
									onclick = "window.print()">Print</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			
			<table id='tableStock' class="table  table-striped table-bordered" border="1" cellpadding="3">
				<thead>
					<tr>
						<th>Product</th>
						<th>Op Stk</th>
						<th>Sales(QTY)</th>
						<th>Closing Stock</th>
					</tr>
				</thead>
				<tbody id='tBodyStockDetails'>
					<c:forEach items="${stockDetails}" var="stockDetail"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${stockDetail.productName}</td>
							<td>${stockDetail.openingStock}</td>
							<td>${stockDetail.saledQuantity}</td>
							<td>${stockDetail.closingStock}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/stockDetails.js" var="stockDetailJs"></spring:url>
	<script type="text/javascript" src="${stockDetailJs}"></script>
</body>
</html>