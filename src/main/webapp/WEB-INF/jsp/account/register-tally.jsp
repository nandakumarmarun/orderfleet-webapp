<!DOCTYPE html>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html lang="en">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta name="description" content="Modern Admin Panel" />
<meta name="author" content="" />

<title>SalesNrich | Registration</title>

<spring:url value="/resources/assets/images/favicon.ico" var="favicon"></spring:url>
<link rel="icon" href="${favicon}">

<spring:url
	value="/resources/assets/css/font-icons/entypo/css/entypo.css"
	var="entypoCss"></spring:url>
<link href="${entypoCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/bootstrap.css"
	var="bootstrapCss"></spring:url>
<link href="${bootstrapCss}" rel="stylesheet">



<!-- CSS Theme -->
<spring:url value="/resources/web-assets/css/style.css" var="styleCss"></spring:url>
<link href="${styleCss}" rel="stylesheet">


<!-- CSS Header and Footer -->
<spring:url value="/resources/web-assets/css/headers/header-default.css"
	var="headerdefault"></spring:url>
<link href="${headerdefault}" rel="stylesheet">




<spring:url value="/resources/assets/css/neon-forms.css"
	var="neonFormsCss"></spring:url>
<link href="${neonFormsCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/white.css" var="facebookCss"></spring:url>
<link href="${facebookCss}" rel="stylesheet">

<spring:url value="/resources/assets/js/jquery-1.11.3.min.js"
	var="jQuery"></spring:url>
<script type="text/javascript" src="${jQuery}"></script>

<spring:url value="/resources/assets/js/jquery.validate.min.js"
	var="jQueryValidate"></spring:url>
<script type="text/javascript" src="${jQueryValidate}"></script>

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
#userName
	{
	background-color : #e2e2e2;
}
</style>
</head>
<body>

	
	
			<!--=== Header ===-->
		<div class="header">
			<div class="container">
				<!-- Logo -->
				<a class="logo" href="index"> <img
					src="/resources/web-assets/img/logo1.png" alt="Logo">
				</a>
				<!-- End Logo -->

				<!-- Topbar -->
				<div class="topbar">
					<ul class="loginbar pull-right">
						<!--
                    <li class="hoverSelector">
                        <i class="fa fa-globe"></i>
                        <a>Languages</a>
                        <ul class="languages hoverSelectorBlock">
                            <li class="active">
                                <a >English <i class="fa fa-check"></i></a>
                            </li>
                            <li><a >Spanish</a></li>
                            <li><a >Russian</a></li>
                            <li><a >German</a></li>
                        </ul>
                    </li>-->
						<li><a>+91 8893 499 106</a></li>
						<li class="topbar-devider"></li>
						<li><a>Help</a></li>
						<li class="topbar-devider"></li>
						<li><a href="../login">Login</a></li>
						<li class="topbar-devider"></li>
						<li><a href="../register">Register</a></li>
						
						
					</ul>
				</div>
				<!-- End Topbar -->

				<!-- Toggle get grouped for better mobile display -->
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-responsive-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="fa fa-bars"></span>
				</button>
				<!-- End Toggle -->
			</div>
			<!--/end container-->
			<!--<p style="float:left;margin-left:350px; margin-top:10px;font-size:16px;color:#09F;"> 
                    	Solutions For
                       <span class="glyphicon glyphicon-play"></span>
                        
                    </p>-->
			<!-- Collect the nav links, forms, and other content for toggling -->


			<div class="collapse navbar-collapse navbar-responsive-collapse">
				<div class="container">
					<ul class="nav navbar-nav ">
						<!--<li class="solutions"> 
                    	Solutions For
                       <span class="glyphicon glyphicon-play"></span>
                    </li>-->
						<!-- Pages -->
						<li class="active"><a href="index">Home</a></li>
						<li class="dropdown"><a href="javascript:void(0);"
							class="dropdown-toggle" data-toggle="dropdown"> Products </a>
							<ul class="dropdown-menu">
								<!--  <li class="active"><a href="index">Option 1: Default Page</a></li>-->
								<li class=""><a href="marketpro">Market Pro</a></li>
								<li class=""><a>BMet</a></li>
								<li class=""><a href="pharmaplus">Pharme Plus</a></li>
								<li class=""><a href="orderpro">Order Pro</a></li>
								<li class=""><a href="vansales">Van Sales</a></li>
								<li class=""><a>Sales360</a></li>

								<!-- End About Pages -->
							</ul></li>
						<!-- End Pages -->
						<!-- Pages -->
						<!-- <li><a >About Us</a></li> -->
						<li><a href="contactUs">Contact Us</a></li>
						<!-- End Pages -->
					</ul>
				</div>
				<!--/end container-->
			</div>
			<!--/navbar-collapse-->
		</div>
		<!--=== End Header ===-->


	<div class="container">
		<div>
			<h1 class="well" style="text-align: center; color: black; margin-top: 50px;">
			<c:if test="${not empty tallyConfig.tallyProductKey && not empty tallyConfig.tallyCompanyName }">
				Tally Integration for <strong>${tallyConfig.tallyCompanyName} </strong>
			</c:if>
			</h1>
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
					
				<spring:url value="/register-tally" var="urlRegister"></spring:url>
				 	<form:form id="tallyForm" modelAttribute="tallyConfig" action="${urlRegister}" method="post">
							<c:if test="${not empty tallyConfig.tallyProductKey  &&  not empty tallyConfig.tallyCompanyName }">
							
								<form:hidden path="pid" name="pid"/>
				
								<div class="well col-sm-12" >
									<div class="col-sm-6 form-group">
									<form:label path="tallyProductKey">
										<strong>Tally Serial.No : ${tallyConfig.tallyProductKey}</strong>
									</form:label>
									<form:hidden path="tallyProductKey" name="tallyProductKey"/>
									<font color='red'><form:errors path='tallyProductKey' /></font>
								</div>
								 <div class="col-sm-6 form-group">
									<form:label path="tallyCompanyName">
										<strong>Tally company Name : ${tallyConfig.tallyCompanyName}</strong>
									</form:label>
									<form:hidden path="tallyCompanyName" name="tallyCompanyName"/>
									<font color='red'><form:errors path='tallyCompanyName' /></font>
								</div>
								<div class="col-sm-6 form-group">
									<form:label path="companyName">
										<strong>SalesNrich company Name : ${tallyConfig.companyName}</strong>
									</form:label>
									<form:hidden path="companyPid" name="companyPid"/>
									<font color='red'><form:errors path='companyPid' /></font>
								</div>
								</div>
							</c:if>
							
							<div class="row" style="padding:10% ;">
								<div class="col-sm-2 form-group">
									
								</div>
								<div class="col-sm-3 form-group">
									<form:checkbox path="actualBillStatus" class="inputCheckBox" value="Java"/>
									Use separate actual and<br>&emsp; billed quantity columns 
								</div>
								<div class="col-sm-3 form-group">
									<form:checkbox path="orderNumberWithEmployee" class="inputCheckBox" value="Java"/>
									Add employee name <br>&emsp; with order number 
								</div>
								<div class="col-sm-3 form-group">
									<form:checkbox path="itemRemarksEnabled" class="inputCheckBox" value="Java"/>
									Enable item remarks<br>&emsp; in narration
								</div>
								<div class="col-sm-12 "><hr></div>
								<%-- <div class="col-sm-6 form-group">
									<form:label path="gstNames">
										<strong>Gst Ledger names </strong>&emsp;
										<strong>(separate with comma &emsp;",")</strong>
									</form:label>
									<form:input path="gstNames" name="gstNames"
										placeholder="CGST,SGST" class="inputTextBox" />
									<font color='red'><form:errors path='gstNames' /></font> 
								</div>
								<div class="col-sm-6 form-group">
									<form:label path="staticGodownNames">
										<strong>Default Godown Name :</strong>
									</form:label>
									<form:input path="staticGodownNames" name="staticGodownNames"
										placeholder="Main Location" class="inputTextBox" />
									<font color='red'><form:errors path='staticGodownNames' /></font> 
								</div> --%>
								<div class="col-sm-6 form-group">
									<form:label path="salesLedgerName">
										<strong>Sales Ledger Name :</strong>
									</form:label>
									<form:input path="salesLedgerName" name="salesLedgerName"
										placeholder="Sales" class="inputTextBox" 
										value="${not empty  tallyConfig.salesLedgerName ? tallyConfig.salesLedgerName : 'Sales'}"/>
									<font color='red'><form:errors path='salesLedgerName' /></font> 
								</div>
								<div class="col-sm-6 form-group">
									<form:label path="roundOffLedgerName">
										<strong>RoundOff Ledger Name :</strong>
									</form:label>
									<form:input path="roundOffLedgerName" name="roundOffLedgerName"
										placeholder="Round Off" class="inputTextBox" 
										value="${not empty  tallyConfig.roundOffLedgerName ? tallyConfig.roundOffLedgerName : 'Round Off'}"/>
									<font color='red'><form:errors path='roundOffLedgerName' /></font> 
								</div>
								<div class="col-sm-6 form-group">
									<form:label path="receiptVoucherType">
										<strong>Receipt Cash Ledger :</strong>
									</form:label>
									<form:input path="receiptVoucherType" name="receiptVoucherType"
										placeholder="Cash" class="inputTextBox" 
										value="${not empty  tallyConfig.receiptVoucherType ? tallyConfig.receiptVoucherType : 'Cash'}"/>
									<font color='red'><form:errors path='receiptVoucherType' /></font> 
								</div>
								<div class="col-sm-6 form-group">
									<form:label path="bankReceiptType">
										<strong>Bank Receipt Voucher Type :</strong>
									</form:label>
									<form:input path="bankReceiptType" name="bankReceiptType"
										placeholder="Bank" class="inputTextBox" 
										value="${not empty  tallyConfig.bankReceiptType ? tallyConfig.bankReceiptType : 'Receipt'}"/>
									<font color='red'><form:errors path='bankReceiptType' /></font> 
								</div>
								<div class="col-sm-6 form-group">
									<form:label path="bankName">
										<strong>Bank Name(for Bank Receipt Ledger):</strong>
									</form:label>
									<form:input path="bankName" name="bankName"
										placeholder="Bank Name" class="inputTextBox" 
										value="${not empty  tallyConfig.bankName ? tallyConfig.bankName : 'Bank Name'}"/>
									<font color='red'><form:errors path='bankName' /></font> 
								</div>
								<div class="col-sm-6 form-group">
									<form:label path="cashReceiptType">
										<strong>Cash Receipt Voucher Type :</strong>
									</form:label>
									<form:input path="cashReceiptType" name="cashReceiptType"
										placeholder="Cash" class="inputTextBox" 
										value="${not empty  tallyConfig.cashReceiptType ? tallyConfig.cashReceiptType : 'Receipt'}"/>
									<font color='red'><form:errors path='cashReceiptType' /></font> 
								</div>
								<div class="col-sm-6 form-group">
									<form:label path="transactionType">
										<strong>Transaction Type for Bank receipt :</strong>
									</form:label>
									<form:input path="transactionType" name="transactionType"
										placeholder="Cheque/DD" class="inputTextBox" 
										value="${not empty  tallyConfig.transactionType ? tallyConfig.transactionType : 'Cheque/DD'}"/>
									<font color='red'><form:errors path='transactionType' /></font> 
								</div>
								<div class="col-sm-6 form-group">
									<form:label path="transactionType">
										<strong>Post Dated Cheque voucher type :</strong>
									</form:label>
									<form:input path="pdcVoucherType" name="pdcVoucherType"
										placeholder="Receipt" class="inputTextBox" 
										value="${not empty  tallyConfig.pdcVoucherType ? tallyConfig.pdcVoucherType : 'Receipt'}"/>
									<font color='red'><form:errors path='pdcVoucherType' /></font> 
								</div>
								<div class="row" align="center" >
									<div class="col-sm-12 ">
										<form:button id="register" name="register"
											class="btn btn-lg btn-info"><strong>Register</strong></form:button>
									</div>
								</div>
							</div>
							
					</form:form>  
				</div>
			</div>
		</div>
	</div>

	<div id="myModal" class="modal fade" role="dialog">
		<div class="modal-dialog">

			<!-- Modal content-->
			<div class="modal-content">

				<div class="modal-body">
					<h3 style="color: green;">
						<strong> Account Create Successfully </strong>
					</h3>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" data-dismiss="modal"
						id="loginPage">Login</button>
				</div>
			</div>

		</div>
	</div>
	<script type="text/javascript">
	$(document).ready(function(){
		$("#alias").keyup(function(){
			var alias = $("#alias").val();
			$("#userName").val("admin@"+alias.toLowerCase());
		});
	});
	</script>
	<spring:url value="/resources/assets/js/bootstrap.js" var="bootstrapJs"></spring:url>
	<script src="${bootstrapJs}"></script>


	<spring:url value="/resources/assets/js/jquery.validate.min.js"
		var="jQueryValidate"></spring:url>
	<script type="text/javascript" src="${jQueryValidate}"></script>

	<spring:url value="/resources/app/set-up-trial.js" var="setUpTrialJs"></spring:url>
	<script type="text/javascript" src="${setUpTrialJs}"></script>

</body>
</html>