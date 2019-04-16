<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html lang="en">
<head>
	<jsp:include page="../fragments/m_head.jsp"></jsp:include>
	<title>SalesNrich | Add Mobile Users</title>
	
	<spring:url value="/resources/assets/css/add-mobile-user.css"
	var="addMobileUserCss"></spring:url>
	<link href="${addMobileUserCss}" rel="stylesheet">
</head>


<body class="page-body" data-url="">
<div id="loadingDiv"></div>
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			
			<h2>Add Mobile Users</h2>
			<hr />
			<!--the inner contents Ends here-->
			<div class="clearfix"></div>
			
			
			<div class="col-lg-12 fah">
				<div class="row">
				<c:if test="${not empty msg}">
					<div class="alert alert-danger alert-dismissible" role="alert">
						<button type="button" class="close"
							onclick="$('.alert').hide();" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<strong>${msg}</strong>
					</div>
				</c:if>
				<div class="col-sm-12">
					<div class="row" style="padding-left: inherit;">
						<%-- <div class="col-sm-4 form-group">
							<label class="control-label" for="field_snrich_product">SalesnRich Product</label>
							<select id="field_snrich_product" name="snrichProductPid" class="form-control">
							<option value="-1">Select Product</option>
								<c:forEach items="${snrichProducts}" var="snrichProduct">
									<option value="${snrichProduct.pid}">${snrichProduct.name}</option>
								</c:forEach>
							</select>
						</div> --%>
						<div class="col-sm-6 form-group">
							<label><strong>SalesnRich Product : ${snrichProduct.name}</strong></label>
							<input id="field_snrich_product" type="hidden" value="${snrichProduct.pid}">
						</div>
					</div>
					<div class="row" style="padding-left: inherit;">
						<div class="col-sm-4 form-group">
							<label class="control-label" for="field_paymentMode">Payment Mode</label> <select
								id="field_paymentMode" name="orderProPaymentMode" class="form-control">
								<!-- <option value="-1">Select Payment Mode</option> -->
								<c:forEach items="${orderProPaymentModes}" var="orderProPaymentMode">
									<c:choose>
										<c:when  test="${orderProPaymentMode == 'YEARLY'}">
											<option value="${orderProPaymentMode}" selected>${orderProPaymentMode}</option>
										</c:when>
										<c:otherwise>
											<option value="${orderProPaymentMode}">${orderProPaymentMode}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>
						</div>
						<div class="col-sm-4 form-group">
							<label><strong>Rate/User</strong></label>
							<input id="field_amountPerUser" class="inputTextBox" readonly/>
						</div>
						<div class="col-sm-4 form-group">
							<label><strong>GST @ 18%</strong></label>
							<input id="field_gstPerUser" class="inputTextBox" readonly/>
						</div>
					</div>
					<div class="row" style="padding-left: inherit;">
						<div class="col-sm-4 form-group">
							<label><strong>Number of Users</strong></label>
							<input type="number" min="1" id="field_numberOfUsers" class="inputTextBox" value="1"/>
						</div>
						<div class="col-sm-4 form-group">
							<label><strong>Total</strong></label>
							<input id="field_amountTotal" name="userName" class="inputTextBox" readonly/>
						</div>
						<div class="col-sm-4 form-group">
							<label><strong>GST @ 18%</strong></label>
							<input type="text" id="field_gstTotal" class="inputTextBox" readonly/>
						</div>
					</div>
					<br>
					<div class="row" style="padding-left: inherit;">
						<div class="col-sm-6 form-group">
							<button id="register" onclick="AddMobileUser.payment();"
								class="btn btn-lg btn-info"><strong>Add User</strong></button>
						</div>
						<div class="col-sm-6">
							<label style="font-size: medium;" id="lbl_grandTotal" class="pull-right"></label>
						</div>
					</div>
				</div>
				</div>
			</div>
			
			
			<!-- footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
		</div>
	</div>
	
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/add-mobile-user.js"
		var="addMobileUserJs"></spring:url>
	<script type="text/javascript" src="${addMobileUserJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

</body>
</html>