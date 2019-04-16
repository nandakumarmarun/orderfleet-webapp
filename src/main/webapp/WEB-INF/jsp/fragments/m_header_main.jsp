<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<spring:url value="/logout" var="logout"></spring:url>
<spring:url value="/resources/assets/images/user.png" var="image"></spring:url>

<!-- ......................My Account....................... -->
<spring:url value="/web/account/change-password" var="password"></spring:url>
<spring:url value="/web/account/edit-profile" var="editProfile"></spring:url>

<div class="se-pre-con"></div>
<div class="row">
	<script type="text/javascript">
		// for show user profile picture
		$(document).ready(function() {
			getLoggedUserImage();
		});
		function getLoggedUserImage() {
			var contextPath = "${pageContext.request.contextPath}";
					$.ajax({
						url : contextPath + "/web/employee-profile-image",
						type : "GET",
						success : function(employeeProfile) {
							if (employeeProfile.profileImage) {
								$("#imgLoggedUser").attr("src","data:image/png;base64,"+ employeeProfile.profileImage);
							}
						},
						error : function(xhr, error) {
							console.log("Error in load employee Profile Image.................");
						}
					});
		}
	</script>
	<!-- Profile Info and Notifications -->
	<div class="col-md-6 col-sm-8 clearfix">

		<ul class="user-info pull-left pull-none-xsm">

			<!-- Profile Info -->
			<li class="profile-info dropdown">
				<!-- add class "pull-right" if you want to place this from right -->

				<a href="#" class="dropdown-toggle" data-toggle="dropdown"> <img
					id="imgLoggedUser" src="${image}" alt="" class="img-circle"
					width="44" /> <sec:authentication property="principal.username" />
			</a>
				<ul class="dropdown-menu">

					<!-- Reverse Caret -->
					<li class="caret"></li>

					<!-- Profile sub-links -->
					<li><a href="${editProfile}"> <i class="entypo-user"></i> Edit Profile
					</a></li>

					<li><a href="${password}"> <i class="entypo-mail"></i> Change
							Password
					</a></li>

				</ul>
			</li>
		</ul>


	</div>

	<!-- Raw Links -->
	<div class="col-md-6 col-sm-4 clearfix hidden-xs">
		<ul class="list-inline links-list pull-right">
			<li class="sep"></li>

			<li>
				<form id="frmLogout" action="${logout}" method="post">
					<a href="#" onclick="$(this).closest('form').submit()"
						style="font-weight: bolder;"> Log Out <i
						class="entypo-logout right"></i>
					</a> <input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}" />
				</form>
			</li>
		</ul>
	</div>
</div>