<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>

<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich |Document Wise YTD Report</title>


</head>
<body class="page-body  skin-red">

	<div class="page-container">
		<!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->

		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">

			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>

			<hr />
			<!-- the contents starts here-->
			<h2>Document Wise YTD Report</h2>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								Document Type <select id="dbDocumentType" name="documentType" class="form-control">
									<!-- <option value="no">Select DocumentType</option> -->
									<c:forEach items="${voucherTypes}" var="voucherType">
										<option value="${voucherType}">${voucherType}</option>
									</c:forEach>
								</select>
							</div>

							<div class="col-sm-2">
								Document <select id="dbDocument" name="documentPid"
									class="form-control">
									<option value="no">Select Document</option>
								</select>
							</div>
							<br/>
							<div class="col-sm-1">
								<button type="button" id='btnApply' class="btn btn-info" >Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<br/>
			<div class="row">
				<div class="col-md-12 col-sm-12">
					<div>
						<div class="panel panel-primary" data-collapsed="0">
							<div class="panel-heading">
								<div class="panel-title">&nbsp;</div>

								<div class="panel-options">
									<a href="#" data-rel="collapse"><i class="entypo-down-open"></i></a>
								</div>
							</div>
							<div class="panel-body"
								style="max-width: 100%; overflow-x: scroll;">
								<table class="collaptable table table-striped table-responsive"
									style="border: 2px solid #EBEBEB;">
									<!--table header-->
									<thead>
										<tr
											style="font-weight: bold; background-color: rgb(195, 195, 195); color: black;"
											class="td-color">
											<th></th>
											<!-- <td>TC</td>
											<th>PC</th>
											<td>Efficiency(%)</td> -->
											<th></th>
											<th></th>
											<th></th>
											<th>Volume</th>
											<th>Amount</th>
										</tr>
										<tr style="font-weight: bold; color: brown;">
											<td>YTD</td>
											<!-- <td id="totalTC">0.0</td>
											<td id="totalPC">0.0</td>
											<td id="totalEfficiency">0.0</td> -->
											<td></td>
											<td></td>
											<td></td>
											<td id="totalVol">0.0</td>
											<td id="totalAmount">0.0</td>
										</tr>
										<tr style="font-weight: bold;">
											 <td>Month</td>
											<!--<td>TC</td>
											<td>PC</td>
											<td>Efficiency(%)</td> -->
											<td></td>
											<td></td>
											<td></td>
											<td>Volume</td>
											<td>Amount</td>
										</tr>
									</thead>
									<!--table header-->
									<tbody id="tblBody">

									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>

	<!--collapse table-->
	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

	<!-- bottom common scripts -->
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/document-wise-ytd-report.js"
		var="ytdReportJs"></spring:url>
	<script type="text/javascript" src="${ytdReportJs}"></script>

</body>
</html>