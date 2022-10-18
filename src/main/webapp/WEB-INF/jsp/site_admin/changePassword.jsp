
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.13.0/css/all.min.css">

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich |Change Password</title>
<style type="text/css">
.error {
	color: red;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Change Password</h2>
			<div class="row col-xs-12"></div>
			<hr>
							<div class="row">
				<div class="form-group">
					<div class="col-sm-3">
						Company<select id="dbCompany" name="companyPid"
							class="form-control selectpicker" data-live-search="true">
							<option value="no">Select Company</option>
							<c:forEach items="${companies}" var="company">
								<option value="${company.pid}">${company.legalName}</option>
							</c:forEach>
						</select>
					</div>
					<!-- <div class="col-sm-3">
						<br />
						<button type="button" class="btn btn-orange"
									onclick="ChangePassword.showModalPopup($('#viewModal'),0);">Change Password</button>
					</div> -->
				</div>
				<div class="col-sm-3">
				<div class="input-group">
						 <input type="text"
							class="form-control" placeholder="Search" id="search"/> <span
							class="input-group-btn">
							<button
								class="btn btn-default" type="button" id="btnSearch">Search</button>
						</span>
						
					</div>
					</div>
					<div class="clearfix"></div>
			<hr />
								<table class="table table-striped table-bordered of-tbl-search"
				id="tblChangePassword">
				<thead>
					<tr>
						<th>Name</th>
						<th>Password</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody id="tBodyChangePassword">
				</tbody>
			</table>
						<spring:url value="/web/changePassword" var="urlChangePassword"></spring:url>
					
					<form id="passwordForm" name="form" role="form" method="post"
						action="${urlChangePassword}">
				<!-- Model Container-->
				<div class="modal fade container" id="viewModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="viewModalLabel">Change Password</h4>
							</div>
							<div class="modal-body">
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>

								<div class="modal-body" style="overflow: auto;">
									<div class="form-group">
							<label class="control-label" for="password">New password</label>
							<br>
							<input type="password" id="password" autocomplete="current-password" required
								name="password" placeholder="New password" maxlength=50 size ="80">
								 <i class="far fa-eye" id="togglePassword" style="margin-left: -30px; cursor: pointer;"></i> 
						</div>
						<div class="form-group">
							<label class="control-label" for="confirmPassword">New
								password confirmation</label> <input type="password"
								 id="confirmPassword" name="confirmPassword"
								placeholder="Confirm the new password" maxlength=50 required size="80">
						</div>
						

									
								</div>
							</div>
							<div class="modal-footer">
								<input class="btn btn-success" type="button"
								id="myFormSubmit" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>
					
			</div>
		<br>
			<hr/>
			
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/change-password.js" var="changePasswordJs"></spring:url>
	<script type="text/javascript" src="${changePasswordJs}"></script>
	
	<script type="text/javascript">
	const togglePassword = document.querySelector('#togglePassword');
	  const password = document.querySelector('#password');

	  togglePassword.addEventListener('click', function (e) {
	    // toggle the type attribute
	    const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
	    password.setAttribute('type', type);
	    // toggle the eye slash icon
	    this.classList.toggle('fa-eye-slash');
	});
	
	</script>
</body>
</html>