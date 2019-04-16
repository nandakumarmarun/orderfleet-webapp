<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Mobile Menu Item Group</title>
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
			<h2>User Mobile Menu Item Group</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div>
				Company <select id="dbCompany" class="form-control">
					<option value="no">Select Company</option>
					<c:forEach items="${companies}" var="company">
						<option value="${company.pid}">${company.legalName}</option>
					</c:forEach>
				</select>
			</div>
			<br />
			<table class="table table-striped table-bordered">
				<thead>
					<tr>
						<th>User</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tblUsers">
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<div class="modal fade container" id="menuItemsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Menu Items Group</h4>
						</div>
						<div class="modal-body">
							<div class="form-group">
								<label class="control-label">Menu Group</label> <select
									id="dbMenuGroup" class="form-control"><option
										value="no">Select Menu Group</option>
									<c:forEach items="${menuGroups}" var="menuGroup">
										<option value="${menuGroup.pid}">${menuGroup.name}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						<label class="error-msg" style="color: red;"></label>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveMenuGroup" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<script type="text/javascript">
		var contextPath = "${pageContext.request.contextPath}";
		var userPid = null;
		$(document).ready(function() {
			$('#dbCompany').change(function() {
				var optionSelected = $(this).find("option:selected");
				loadCompanyUsers(optionSelected.val());
			});

			$('#btnSaveMenuGroup').click(function() {
				saveUserMenuGroup();
			});
		});

		function loadCompanyUsers(companyPid) {
			$('#tblUsers').html("");
			$.ajax({
						url : contextPath + "/web/management/users/company/" + companyPid,
						type : "GET",
						success : function(response) {
							$
									.each(
											response,
											function(index, user) {
												$('#tblUsers')
														.append(
																'<tr><td>'
																		+ user.firstName
																		+ '</td><td><button class="btn btn-info" onclick="showMenuItemModal(\''
																		+ user.pid
																		+ '\')">Assign Menu Group</button></td>'
																		+ '</tr>');
											});
						},
						error : function(xhr, error) {
							console.log("Error loading users.");
						}
					});
		}

		function showMenuItemModal(selectedUserPid) {
			userPid = selectedUserPid;
			//un select all
			$('#menuItemsModal').modal('show');
			$('#dbMenuGroup').val('no')
			$.ajax({
				url : contextPath + "/web/userMobileMenuItemGroups/" + userPid,
				type : "GET",
				success : function(response) {
					if (response) {
						$('#dbMenuGroup').val(response.pid)
					}
				},
				error : function(xhr, error) {
					onError(xhr, error);
				},
			});
		}

		function saveUserMenuGroup() {
			$("#menuItemsModal .error-msg").html("");

			var groupPid = $('#dbMenuGroup').val();
			if (groupPid == "no") {
				$("#menuItemsModal .error-msg")
						.html("Please select Menu Group");
				return;
			}
			$.ajax({
				url : contextPath + "/web/userMobileMenuItemGroups/save",
				type : "POST",
				data : {
					userPid : userPid,
					menuGroupPid : groupPid
				},
				success : function(status) {
					$("#menuItemsModal").modal("hide");
				},
				error : function(xhr, error) {
					console.log("Error saving user menuitems : " + error);
				},
			});
		}
	</script>
</body>
</html>