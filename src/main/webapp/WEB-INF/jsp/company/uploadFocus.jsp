<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Sync Focus Masters</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Sync DevSteel Masters</h2>
			
			<div class="row">
			<hr />
				
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="row">
							<div class="col-sm-3">
								<br>
								<button type="button" class="btn btn-success " id="uploadAll"
									style="width: 175px; text-align: center;">Upload Masters</button>
							</div>
							<div class="col-md-9">
								<div class="panel-body">
										<div class="form-group">
											<label class="error-msg" style="color: red;text-align:center;"></label>
										</div>
								</div>
							</div>
						</div>
					</form>
				</div>
				
				
				</div>
				
				<div class="table-responsive">
					<table class="table table-striped table-bordered">
						<thead>
							<tr>
								<th style="width:10%;"><input type="checkbox" id="selectAll" />&nbsp;&nbsp; Select All</th>
								<th>Masters</th>
							</tr>
						</thead>
						<tbody>
							 <tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="product_profiles"></input></td>
								<td>Product Profiles</td>
							</tr>
							
							<tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="account_profiles"></input></td>
								<td>Account Profiles</td>
							</tr>
							
							<tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="receivable_payable"></input></td>
								<td>Receivable Payable</td>
							</tr>
							
							<tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="brand_deva"></input></td>
								<td>branddeva</td>
							</tr>
							<tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="district"></input></td>
								<td>District</td>
							</tr>
							<tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="city"></input></td>
								<td>City</td>
							</tr>
							<tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="state"></input></td>
								<td>state</td>
							</tr>
							<tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="bussinessunit"></input></td>
								<td>BussinessUnit</td>
							</tr>
							<tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="routecode"></input></td>
								<td>RouteCode</td>
							</tr>
							<tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="lengthType"></input></td>
								<td>LengthType</td>
							</tr>
							<tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="contry"></input></td>
								<td>Country</td>
							</tr>
							<tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="customertype"></input></td>
								<td>CustomerType</td>
							</tr>
							<tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="unitmaster"></input></td>
								<td>UnitMaster</td>
							</tr>
							<tr>
								<td><input class="check-one" name='uploadMasters' type="checkbox" value="fiscalyear"></input></td>
								<td>FiscalYear</td>
							</tr>
						</tbody>
					</table>
				</div>
				
			<hr/>

			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>

	<spring:url value="/resources/assets/js/joinable.js" var="joinable"></spring:url>
	<script type="text/javascript" src="${joinable}"></script>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/upload-focus.js" var="uploaddevsteelJs"></spring:url>
	<script type="text/javascript" src="${uploaddevsteelJs}"></script>
</body>
</html>