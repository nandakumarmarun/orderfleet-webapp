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

<title>SalesNrich | Login</title>

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

</head>
<body class="page-body login-page login-form-fall">

	<div class="login-container">
		<div class="login-header login-caret">

			<div class="login-content">
				<spring:eval
					expression="@environment.getProperty('application.logoPath')"
					var="logoPath" />
				<a href="/" class="logo"> <img src="${logoPath}" width="200px"
					alt="${logoPath}" />

				</a>

				<p class="description">Dear user, log in to access the admin
					area!</p>

				<!-- progress bar indicator -->
				<div class="login-progressbar-indicator">
					<h3>43%</h3>
					<span>logging in...</span>
				</div>
			</div>
		</div>

		<div class="login-progressbar">
			<div></div>
		</div>
		<div class="login-form">
			<div class="login-content">
				<div class="form-login-error">
					<h3>Invalid login</h3>
					<p>
						Enter <strong>demo</strong>/<strong>demo</strong> as login and
						password.
					</p>
				</div>
				<c:url var="url" value="/login" />
				<form method="post" role="form" id="loginForm" action="${url}">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}" />
					<div class="form-group">
						<div class="input-group">
							<div class="input-group-addon">
								<i class="entypo-user"></i>
							</div>
							<input type="text" class="form-control" name="username"
								autofocus="autofocus" id="username" placeholder="Username"
								autocomplete="off" />
						</div>
					</div>
					<div class="form-group">
						<div class="input-group">
							<div class="input-group-addon">
								<i class="entypo-key"></i>
							</div>
							<input type="password" class="form-control" name="password"
								id="password" placeholder="Password" autocomplete="off" />
						</div>
					</div>
					<div class="pull-left">
						<input type="checkbox" name="remember-me" id="remember-me">
						<label for="remember-me">Remember me</label>
					</div>

					<div class="clearfix"></div>
					<br />
					<div>
						<c:if test="${param.error != null}">
							<p style="color: red;">Invalid username or password.</p>
						</c:if>
						<c:if test="${param.expired != null}">
							<p style="color: red;">Session Expired:	Multiple Login in Server</p>
							<!-- <div class="modal fade container" id="expiredModal">
								model Dialog
									<div class="modal-dialog">
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal"
													aria-label="Close">
													<span aria-hidden="true">&times;</span>
												</button>
												<h4 class="modal-title">Assign Activities</h4>
											</div>
											<div class="modal-body" style="overflow: auto; height: 500px">
												<div class="form-group">
													<p style="color: red;">Multiple Login in Server</p>
												</div>
											</div>
													
											<div class="modal-footer">
												<button class="btn" data-dismiss="modal">Cancel</button>
											</div>
										</div>
										/.modal-content
									</div>
									/.modal-dialog
								</div> -->
						</c:if>
						<c:if test="${param.logout != null}">
							<p style="color: #1a69cb;">You have been logged out.</p>
						</c:if>
					</div>
					<div class="form-group">
						<button type="submit" class="btn btn-primary btn-block btn-login"
							style="border-color: #d9e2e5;">
							<i class="entypo-login"></i> Sign In
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<spring:url value="/resources/assets/js/bootstrap.js" var="bootstrapJs"></spring:url>
	<script src="${bootstrapJs}"></script>

	<spring:url value="/resources/assets/js/neon-login.js" var="login"></spring:url>
	<script type="text/javascript" src="${login}"></script>

	<spring:url value="/resources/assets/js/jquery.validate.min.js"
		var="jQueryValidate"></spring:url>
	<script type="text/javascript" src="${jQueryValidate}"></script>

	<spring:url value="/resources/assets/orderfleet-app.js"
		var="orderfleetApp"></spring:url>
	<script type="text/javascript" src="${orderfleetApp}"></script>

	<script type="text/javascript">
		//for mobile users
		$(document).ready(function() {
			var login = Orderfleet.getParameterByName("user");
			var pass = Orderfleet.getParameterByName("pass");
			if (login && pass) {
				$('#username').val(login);
				$('#password').val(pass);

				$('#loginForm').submit();
			}
		});
	</script>
</body>
</html>