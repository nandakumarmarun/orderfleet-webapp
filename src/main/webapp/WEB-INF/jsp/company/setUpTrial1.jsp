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

<title>SalesNrich | Trial</title>

<spring:url value="/resources/assets/images/favicon.ico" var="favicon"></spring:url>
<link rel="icon" href="${favicon}">

<spring:url
	value="/resources/assets/css/font-icons/entypo/css/entypo.css"
	var="entypoCss"></spring:url>
<link href="${entypoCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/bootstrap.css"
	var="bootstrapCss"></spring:url>
<link href="${bootstrapCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/neon-forms.css"
	var="neonFormsCss"></spring:url>
<link href="${neonFormsCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/white.css" var="facebookCss"></spring:url>
<link href="${facebookCss}" rel="stylesheet">

<spring:url value="/resources/assets/js/jquery-1.11.3.min.js"
	var="jQuery"></spring:url>
<script type="text/javascript" src="${jQuery}"></script>

<spring:url value="/resources/assets/js/neon-login.js" var="login"></spring:url>
<script type="text/javascript" src="${login}"></script>

<spring:url value="/resources/assets/js/jquery.validate.min.js"
	var="jQueryValidate"></spring:url>
<script type="text/javascript" src="${jQueryValidate}"></script>

<%-- <spring:url value="/resources/assets/css/radio-button.css"
	var="radioButtonsCss"></spring:url>
<link href="${radioButtonsCss}" rel="stylesheet"> --%>
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
</head>
<body>

	<div class="container">
		<div>
			<h1 class="well" style="text-align: center; color: black;">Create
				Account</h1>
			<div class="col-lg-12 fah">
				<div class="row">
					<spring:url value="/web/trial/setup" var="urlCompanyTrialSetUp"></spring:url>

					<form id="companyTrialSetUpForm" role="form" method="post"
						action="${urlCompanyTrialSetUp}">

						<div class="alert alert-danger alert-dismissible" role="alert"
							style="display: none;">
							<button type="button" class="close" onclick="$('.alert').hide();"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<p></p>
						</div>
						<div class="col-sm-12">

							<div class="row" style="padding-left: inherit;">

								<div class="col-sm-6 form-group">
									<div class="form-group">
										<label> <strong>Full Name</strong> <small></small></label> <input
											type="text" placeholder="Full Name" class="inputTextBox"
											id="field_name">
									</div>
								</div>
							</div>
							<div class="row" style="padding-left: inherit;">
								<div class="col-sm-6 form-group">
									<label> <strong>Company Name</strong> <small></small></label> <input
										type="text" placeholder="Company Name" class="inputTextBox"
										id="field_companyName">
								</div>

							</div>
							<div class="row" style="padding-left: inherit;">
								<div class="col-sm-6 form-group">
									<label> <strong>Short Name</strong> <small></small></label> <input
										type="text" placeholder="Short Name" class="inputTextBox"
										id="field_shortName">
								</div>
							</div>
							<div class="row" style="padding-left: inherit;">
								<div class="col-sm-6 form-group">
									<label> <strong>Email <small>(This is
												your User Name)</small></strong></label> <input type="text"
										placeholder="Email Address" class="inputTextBox"
										id="field_email">

								</div>
							</div>
							<div class="row" style="padding-left: inherit;">
								<div class="col-sm-6 form-group">
									<label> <strong>Password <small>(This
												is your Password)</small></strong></label> <input type="password"
										placeholder="Password" class="inputTextBox"
										id="field_password">

								</div>
							</div>

							<div class="row" style="padding-left: inherit;">
								<div class="col-sm-6 form-group">
									<label> <strong>Country</strong> <small></small></label>
									<div class="form-group">
										<select id="field_country" class="inputTextBox">
											<option value="no">Country</option>
											<option value="IN">INDIA</option>
											<option value="AE">UAE</option>
										</select>
									</div>
								</div>
							</div>
							
							<div class="row" style="padding-left: inherit;">
								<div class="col-sm-6 form-group">
									<label> <strong>GST Number</strong> <small></small></label> <input
										type="text" placeholder="GST Number" class="inputTextBox"
										id="field_gstNo">
								</div>
							</div>

							<div class="row" style="padding-left: inherit;">
								<div class="col-sm-6 form-group">
									<button class="btn btn-lg btn-info" id="myFormSubmit">Create
										Account</button>
								</div>
							</div>
						</div>
					</form>
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

	<spring:url value="/resources/assets/js/bootstrap.js" var="bootstrapJs"></spring:url>
	<script src="${bootstrapJs}"></script>


	<spring:url value="/resources/assets/js/jquery.validate.min.js"
		var="jQueryValidate"></spring:url>
	<script type="text/javascript" src="${jQueryValidate}"></script>

	<spring:url value="/resources/app/set-up-trial.js" var="setUpTrialJs"></spring:url>
	<script type="text/javascript" src="${setUpTrialJs}"></script>

</body>
</html>