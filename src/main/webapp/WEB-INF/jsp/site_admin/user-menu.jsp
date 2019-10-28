<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Menu Association</title>
<style type="text/css">
.error {
	color: red;
}
</style>
<spring:url
	value="/resources/assets/plugin/jstree/themes/default/style.min.css"
	var="jstreeCss"></spring:url>
<link href="${jstreeCss}" rel="stylesheet">
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>User Menu-item</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div>
				Company <select id="dbCompany" class="form-control selectpicker" data-live-search="true">
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
							<h4 class="modal-title">Assign Menu Items</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<label class="error-msg" style="color: red;"></label> <label><input
								id="cbSelectAll" type="checkbox" class="form-control" value="">Select All</label>
							<div class="form-group">
								<div id="tree-container"></div>
							</div>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveUserMenu"
								value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			
			<div class="modal fade container" id="sortMenuItemsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Sort Root Menu Items</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<label class="error-msg" style="color: red;"></label>
							<div class="form-group">
								<table class="table table-striped table-bordered">
									<thead>
										<tr>
											<th>Menu</th>
											<th>Sort Order</th>
										</tr>
									</thead>
									<tbody id="tblSortMenuItems">
									</tbody>
								</table>
							</div>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveSortedUserMenu"
								value="Save" />
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
	<spring:url value="/resources/assets/plugin/jstree/jstree.min.js"
		var="jstreeJS"></spring:url>
	<script type="text/javascript" src="${jstreeJS}"></script>

	<script type="text/javascript">
		var contextPath = "${pageContext.request.contextPath}";
		var userPid = null;
		var rootMenuItems = [];
		$(document).ready(function() {

			$('.selectpicker').selectpicker();
			
			$('#dbCompany').change(function() {
				var optionSelected = $(this).find("option:selected");
				loadCompanyUsers(optionSelected.val());
			});
			$('#cbSelectAll').change(function() {
				if($(this).is(":checked")) {
					$('#tree-container').jstree("select_all");
				} else {
					$('#tree-container').jstree("deselect_all");
				}
			});
			$('#btnSaveUserMenu').click(function() {
				saveUserMenuItems();
			});
			$('#btnSaveSortedUserMenu').click(function() {
				saveSortedUserMenuItems();
			});
			loadMenuItems();
		});

		function loadMenuItems() {
			$.ajax({
				url : contextPath + "/web/menu-items",
				type : "GET",
				success : function(response) {
					buildMenuItemsTree(response);
				},
				error : function(xhr, error) {
					console.log("Error loading menuitems.");
				}
			});
		}

		function buildMenuItemsTree(responseData) {
			var menuItemsTreeData = [];
			responseData.forEach(function(element) {
				var item = {};
				item.id = element.id;
				if(element.parent == null) {
					item.parent = "#";
				} else {
					item.parent = element.parent.id;
				}
	        	item.text = element.label;
	        	menuItemsTreeData.push(item);
			});
			
			$('#tree-container').jstree({
				'core' : {
					'data' : menuItemsTreeData
				},
				"checkbox" : {
					"three_state": false
				},
				"plugins" : ["checkbox"]
			}).on('loaded.jstree', function (e, data) {
				$(this).jstree("open_all");
			});

		}

		function loadCompanyUsers(companyPid) {
			$('#tblUsers').html("");
			$.ajax({
				url : contextPath + "/web/management/users/company/" + companyPid,
				type : "GET",
				success : function(response) {
					$.each(response,function(index, user) {
						$('#tblUsers').append('<tr><td>'+ user.firstName + '</td><td><button class="btn btn-info" onclick="showMenuItemModal(\'' + user.pid + '\')">Assign Menu Items</button>&nbsp;&nbsp;'
								+ '<button class="btn btn-info" onclick="showSortMenuItemsModal(\'' + user.pid + '\')">Sort Menu Items</button></td>'
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
			$('#tree-container').jstree("deselect_all");
			$('#menuItemsModal').modal('show');
			$.ajax({
				url : contextPath + "/web/user-menu/" + userPid,
				type : "GET",
				success : function(response) {
					var userMenuItemsIds = [];
					if (response) {
						$.each(response, function(index, menuItem) {
							userMenuItemsIds.push(menuItem.id);
						});
						$('#tree-container').jstree('select_node', userMenuItemsIds); //node ids that you want to check
					}
				},
				error : function(xhr, error) {
					onError(xhr, error);
				},
			});
		}
		
		function showSortMenuItemsModal(selectedUserPid) {
			$('#tblSortMenuItems').html("");
			$('#sortMenuItemsModal').modal('show');
			$.ajax({
				url : contextPath + "/web/root-user-menu/" + selectedUserPid,
				type : "GET",
				success : function(response) {
					if (response) {
						$.each(response, function(index, menu) {
							if(menu.parent == null) {
								if(menu.sortOrder == null) {
									$('#tblSortMenuItems').append('<tr><td>'+ menu.menuItemLabel + '</td><td><input id="'+ menu.id +'" type="number" value="" /></td>'
											+ '</tr>');
								} else {
									$('#tblSortMenuItems').append('<tr><td>'+ menu.menuItemLabel + '</td><td><input id="'+ menu.id +'" type="number" value="'+ menu.sortOrder +'" /></td>'
											+ '</tr>');
								}
								
							}
						});
					}
				},
				error : function(xhr, error) {
					onError(xhr, error);
				},
			});
		}
		
		function saveUserMenuItems() {
			$("#menuItemsModal .error-msg").html("");
			var checked_ids = $('#tree-container').jstree('get_selected');
			if (checked_ids == "") {
				$("#menuItemsModal .error-msg").html("Please select Menu items");
				return;
			}
			$.ajax({
				url : contextPath + "/web/user-menu/save",
				type : "POST",
				data : {
					userPid : userPid,
					assignedMenuItems : "" + checked_ids,
				},
				success : function(status) {
					$("#menuItemsModal").modal("hide");
				},
				error : function(xhr, error) {
					console.log("Error saving user menuitems : " + error);
				},
			});
		}
		
		function saveSortedUserMenuItems() {
			var sortOrders = "";
			$('#tblSortMenuItems > tr').each(function() {
				var element = $(this).find("input");
				if($(element).val() != null) {
					sortOrders += $(element).val() + ":"  + $(element).attr('id') + ",";
				}
			});
			$.ajax({
				url : contextPath + "/web/user-menu/sort-order/save",
				type : "POST",
				data : {
					sortOrders : sortOrders,
				},
				success : function(status) {
					$("#sortMenuItemsModal").modal("hide");
				},
				error : function(xhr, error) {
					console.log("Error saving sorted user menuitems : " + error);
				},
			});
		}
	</script>
</body>
</html>