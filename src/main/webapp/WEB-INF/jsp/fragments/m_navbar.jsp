<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<!-- Brand and toggle get grouped for better mobile display -->
<spring:url value="/web/dashboard" var="snapshot"></spring:url>
<spring:url value="/web/live-tracking" var="liveTracking"></spring:url>



<spring:url value="/logout" var="logout"></spring:url>

<!-- ......................My Account....................... -->
<spring:url value="/web/account/change-password" var="password"></spring:url>
<spring:url value="/web/account/edit-profile" var="editProfile"></spring:url>

<spring:url value="/resources/assets/images/logo.png" var="image"></spring:url>
<spring:url value="/resources/assets/images/menu-ajax-loader.gif"
	var="menuAjaxLoader"></spring:url>
<!-- Collect the nav links, forms, and other content for toggling -->
<div class="sidebar-menu">
	<div class="sidebar-menu-inner">
		<header class="logo-env">
			<!-- logo -->
			<div class="logo">
				<a href="${snapshot}"> <img id="imgCompanyLogo" src="${image}"
					width="165px" alt="abc" />
				</a>
			</div>

			<!-- logo collapse icon -->
			<div class="sidebar-collapse">
				<a href="#" class="sidebar-collapse-icon"> <!-- add class "with-animation" if you want sidebar to have animation during expanding/collapsing transition -->
					<i class="entypo-menu"></i>
				</a>
			</div>

			<!-- open/close menu icon (do not remove if you want to enable menu on mobile devices) -->
			<div class="sidebar-mobile-menu visible-xs">
				<a href="#" class="with-animation"> <!-- add class "with-animation" to support animation -->
					<i class="entypo-menu"></i>
				</a>
			</div>
		</header>
		<div>
			<ul id="main-menu" class="main-menu">
				<security:authorize access="hasAnyRole('SITE_ADMIN')">
					<jsp:include page="site-admin-navbar.jsp"></jsp:include>
				</security:authorize>
			</ul>
			<ul id="main-menu" class="main-menu">
				<security:authorize access="hasAnyRole('PARTNER')">
					<jsp:include page="partner-navbar.jsp"></jsp:include>
				</security:authorize>
			</ul>

			<img id="menuAjaxLoader" src="${menuAjaxLoader}"
				style="margin-left: 50%" alt="loading..." />

			<form id="frmLogout" action="${logout}" method="post">
				<input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}" />
			</form>
		</div>


	</div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		getLoggedCompanyLogo();
		loadSidebarMenus();
	});

	function loadSidebarMenus() {
		var contextPath = "${pageContext.request.contextPath}";
		$('#menuAjaxLoader').show();
		$
				.ajax({
					url : contextPath + "/web/current-user/menu-items",
					type : "GET",
					success : function(response) {
						//hide loading image
						$('#menuAjaxLoader').hide();
						if (response.length > 0) {
							buildMenu(response);
							//load js for side bar menu.
							$
									.getScript(
											"/resources/assets/js/neon-api.js",
											function() {
												$
														.getScript(
																"/resources/assets/js/neon-custom.js",
																function() {
																	// Sidebar Collapse icon
																	$(
																			".sidebar-collapse-icon")
																			.click(
																					function(
																							ev) {
																						ev
																								.preventDefault();
																						var with_animation = $(
																								this)
																								.hasClass(
																										'with-animation');
																						toggle_sidebar_menu(with_animation);
																					});
																	// Mobile Sidebar Collapse icon
																	$(
																			".sidebar-mobile-menu a")
																			.click(
																					function(
																							ev) {
																						ev
																								.preventDefault();
																						var with_animation = $(
																								this)
																								.hasClass(
																										'with-animation');
																						if (with_animation) {
																							$(
																									'#main-menu')
																									.stop()
																									.slideToggle(
																											'normal',
																											function() {
																												$(
																														'#main-menu')
																														.css(
																																'height',
																																'auto');
																											});
																						} else {
																							$(
																									'#main-menu')
																									.toggle();
																						}
																					});
																});
											});
						}
					},
					error : function(xhr, error) {
						console.log("Error loading side bar menu items.");
					}
				});
	}

	function buildMenu(menuItems) {
		var rootMenu = [];
		var childMenu = [];
		for (i = 0; i < menuItems.length; i++) {
			var menu = menuItems[i];
			if (menu.parent == null) {
				rootMenu.push(menu)
			} else {
				childMenu.push(menu);
			}
		}
		var generated = '';
		for (var i = 0; i < rootMenu.length; i++) {
			var parentMenu = rootMenu[i];
			var generatedMenu = buildChildMenu(parentMenu, childMenu, generated);
			generated = generatedMenu;
		}
		$('#main-menu').append(generated);
	}

	function buildChildMenu(parentMenu, childMenu, generatedMenu) {
		var parentId = parentMenu.id;
		if (parentMenu.parent === null) {
			//has child then add has-sub css class.
			if (isHaveChild(parentId, childMenu)) {
				generatedMenu = generatedMenu
						+ '<li class="has-sub"><a href=' + parentMenu.link + '> <i class='+ parentMenu.iconClass +'></i><span class="title">'
						+ parentMenu.label + '</span></a>';

			} else {
				generatedMenu = generatedMenu
						+ '<li><a href=' + parentMenu.link + '> <i class='+ parentMenu.iconClass +'></i><span class="title">'
						+ parentMenu.label + '</span></a></li>';
			}
		}

		if (childMenu.length > 0) {
			generatedMenu = generatedMenu + "<ul>";
			for (var j = 0; j < childMenu.length; j++) {
				var child = childMenu[j];
				if (parentId == child.parent.id) {
					if (isHaveChild(child.id, childMenu)) {
						generatedMenu = generatedMenu
								+ '<li class="has-sub"><a href='+ child.link +'><span class="title">'
								+ child.label + '</span> </a>';
						generatedMenu = buildChildMenu(child, childMenu,
								generatedMenu)
								+ '</li>';
					} else {
						generatedMenu = generatedMenu
								+ '<li><a href='+ child.link +'><span class="title">'
								+ child.label + '</span> </a></li>';
					}
				}
			}
			generatedMenu = generatedMenu + "</ul>";
		}
		return generatedMenu;
	}

	//return true if current menu have child.
	function isHaveChild(parentId, childMenu) {
		for (var k = 0; k < childMenu.length; k++) {
			if (childMenu[k].parent.id == parentId)
				return true;
		}
		return false;
	}

	//for show comapny logo
	function getLoggedCompanyLogo() {
		var contextPath = "${pageContext.request.contextPath}";
		$.ajax({
			url : contextPath + "/web/company-logo",
			type : "GET",
			success : function(company) {
				if (company.logo) {
					$("#imgCompanyLogo").attr("src",
							"data:image/png;base64," + company.logo);
				}
			},
			error : function(xhr, error) {
				console.log("Error in load company logo............");
			}
		});
	}
</script>