<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Price Trend Configuration</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Price Trend Configuration</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<table id="tblConfig" class="table  table-striped table-bordered">
				<tbody>
					<tr>
						<td><input type="checkbox" name="price" value="price1"></td>
						<td>Price 1</td>
						<td><input type="text" id="txtprice1"></td>
					</tr>
					<tr>
						<td><input type="checkbox" name="price" value="price2"></td>
						<td>Price 2</td>
						<td><input type="text" id="txtprice2"></td>
					</tr>
					<tr>
						<td><input type="checkbox" name="price" value="price3"></td>
						<td>Price 3</td>
						<td><input type="text" id="txtprice3"></td>
					</tr>
					<tr>
						<td><input type="checkbox" name="price" value="price4"></td>
						<td>Price 4</td>
						<td><input type="text" id="txtprice4"></td>
					</tr>
					<tr>
						<td><input type="checkbox" name="price" value="price5"></td>
						<td>Price 5</td>
						<td><input type="text" id="txtprice5"></td>
					</tr>
					<tr>
						<td><input type="checkbox" id="remarks" value="remarks"></td>
						<td colspan="2">Remarks</td>
					</tr>
					<tr align="center">
						<td>&nbsp;</td>
						<td><button id="btnSaveConfig" class="btn btn-primary">Save</button></td>
						<td><label id="msg"></label></td>
					</tr>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/price-trend-configuration"
				var="urlPriceTrendConfiguration"></spring:url>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/price-trend-configuration.js"
		var="priceTrendConfiguration"></spring:url>
	<script type="text/javascript" src="${priceTrendConfiguration}"></script>
</body>
</html>