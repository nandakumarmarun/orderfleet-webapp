<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<style>
* {
	box-sizing: border-box;
}

#myInput {
	width: 100%;
	font-size: 16px;
	padding: 12px 20px 12px 40px;
	border: 1px solid #ddd;
	margin-bottom: 12px;
}
</style>
<title>SalesNrich | Odoo Api Products</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Odoo Products</h2>
			<hr />
			<br> <br>

			<div class="clearfix"></div>
			<hr />
			<input class="form-control" type="text" id="myInput"
				onkeyup="myFunction()" placeholder="Search For Products By Name...">
			<table class="table  table-striped table-bordered" id="myTable">
				<thead>
					<tr>
						<th>Name</th>
						<th>Default Code</th>
						<th>Product Group</th>
						<th>Product Price</th>
						<th>Odoo Id</th>
						<th>Uom Id</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${products}" var="product" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${product.name}</td>
							<td>${product.default_code}</td>
							<td>${product.group}</td>
							<td>${product.standard_price}</td>
							<td>${product.id}</td>
							<td>${product.uom}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="addProduct('${product.name}','${product.id}');">Add
									Product</button>
							</td>
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

	<spring:url value="/resources/app/activity-group.js"
		var="activityGroupJs"></spring:url>
	<script type="text/javascript" src="${activityGroupJs}"></script>

	<script>
		var odooProductsContextPath = location.protocol + '//' + location.host
				+ location.pathname;
		function addProduct(productName, productId) {
			console.log(productName + "(" + productId + ")");

			if (confirm("''"
					+ productName
					+ "''  will be added to Product Profile.Please Sync Product Master and Stock Location From Odoo Once Again.")) {
				// update status;changeStatus

				$.ajax({
					url : odooProductsContextPath + "/addProduct",
					type : 'POST',
					data : {
						productName : productName,
						productId : productId
					},
					success : function(data) {

						window.location = odooProductsContextPath;
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
			}

		}

		function myFunction() {
			var input, filter, table, tr, td, i, txtValue;
			input = document.getElementById("myInput");
			filter = input.value.toUpperCase();
			table = document.getElementById("myTable");
			tr = table.getElementsByTagName("tr");
			for (i = 0; i < tr.length; i++) {
				td = tr[i].getElementsByTagName("td")[0];
				if (td) {
					txtValue = td.textContent || td.innerText;
					if (txtValue.toUpperCase().indexOf(filter) > -1) {
						tr[i].style.display = "";
					} else {
						tr[i].style.display = "none";
					}
				}
			}
		}
	</script>
</body>
</html>