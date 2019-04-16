<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="en">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta name="description" content="Modern Admin Panel" />
<meta name="author" content="" />

<title>SalesNrich | Registration</title>

<spring:url value="/resources/assets/images/fovicon.png" var="favicon"></spring:url>
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

<spring:url value="/resources/assets/css/facebook.css" var="facebookCss"></spring:url>
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
	<div>
		<div class="row">
			<div class="col-md-8 col-md-offset-2">
				<h1>Registration</h1>

				<c:if test="${not empty msg}">
					<div class="alert alert-${css} alert-dismissible" role="alert">
						<button type="button" class="close" data-dismiss="alert"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<span>${msg}</span>

						<!-- <strong>Registration saved!</strong> Please check your email for
						confirmation. -->
					</div>
				</c:if>

				<!-- <div class="alert alert-danger" ng-show="vm.error">
					<strong>Registration failed!</strong> Please try again later.
				</div>

				<div class="alert alert-danger" ng-show="vm.errorUserExists">
					<strong>Login name already registered!</strong> Please choose
					another one.
				</div>

				<div class="alert alert-danger" ng-show="vm.errorEmailExists">
					<strong>E-mail is already in use!</strong> Please choose another
					one.
				</div>

				<div class="alert alert-danger" ng-show="vm.doNotMatch">The
					password and its confirmation do not match!</div> -->
			</div>
			<div class="col-md-8 col-md-offset-2">
			<spring:url value="/web/register" var="registrationDto" />
				<%-- <form:form name="form" id="regForm" role="form"  method="post" action="${register}" commandName="managedUserDTO">
					<div class="form-group">
						<spring:bind path="login">
							<div class="form-group${status.error ? ' has-error' : ''}">
								<label class="control-label" for="login">Username</label>
								<form:input type="text" class="form-control" id="login"
									path="login" placeholder="Your username" />
								<form:errors path="login" cssClass="control-label" />
							</div>
						</spring:bind>
					</div>
					<div class="form-group">
						<spring:bind path="email">
							<div class="form-group${status.error ? ' has-error' : ''}">
								<label class="control-label" for="email">E-mail</label>
								<form:input type="email" class="form-control" id="email"
									path="email" placeholder="Your e-mail" />
								<form:errors path="email" cssClass="control-label" />
							</div>
						</spring:bind>
					</div>
					<div class="form-group">
						<spring:bind path="password">
							<div class="form-group${status.error ? ' has-error' : ''}">
								<label class="control-label" for="password">New Password</label>
								<form:input type="password" class="form-control" id="password"
									path="password" placeholder="New Password" />
								<form:errors path="password" cssClass="control-label" />
							</div>
						</spring:bind>
						<!-- <span>Password strength bar goes here</span> -->
					</div>
					<div class="form-group">
							<div class="form-group${status.error ? ' has-error' : ''}">
								<label class="control-label" for="confirmPassword">New
									Password Confirmation</label>
								<input type="password" class="form-control"
									id="confirmPassword" name="confirmPassword"
									placeholder="New Password" />
							</div>
					</div>
					<button type="submit" class="btn btn-primary">Register</button>
				</form:form> --%>
			</div>
		</div>
	</div>

	<spring:url value="/resources/assets/js/bootstrap.js" var="bootstrapJs"></spring:url>
	<script src="${bootstrapJs}"></script>

	<spring:url value="/resources/assets/js/neon-login.js" var="login"></spring:url>
	<script type="text/javascript" src="${login}"></script>
	<script>
		$(function() {
			$.validator.addMethod("regx", function(value, element, regexpr) {
				return regexpr.test(value);
			}, "Your username can only contain lower-case letters and digits.");

			$("#regForm")
					.validate(
							{
								rules : {
									login : {
										required : true,
										minlength : 1,
										maxlength : 50,
										regx : /^[a-z0-9]*$/
									},
									email : {
										required : true,
										minlength : 5,
										maxlength : 100,
										email : true
									},
									password : {
										required : true,
										minlength : 4,
										maxlength : 50
									},
									confirmPassword : {
										required : true,
										minlength : 4,
										maxlength : 50,
										equalTo: "#password"
									}
								},
								messages : {
									login : {
										required : "Your username is required.",
										minlength : "Your username is required to be at least 1 character.",
										maxlength : "Your username cannot be longer than 50 characters.",
									},
									email : {
										required : "Your e-mail is required.",
										minlength : "Your e-mail is required to be at least 5 characters.",
										maxlength : "Your e-mail cannot be longer than 100 characters.",
										email : "Youre-mail is invalid."
									},
									password : {
										required : "Your password is required.",
										minlength : "Your password is required to be at least 4 characters.",
										maxlength : "Your password cannot be longer than 50 characters."
									},
									confirmPassword : {
										required : "Your confirmation password is required.",
										minlength : "Your confirmation password is required to be at least 4 characters.",
										maxlength : "Your confirmation password cannot be longer than 50 characters."
									}

								}
							});
		});
	</script>

</body>
</html>