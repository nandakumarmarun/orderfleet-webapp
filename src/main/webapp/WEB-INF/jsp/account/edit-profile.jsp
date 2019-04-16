<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Edit Profile</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<div class="row">
				<div class="col-md-8 col-md-offset-2">
					<h2>
						Edit user profile for [<b><sec:authentication
								property="principal.username" /></b>]
					</h2>
					<div class="alert alert-dismissible" role="alert"
						style="display: none;">
						<button type="button" class="close" onclick="$('.alert').hide();"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<p></p>
					</div>
					<spring:url value="/web/account" var="urlEditProfile"></spring:url>
					<form id="editProfileForm" name="form" role="form" method="post"
						action="${urlEditProfile}">
						<div class="form-group">
							<label class="control-label" for="firstName">First Name</label> <input
								type="text" value="${userDTO.firstName}" class="form-control"
								id="firstName" name="firstName" placeholder="Your first name"
								maxlength="55" required>
						</div>
						<div class="form-group">
							<label class="control-label" for="lastName">Last Name</label> <input
								type="text" value="${userDTO.lastName}" class="form-control"
								id="lastName" name="lastName" placeholder="Your last name"
								maxlength="55" required>
						</div>
						<div class="form-group">
							<label class="control-label" for="email">E-mail</label> <input
								type="email" value="${userDTO.email}" class="form-control"
								id="email" name="email" placeholder="Your email" maxlength="100"
								required>
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
		var userModel = {};
		var editProfileForm = $("#editProfileForm");
		// Specify the validation rules
		var validationRules = {
			firstName : {
				required : true,
				minlength : 1,
				maxlength : 50
			},
			lastName : {
				required : true,
				minlength : 1,
				maxlength : 50
			},
			email : {
				required : true,
				minlength : 5,
				maxlength : 100
			}
		};

		// Specify the validation error messages
		var validationMessages = {
			firstName : {
				required : "Your first name is required.",
				minlength : "Your first name is required to be at least 1 character.",
				maxlength : "Your first name cannot be longer than 50 characters."
			},
			lastName : {
				required : "Your last name is required.",
				minlength : "Your last name is required to be at least 1 character.",
				maxlength : "Your last name cannot be longer than 50 characters."
			},
			email : {
				required : "Your e-mail is required.",
				minlength : "Your e-mail is required to be at least 5 characters.",
				maxlength : "Your e-mail cannot be longer than 100 characters."
			}
		};

		$(document).ready(
				function() {
					editProfileForm.validate({
						rules : validationRules,
						messages : validationMessages,
						submitHandler : function(form) {
							updateUserInfo(form);
						}
					});

					//load user info
					$.ajax({
						url : location.protocol + '//' + location.host
								+ "/web/account",
						method : 'GET',
						success : function(data) {
							userModel = {pid : data.pid,companyPid : data.companyPid,
									companyName : data.companyName,
									login : data.login,
									firstName : data.firstName,
									lastName : data.lastName,
									email : data.email,
									mobile : data.mobile,
									activated : data.activated,
									langKey : data.langKey,
								};
								$('#firstName').val(data.firstName);
								$('#lastName').val(data.lastName);
								$('#email').val(data.email);
						},
						error : function(xhr, error) {
							onError(xhr, error);
						}
					});

				});

		function updateUserInfo(el) {
			userModel.firstName = $('#firstName').val();
			userModel.lastName = $('#lastName').val();
			userModel.email = $('#email').val();
			$('.alert').hide();
			$.ajax({
				method : $(el).attr('method'),
				url : $(el).attr('action'),
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(userModel),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

		function onSaveSuccess(data) {
			$(".alert > p").html("Profile Updated!");
			$(".alert").removeClass('alert-danger');
			$(".alert").addClass('alert-success');
			$('.alert').show();
		}

		function onError(httpResponse, exception) {
			var i;
			switch (httpResponse.status) {
			// connection refused, server not reachable
			case 0:
				addErrorAlert('Server not reachable',
						'error.server.not.reachable');
				break;
			case 400:
				var errorHeader = httpResponse
						.getResponseHeader('X-orderfleetwebApp-error');
				var entityKey = httpResponse
						.getResponseHeader('X-orderfleetwebApp-params');
				if (errorHeader) {
					var entityName = entityKey;
					addErrorAlert(errorHeader, errorHeader, {
						entityName : entityName
					});
				} else if (httpResponse.responseText) {
					var data = JSON.parse(httpResponse.responseText);
					if (data && data.fieldErrors) {
						for (i = 0; i < data.fieldErrors.length; i++) {
							var fieldError = data.fieldErrors[i];
							var convertedField = fieldError.field.replace(
									/\[\d*\]/g, '[]');
							var fieldName = convertedField.charAt(0)
									.toUpperCase()
									+ convertedField.slice(1);
							addErrorAlert('Field ' + fieldName
									+ ' cannot be empty', 'error.'
									+ fieldError.message, {
								fieldName : fieldName
							});
						}
					} else if (data && data.message) {
						addErrorAlert(data.message, data.message, data);
					} else {
						addErrorAlert(data);
					}
				} else {
					addErrorAlert(exception);
				}
				break;
			default:
				if (httpResponse.responseText) {
					var data = JSON.parse(httpResponse.responseText);
					if (data && data.description) {
						addErrorAlert(data.description);
					} else if (data && data.message) {
						addErrorAlert(data.message);
					} else {
						addErrorAlert(data);
					}
				} else {
					addErrorAlert(exception);
				}
			}
		}

		function addErrorAlert(message, key, data) {
			$(".alert > p").html(message);
			$(".alert").removeClass('alert-success');
			$(".alert").addClass('alert-danger');
			$('.alert').show();
		}
	</script>
</body>
</html>