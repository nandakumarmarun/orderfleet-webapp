<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Password</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<div class="row">
				<div class="col-md-8 col-md-offset-2">
					<h2>
						Password for [<b><sec:authentication
								property="principal.username" /></b>]
					</h2>
					<c:if test="${not empty msg}">
						<div class="alert alert-${css} alert-dismissible" role="alert">
							<button type="button" class="close" data-dismiss="alert"
								aria-label="Close">
								<span aria-hidden="true">×</span>
							</button>
							${msg}
						</div>
					</c:if>
					<spring:url value="/web/account/change-password"
						var="urlChangePass"></spring:url>
					<form id="passwordForm" name="form" role="form" method="post"
						action="${urlChangePass}">
						<div class="form-group">
							<label class="control-label" for="password">New password</label>
							<input type="password" class="form-control" id="password"
								name="password" placeholder="New password" maxlength=50 required>
						</div>
						<div class="form-group">
							<label class="control-label" for="confirmPassword">New
								password confirmation</label> <input type="password"
								class="form-control" id="confirmPassword" name="confirmPassword"
								placeholder="Confirm the new password" maxlength=50 required>
						</div>
						<button type="submit" class="btn btn-primary">Save</button>
					</form>
				</div>
			</div>

			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<script type="text/javascript">
		var passwordForm = $("#passwordForm");
		// Specify the validation rules
		var validationRules = {
			password : {
				required : true,
				minlength : 4,
				maxlength : 50
			},
			confirmPassword : {
				required : true,
				minlength : 4,
				maxlength : 50,
				equalTo : "#password"
			}
		};

		// Specify the validation error messages
		var validationMessages = {
			password : {
				required : "Your password is required.",
				minlength : "Your password is required to be at least 4 characters.",
				maxlength : "Your password cannot be longer than 50 characters."
			},
			confirmPassword : {
				required : "Your confirmation password is required.",
				minlength : "Your confirmation password is required to be at least 4 characters.",
				maxlength : "Your confirmation password cannot be longer than 50 characters.",
				equalTo : "The password and its confirmation do not match!"
			}
		};

		$(document).ready(function() {
			passwordForm.validate({
				rules : validationRules,
				messages : validationMessages,
				submitHandler : function(form) {
					return true;
				}
			});
		});
	</script>
</body>
</html>