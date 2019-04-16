<!DOCTYPE html>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>SalesNrich | OTP Verification</title>
<spring:url value="/resources/assets/css/bootstrap.css"
	var="bootstrapCss"></spring:url>
<link href="${bootstrapCss}" rel="stylesheet">
<style type="text/css">
.fah {
	min-height: 20px;
	padding: 19px;
	margin-bottom: 20px;
	background-color: white;
	border: 1px solid #e3e3e3;
	border-radius: 3px;
	-webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.05);
	box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.05);
	color: black;
}

.inputTextBox {
	display: block;
	width: 100%;
	height: 34px;
	padding: 6px 12px;
	font-size: 12px;
	line-height: 1.42857143;
	color: #555555;
	background-color: #fff;
	background-image: none;
	border: 1px solid #ebebeb;
	border-radius: 3px;
	-webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
	box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
	-webkit-transition: border-color ease-in-out .15s, box-shadow
		ease-in-out .15s;
	-o-transition: border-color ease-in-out .15s, box-shadow ease-in-out
		.15s;
	transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
}

.tabs-left {
	border-bottom: none;
	padding-top: 2px;
	border-right: 1px solid #ddd;
}

.tabs-left>li {
	float: none;
	margin-bottom: 2px;
	margin-right: -1px;
}

.tabs-left>li>a {
	border-radius: 4px 0 0 4px;
	margin-right: 0;
	display: block;
}

.tabs-left>li.active>a, .tabs-left>li.active>a:hover, .tabs-left>li.active>a:focus
	{
	border-bottom-color: #ddd;
	border-right-color: transparent;
}
</style>
<script type="text/javascript">
function validateOtp() 
{
	 var otp_val = document.getElementById("otp_value").value;
	   if(otp_val == null || otp_val === ""){
	   		alert("Enter OTP");
	   		return false; 
	   }else{
			return true; 
	   } 	
 }
</script>
</head>
<body>
	<div class="container">
		<div>
			<h1 style="text-align: center; color: black;">OTP VERIFICATION</h1>
			<div class="col-lg-3"></div>
			<div class="col-lg-6 fah">
				<div class="row">
					<spring:url value="/validateOtp" var="urlValidate"></spring:url>
					<form:form modelAttribute="registrationDto" action="${urlValidate}"
						method="get" onsubmit="validateOtp()">
						<div class="col-sm-12">
							<c:if test="${not empty msg}">
								<div class="alert alert-danger alert-dismissible" role="alert">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<strong>${msg}</strong>
								</div>
							</c:if>
							<br />
							<div class="form-group">
								<label><strong>Enter OTP</strong></label> 
								<input name="otpnum" type="number" id="otp_value" placeholder="Enter OTP" class="inputTextBox" required="required" />
							</div>
							<div class="col-sm-3 form-group">
								<form:button id="register" class="btn btn-lg btn-info">Validate</form:button>
							</div>
						</div>
					</form:form>
				</div>
			</div>
		</div>
	</div>
	<spring:url value="/resources/assets/js/jquery-1.11.3.min.js"
		var="jQuery"></spring:url>
	<script type="text/javascript" src="${jQuery}"></script>
</body>
</html>