<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Primer Calculator</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Primer Calculator</h2>

			<div class="clearfix"></div>
			<hr />
			<table style="width: 700px" class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Company Name</th>
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
							TATA
						</th>
					</tr>
				</thead>
				<tbody>
					<tr class="odd">
						<td>Litre</td>
						<td><input type="text" class="form-control" value="0" id="bag-wt-1"></td>
						<td><input type="text" class="form-control" value="0" id="bag-wt-2"></td>
						<td><input type="text" class="form-control" value="0" id="bag-wt-3"></td>
						<td><input type="text" class="form-control" value="0" id="bag-wt-4"></td>
						
					</tr>
					<tr class="odd">
						<td>Litre Rate(DP)</td>
						<td><input type="text" class="form-control" value="0" id="bag-rt-1"></td>
						<td><input type="text" class="form-control" value="0" id="bag-rt-2"></td>
						<td><input type="text" class="form-control" value="0" id="bag-rt-3"></td>
						<td><input type="text" class="form-control" value="0" id="bag-rt-4"></td>
						
					</tr>
					<tr class="odd">
						<td>Qty Discount in %</td>
						<td><input type="text" class="form-control" value="0" id="free-scheme-1"></td>
						<td><input type="text" class="form-control" value="0" id="free-scheme-2"></td>
						<td><input type="text" class="form-control" value="0" id="free-scheme-3"></td>
						<td><input type="text" class="form-control" value="0" id="free-scheme-4"></td>
						
					</tr>
					<tr class="even">
						<td>Discount Amount</td>
						<td id="per-kg-rate-1"></td>
						<td id="per-kg-rate-2"></td>
						<td id="per-kg-rate-3"></td>
						<td id="per-kg-rate-4"></td>
						
					</tr>
					<tr class="even">
						<td>Net Rate After Disc</td>
						<td id="free-bag-amt-1"></td>
						<td id="free-bag-amt-2"></td>
						<td id="free-bag-amt-3"></td>
						<td id="free-bag-amt-4"></td>
						<td id="free-bag-amt-5"></td>
						<td id="free-bag-amt-6"></td>
					</tr>
					
					<tr class="odd">
						<td>Selling Rate/Ltr</td>
						<td><input type="text" class="form-control" value="0" id="qty-disc-1"></td>
						<td><input type="text" class="form-control" value="0" id="qty-disc-2"></td>
						<td><input type="text" class="form-control" value="0" id="qty-disc-3"></td>
						<td><input type="text" class="form-control" value="0" id="qty-disc-4"></td>
						
					</tr>
					<tr style="color: white; background-color: #35aa47;">
						<td style="color:white">Margin Per Ltr</td>
						<td id="discount-amt-1"></td>
						<td id="discount-amt-2"></td>
						<td id="discount-amt-3"></td>
						<td id="discount-amt-4"></td>
						
					</tr>
				
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/primer-calculator" var="urlPrimerCalculator"></spring:url>


		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/primer-calculator.js" var="primerCalculatorJs"></spring:url>
	<script type="text/javascript" src="${primerCalculatorJs}"></script>
</body>
</html>