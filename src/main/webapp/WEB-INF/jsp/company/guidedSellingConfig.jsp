<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Guided Selling Configurations</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Guided Selling Configurations</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="modal-body">
				<div class="form-group">
					<div>
						<table class='table table-striped table-bordered'>
							<tbody>
								<tr>
									<td>Guided Selling Filter Items</td>
									<td><input id="txtGuidedSellingFilterItems"
										name='guidedSellingFilterItems' type='checkbox' /></td>
								</tr>
								<tr>
									<td>Favourite Product Group</td>
									<td><select id="dbFavouriteProductGroupPid"
										name="favouriteProductGroupPid" class="form-control"><option
												value="no">Select Favourite Product Group</option>
											<c:forEach items="${productGroups}" var="productGroup">
												<option value="${productGroup.pid}">${productGroup.name}</option>
											</c:forEach>
									</select></td>
								</tr>
								<tr>
									<td>Favourite Product Group Is Compulsory</td>
									<td><input id="txtFavouriteItemCompulsory"
										name='favouriteItemCompulsory' type='checkbox' /></td>
								</tr>
								<tr>
									<td>Promotion Product Group</td>
									<td><select id="dbPromotionProductGroupPid"
										name="promotionProductGroupPid" class="form-control"><option
												value="no">Select Promotion Product Group</option>
											<c:forEach items="${productGroups}" var="productGroup">
												<option value="${productGroup.pid}">${productGroup.name}</option>
											</c:forEach>
									</select></td>
								</tr>
								<tr>
									<td>Promotion Product Group Is Compulsory</td>
									<td><input id="txtPromotionItemCompulsory"
										name='promotionItemCompulsory' type='checkbox' /></td>
								</tr>
								<tr>
									<td>Guided Selling Info Document</td>
									<td><select id="dbGuidedSellingInfoDocumentPid"
										name="guidedSellingInfoDocumentPid" class="form-control"><option
												value="no">Select Guided Selling Info Document</option>
											<c:forEach items="${documents}" var="document">
												<option value="${document.pid}">${document.name}</option>
											</c:forEach>
									</select></td>
								</tr>
							</tbody>
						</table>
						<input style="float: right;" class="btn btn-success" type="button"
							id="btnSave" value="Save" />
					</div>
				</div>
				<label class="error-msg" style="color: red;"></label>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/guided_selling_config.js"
		var="guidedSellingConfigJs"></spring:url>
	<script type="text/javascript" src="${guidedSellingConfigJs}"></script>
</body>
</html>