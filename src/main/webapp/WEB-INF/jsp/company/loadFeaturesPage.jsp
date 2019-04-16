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

<spring:url value="/resources/assets/images/Spinner.gif"
	var="loadingImage"></spring:url>

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
</style>

</head>
<body style="background-color: #3e5d8e">

	<div class="container">
		<div>
			<h1 class="well"
				style="text-align: center; background-color: #3e5d8e; color: white;">Features</h1>
			<div class="col-lg-12 fah">
				<div class="row">
					<spring:url value="/trial/setup" var="urlCompanyTrialSetUp"></spring:url>



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
							<div class="progress">
								<div class="progress-bar progress-bar-success"
									role="progressbar" aria-valuemin="0" aria-valuemax="100"
									style="width: 0%" id="progressBarForFeatures">0%</div>
							</div>
						</div>

						<div class="row" style="padding-left: inherit;">
							<div class="col-sm-6 form-group">
								<div class="well well-sm" style="">
									<span id="tdUsersrefresh"
										style="padding-right: 3px; padding-top: 3px; display: inline-block;">
										<img class="manImg" src="${loadingImage}" style="width: 25px;"></img>
									</span> <span class="glyphicon glyphicon-ok" id="tdUsersok"
										style="display: none; color: green; float: left;"></span>&nbsp
									Users Created Successfully
								</div>
							</div>
							<div class="col-sm-6 form-group">
								<div class="well well-sm">
									<span id="tdDefaultSetUprefresh"
										style="padding-right: 3px; padding-top: 3px; display: inline-block;">
										<img class="manImg" src="${loadingImage}" style="width: 25px;"></img>
									</span><span class="glyphicon glyphicon-ok" id="tdDefaultSetUpok"
										style="display: none; color: green; float: left;"></span>&nbsp
									Default Company Data SetUp Successfully
								</div>
							</div>
						</div>
						<div class="row" style="padding-left: inherit;">
							<div class="col-sm-6 form-group">
								<div class="well well-sm">
									<span id="tdEmployeerefresh"
										style="padding-right: 3px; padding-top: 3px; display: inline-block;">
										<img class="manImg" src="${loadingImage}" style="width: 25px;">
									</span> <span class="glyphicon glyphicon-ok" id="tdEmployeeok"
										style="display: none; color: green; float: left;"></span>&nbsp
									Employee Created Successfully
								</div>
							</div>
							<div class="col-sm-6 form-group">
								<div class="well well-sm">
									<span id="tdEmployeeHierarchyrefresh"
										style="padding-right: 3px; padding-top: 3px; display: inline-block;">
										<img class="manImg" src="${loadingImage}" style="width: 25px;">
									</span> <span class="glyphicon glyphicon-ok"
										id="tdEmployeeHierarchyok"
										style="display: none; color: green; float: left;"></span>&nbsp
									Employee Hierarchy Created Successfully
								</div>
							</div>
						</div>
						<div class="row" style="padding-left: inherit;">
							<div class="col-sm-6 form-group">
								<div class="well well-sm">
									<span id="tdDocumentSetUprefresh"
										style="padding-right: 3px; padding-top: 3px; display: inline-block;">
										<img class="manImg" src="${loadingImage}" style="width: 25px;">
									</span><span class="glyphicon glyphicon-ok" id="tdDocumentSetUpok"
										style="display: none; color: green; float: left;"></span>&nbsp
									Document Company Data SetUp Successfully
								</div>
							</div>
							<div class="col-sm-6 form-group">
								<div class="well well-sm">
									<span id="tdActivitySetUprefresh"
										style="padding-right: 3px; padding-top: 3px; display: inline-block;">
										<img class="manImg" src="${loadingImage}" style="width: 25px;">
									</span><span class="glyphicon glyphicon-ok" id="tdActivitySetUpok"
										style="display: none; color: green; float: left;"></span>&nbsp
									Activity Company Data SetUp Successfully
								</div>
							</div>
						</div>


						<div class="row" style="padding-left: inherit;">
							<div class="col-sm-6 form-group">
								<div class="well well-sm">
									<span id="tdEmployeeToLocationAssignedrefresh"
										style="padding-right: 3px; padding-top: 3px; display: inline-block;">
										<img class="manImg" src="${loadingImage}" style="width: 25px;">
									</span> <span class="glyphicon glyphicon-ok"
										id="tdEmployeeToLocationAssignedok"
										style="display: none; color: green; float: left;"></span>&nbsp
									Employee To Location Assigned Successfully
								</div>
							</div>
							<div class="col-sm-6 form-group">
								<div class="well well-sm">
									<span id="tdDefaultAssignedrefresh"
										style="padding-right: 3px; padding-top: 3px; display: inline-block;">
										<img class="manImg" src="${loadingImage}" style="width: 25px;">
									</span> <span class="glyphicon glyphicon-ok" id="tdDefaultAssignedok"
										style="display: none; color: green; float: left;"></span>&nbsp
									Default Data To Users Assigned Successfully
								</div>
							</div>
						</div>
						<div class="row" style="padding-left: inherit;">
							<div class="col-sm-6 form-group">
								<div class="well well-sm">
									<span id="tdDashboardrefresh"
										style="padding-right: 3px; padding-top: 3px; display: inline-block;">
										<img class="manImg" src="${loadingImage}" style="width: 25px;">
									</span> <span class="glyphicon glyphicon-ok" id="tdDashboardok"
										style="display: none; color: green; float: left;"></span>&nbsp
									Dashboard SetUp Successfully
								</div>
							</div>
							<div class="col-sm-6 form-group">
								<div class="well well-sm">

									<span id="tdDocumentAssignedrefresh"
										style="padding-right: 3px; padding-top: 3px; display: inline-block;">
										<img class="manImg" src="${loadingImage}" style="width: 25px;">
									</span> <span class="glyphicon glyphicon-ok" id="tdDocumentAssignedok"
										style="display: none; color: green; float: left;"></span>&nbsp
									Document Company Data Assigned Successfully
								</div>
							</div>
						</div>
						<div class="row" style="padding-left: inherit;">
							<div class="col-sm-6 form-group">
								<div class="well well-sm">
									<span id="tdMenuItemrefresh"
										style="padding-right: 3px; padding-top: 3px; display: inline-block;">
										<img class="manImg" src="${loadingImage}" style="width: 25px;">
									</span> <span class="glyphicon glyphicon-ok" id="tdMenuItemok"
										style="display: none; color: green; float: left;"></span>&nbsp
									MenuItem To Users Assigned Successfully
								</div>
							</div>
							<div class="col-sm-6 form-group">
								<div class="well well-sm">
									<span id="tdMobileMenuItemrefresh"
										style="padding-right: 3px; padding-top: 3px; display: inline-block;">
										<img class="manImg" src="${loadingImage}" style="width: 25px;">
									</span> <span class="glyphicon glyphicon-ok" id="tdMobileMenuItemok"
										style="display: none; color: green; float: left;"></span>&nbsp
									Mobile MenuItem To Users Assigned Successfully
								</div>
							</div>
						</div>

						<div class="row" style="padding-left: inherit;">
							<div class="col-sm-6 form-group">
								<button class="btn btn-lg btn-info" id="loadDashboard"
									disabled="disabled">Finish</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>


	<spring:url value="/resources/assets/js/bootstrap.js" var="bootstrapJs"></spring:url>
	<script src="${bootstrapJs}"></script>


	<spring:url value="/resources/assets/js/jquery.validate.min.js"
		var="jQueryValidate"></spring:url>
	<script type="text/javascript" src="${jQueryValidate}"></script>

	<spring:url value="/resources/app/load-features-page.js"
		var="loadFeaturesPageJs"></spring:url>
	<script type="text/javascript" src="${loadFeaturesPageJs}"></script>

</body>
</html>