<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Profit Calculator</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Profit Calculator</h2>

			<div class="clearfix"></div>
			<hr />
			<table style="width: 700px" class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Company</th>
						<th>
							<button type="button" class="btn btn-default" id="1">Clear</button><br><br>
							<input type="text" class="form-control" >
						</th>
						<th>
							<button type="button" class="btn btn-default" id="2">Clear</button><br><br>
							<input type="text" class="form-control" >
						</th>
						<th>
							<button type="button" class="btn btn-default" id="3">Clear</button><br><br>
							<input type="text" class="form-control" >
						</th>
						<th>
							<button type="button" class="btn btn-default" id="4">Clear</button><br><br>
							<input type="text" class="form-control" >
						</th>
						<th>
							<button type="button" class="btn btn-default" id="5">Clear</button><br><br>
							<input type="text" class="form-control" >
						</th>
						<th>
							<button type="button" class="btn btn-default" id="6">Clear</button><br><br>
							TATA
						</th>
					</tr>
				</thead>
				<tbody>
					<tr class="odd">
						<td>BAG WEIGHT</td>
						<td><input type="text" class="form-control" value="0" id="bag-wt-1"></td>
						<td><input type="text" class="form-control" value="0" id="bag-wt-2"></td>
						<td><input type="text" class="form-control" value="0" id="bag-wt-3"></td>
						<td><input type="text" class="form-control" value="0" id="bag-wt-4"></td>
						<td><input type="text" class="form-control" value="0" id="bag-wt-5"></td>
						<td><input type="text" class="form-control" value="0" id="bag-wt-6"></td>
					</tr>
					<tr class="even">
						<td>BAG RATE (DP)</td>
						<td><input type="text" class="form-control" value="0" id="bag-rt-1"></td>
						<td><input type="text" class="form-control" value="0" id="bag-rt-2"></td>
						<td><input type="text" class="form-control" value="0" id="bag-rt-3"></td>
						<td><input type="text" class="form-control" value="0" id="bag-rt-4"></td>
						<td><input type="text" class="form-control" value="0" id="bag-rt-5"></td>
						<td><input type="text" class="form-control" value="0" id="bag-rt-6"></td>
					</tr>
					<tr class="odd">
						<td>PER KG RATE</td>
						<td id="per-kg-rate-1"></td>
						<td id="per-kg-rate-2"></td>
						<td id="per-kg-rate-3"></td>
						<td id="per-kg-rate-4"></td>
						<td id="per-kg-rate-5"></td>
						<td id="per-kg-rate-6"></td>
					</tr>
					<tr class="even">
						<td>SCHEME- FREE BAG FOR 100 BAG</td>
						<td><input type="text" class="form-control" value="0" id="free-scheme-1"></td>
						<td><input type="text" class="form-control" value="0" id="free-scheme-2"></td>
						<td><input type="text" class="form-control" value="0" id="free-scheme-3"></td>
						<td><input type="text" class="form-control" value="0" id="free-scheme-4"></td>
						<td><input type="text" class="form-control" value="0" id="free-scheme-5"></td>
						<td><input type="text" class="form-control" value="0" id="free-scheme-6"></td>
					</tr>
					<tr class="odd">
						<td>FREE BAG AMOUNT</td>
						<td id="free-bag-amt-1"></td>
						<td id="free-bag-amt-2"></td>
						<td id="free-bag-amt-3"></td>
						<td id="free-bag-amt-4"></td>
						<td id="free-bag-amt-5"></td>
						<td id="free-bag-amt-6"></td>
					</tr>
					<tr class="even">
						<td>NET RATE AFTER FREE BAG FOR 4 MT</td>
						<td id="net-rate-4mt-1"></td>
						<td id="net-rate-4mt-2"></td>
						<td id="net-rate-4mt-3"></td>
						<td id="net-rate-4mt-4"></td>
						<td id="net-rate-4mt-5"></td>
						<td id="net-rate-4mt-6"></td>
					</tr>
					<tr class="odd">
						<td>QTY DISCOUNT IN %</td>
						<td><input type="text" class="form-control" value="0" id="qty-disc-1"></td>
						<td><input type="text" class="form-control" value="0" id="qty-disc-2"></td>
						<td><input type="text" class="form-control" value="0" id="qty-disc-3"></td>
						<td><input type="text" class="form-control" value="0" id="qty-disc-4"></td>
						<td><input type="text" class="form-control" value="0" id="qty-disc-5"></td>
						<td><input type="text" class="form-control" value="0" id="qty-disc-6"></td>
					</tr>
					<tr class="even">
						<td>DISCOUNT AMOUNT</td>
						<td id="discount-amt-1"></td>
						<td id="discount-amt-2"></td>
						<td id="discount-amt-3"></td>
						<td id="discount-amt-4"></td>
						<td id="discount-amt-5"></td>
						<td id="discount-amt-6"></td>
					</tr>
					<tr class="odd">
						<td>NET RATE  AFTER DISC </td>
						<td id="net-rate-aftr-disc-1"></td>
						<td id="net-rate-aftr-disc-2"></td>
						<td id="net-rate-aftr-disc-3"></td>
						<td id="net-rate-aftr-disc-4"></td>
						<td id="net-rate-aftr-disc-5"></td>
						<td id="net-rate-aftr-disc-6"></td>
					</tr>
					<tr class="even">
						<td>PER KG RATE</td>
						<td id="rate-per-kg-1"></td>
						<td id="rate-per-kg-2"></td>
						<td id="rate-per-kg-3"></td>
						<td id="rate-per-kg-4"></td>
						<td id="rate-per-kg-5"></td>
						<td id="rate-per-kg-6"></td>
					</tr>
					<tr class="odd">
						<td>SELLING RATE/BAG</td>
						<td><input type="text" class="form-control" value="0" id="sell-rate-per-bag-1"></td>
						<td><input type="text" class="form-control" value="0" id="sell-rate-per-bag-2"></td>
						<td><input type="text" class="form-control" value="0" id="sell-rate-per-bag-3"></td>
						<td><input type="text" class="form-control" value="0" id="sell-rate-per-bag-4"></td>
						<td><input type="text" class="form-control" value="0" id="sell-rate-per-bag-5"></td>
						<td><input type="text" class="form-control" value="0" id="sell-rate-per-bag-6"></td>
					</tr>
					<tr class="even">
						<td>SELLING RATE/KG</td>
						<td id="sell-rate-per-kg-1"></td>
						<td id="sell-rate-per-kg-2"></td>
						<td id="sell-rate-per-kg-3"></td>
						<td id="sell-rate-per-kg-4"></td>
						<td id="sell-rate-per-kg-5"></td>
						<td id="sell-rate-per-kg-6"></td>
					</tr>
					<tr style="color: white; background-color: #35aa47;">
						<td style="color:white">PROFIT PER KG</td>
						<td id="profit-per-kg-1"></td>
						<td id="profit-per-kg-2"></td>
						<td id="profit-per-kg-3"></td>
						<td id="profit-per-kg-4"></td>
						<td id="profit-per-kg-5"></td>
						<td id="profit-per-kg-6"></td>
					</tr>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/designations" var="urlDesignation"></spring:url>


		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/profit-calculator.js" var="profitCalculatorJs"></spring:url>
	<script type="text/javascript" src="${profitCalculatorJs}"></script>
</body>
</html>