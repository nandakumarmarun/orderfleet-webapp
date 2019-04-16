<!DOCTYPE html>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<spring:url
	value="/resources/assets/css/font-icons/entypo/css/entypo.css"
	var="entypoCss"></spring:url>
<link href="${entypoCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/bootstrap.css"
	var="bootstrapCss"></spring:url>
<link href="${bootstrapCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/customDesign.css"
	var="customDesignCss"></spring:url>
<link href="${customDesignCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/neon-forms.css"
	var="neonFormsCss"></spring:url>
<link href="${neonFormsCss}" rel="stylesheet">

<spring:url value="/resources/assets/js/jquery-1.11.3.min.js"
	var="jQuery"></spring:url>
<script type="text/javascript" src="${jQuery}"></script>

<spring:url value="/resources/assets/js/jquery.validate.min.js"
	var="jQueryValidate"></spring:url>
<script type="text/javascript" src="${jQueryValidate}"></script>

<!-- img link -->
<spring:url value="/resources/assets/images/document1600.png" var="documentIcon"></spring:url>
<spring:url value="/resources/assets/images/Sales_rise-512.png" var="salesIcon"></spring:url>
<spring:url value="/resources/assets/images/246805-200.png" var="receiptIcon"></spring:url>
<spring:url value="/resources/assets/images/518164-backgrounds.jpg" var="backgroundImage"></spring:url>
</head>
<body style="background-color: #07575B">
	<div class="container" >
	<div class="col-sm-12" style="background-color: #003B46;margin-top: 15%">
	<div class="row">
	<div class="col-sm-4 form-group">
	</div>
	<div class="col-sm-4 form-group">
	<h3 style="color: #C4DFE6">Choose Your Plan</h3>
	</div>
	<div class="col-sm-4 form-group">
	</div>
	
	</div>
	</div>
		<div class="col-sm-12" style="background-color: #66A5AD;">
			<div class="row">
				<div class="col-sm-4 form-group">
					<input type="image" src="${salesIcon }" style="width: 40%;margin-left: 28%;margin-top: 12%;"/>
										<input type="button"  style="display: none;" onclick="SetUpTrial.loadCreateAccountPage(1);" />
										 <h6 style="color: black;margin-left: 37%;">Sales</h6>
				</div>
				<div class="col-sm-4 form-group">
					<input type="image" src="${receiptIcon }" style="width: 40%;margin-left: 28%;margin-top: 12%;"/>
										<input type="button" style="display: none;" onclick="SetUpTrial.loadCreateAccountPage(2);"/>
										 <h6 style="color: black;margin-left: 37%;">Receipt</h6>
				</div>
				<div class="col-sm-4 form-group">
					<input type="image" src="${documentIcon }" style="width: 40%;margin-left: 28%;margin-top: 12%;"/>
										<!-- <input type="button"  style="display: none;" onclick="SetUpTrial.loadCreateAccountPage(3);"/> -->
										<button style="display: block;" onclick="SetUpTrial.loadCreateAccountPage(3);"></button>
										 <h6 style="color: black;margin-left: 37%;">Dynamic</h6>
				</div>

			</div>
		</div>
	</div>
	
	<spring:url value="/resources/assets/js/bootstrap.js" var="bootstrapJs"></spring:url>
	<script src="${bootstrapJs}"></script>


	<spring:url value="/resources/assets/js/jquery.validate.min.js"
		var="jQueryValidate"></spring:url>
	<script type="text/javascript" src="${jQueryValidate}"></script>
	<spring:url value="/resources/app/set-up-trial.js" var="setUpTrialJs"></spring:url>
	<script type="text/javascript" src="${setUpTrialJs}"></script>
</body>
</html>