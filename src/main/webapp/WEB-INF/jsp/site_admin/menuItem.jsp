<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Menu Items</title>

<style type="text/css">
.dynamic_dropdown dd, .dynamic_dropdown dt, .dynamic_dropdown ul {
	margin: 0px;
	padding: 0px;
}

.dynamic_dropdown dd {
	position: relative;
}

.dynamic_dropdown dt a {
	display: block;
	border: 1px solid #ebebeb;
	width: 100%;
}

.dynamic_dropdown dt a span {
	cursor: pointer;
	display: block;
	padding: 5px;
	coclor: #555555;
}

.dynamic_dropdown dd ul {
	border: 1px solid #ebebeb;
}

.dynamic_dropdown span.value {
	display: none;
}

.dynamic_dropdown dd ul li a {
	padding: 5px;
	display: block;
}

.dynamic_dropdown dd ul li a:hover {
	background-color: #ebebeb;
}

.dynamic_dropdown img.flag {
	border: none;
}

.flagvisibility {
	display: none;
}
</style>

<!-- <script type="text/javascript">
	$(document).ready(function() {
		$(".dynamic_dropdown dt a span").addClass("flagvisibility");
		$(".dynamic_dropdown dt a").click(function() {
			$(".dynamic_dropdown dd ul").toggle();
		});
		$(".dynamic_dropdown dd ul li a").click(function() {
			var text = $(this).html();
			$("#formDT").html("");
			$("#formDT").html(text);
			$(".dynamic_dropdown dd ul").hide();
		});
		$(document).bind('click', function(e) {
			var $clicked = $(e.target);
			if (!$clicked.parents().hasClass("dynamic_dropdown"))
				$(".dynamic_dropdown dd ul").hide();
		});
	});
</script> -->
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Menu Items</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="MenuItem.showModalPopup($('#myModal'));">Create
						new Menu Item</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Icon</th>
						<th>Label</th>
						<th>Parent</th>
						<th>Link</th>
						<th>Description</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${menuItems}" var="menuItem">
						<tr>
							<td><i class='${menuItem.iconClass}'></i></td>
							<td>${menuItem.label}</td>
							<td>${menuItem.parentLabel}</td>
							<td>${menuItem.link}</td>
							<td>${menuItem.description}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="MenuItem.showModalPopup($('#viewModal'),'${menuItem.id}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="MenuItem.showModalPopup($('#myModal'),'${menuItem.id}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="MenuItem.showModalPopup($('#deleteModal'),'${menuItem.id}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/menu-item" var="urlMenuItem"></spring:url>

			<form id="menuItemForm" role="form" method="post"
				action="${urlMenuItem}">
				<!-- Model Container-->
				<div class="modal fade container" id="myModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Create or edit a
									Menu Item</h4>
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
										<label class="control-label" for="field_label">Label</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="label" id="field_label" maxlength="255"
											placeholder="Label" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_parent">Parent</label>
										<select id="field_parent" name="menuItemParentId"
											class="form-control">
											<option value="-1">Select MenuItem Parent</option>
											<c:forEach items="${menuItems}" var="menuItem">
												<option value="${menuItem.id}">${menuItem.label}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_description">Description</label>
										<input type="text" class="form-control" name="description"
											id="field_description" placeholder="Description" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_link">Link</label> <input
											type="text" class="form-control" name="link" id="field_link"
											maxlength="150" placeholder="Link" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_menuIcon">Icon</label>
										<dl class="dynamic_dropdown">
											<dt>
												<a href="#" id="formDT"><span class="entypo-users">entypo-users</span></a>
											</dt>
											<dd>
												<ul style="overflow: scroll; height: 250px;">
													<li><a href="#"><span class="entypo-note">entypo-note</span></a></li>
													<li><a href="#"><span class="entypo-logo-db">entypo-logo-db</span></a></li>
													<li><a href="#"><span class="entypo-music">entypo-music</span></a></li>
													<li><a href="#"><span class="entypo-search">entypo-search</span></a></li>
													<li><a href="#"><span class="entypo-flashlight">entypo-flashlight</span></a></li>
													<li><a href="#"><span class="entypo-mail">entypo-mail</span></a></li>
													<li><a href="#"><span class="entypo-heart">entypo-heart</span></a></li>
													<li><a href="#"><span class="entypo-heart-empty">entypo-heart-empty</span></a></li>
													<li><a href="#"><span class="entypo-star">entypo-star</span></a></li>
													<li><a href="#"><span class="entypo-star-empty">entypo-star-empty</span></a></li>
													<li><a href="#"><span class="entypo-user">entypo-user</span></a></li>
													<li><a href="#"><span class="entypo-users">entypo-users</span></a></li>
													<li><a href="#"><span class="entypo-user-add">entypo-user-add</span></a></li>
													<li><a href="#"><span class="entypo-video">entypo-video</span></a></li>
													<li><a href="#"><span class="entypo-picture">entypo-picture</span></a></li>
													<li><a href="#"><span class="entypo-camera">entypo-camera</span></a></li>
													<li><a href="#"><span class="entypo-layout">entypo-layout</span></a></li>
													<li><a href="#"><span class="entypo-menu">entypo-menu</span></a></li>
													<li><a href="#"><span class="entypo-check">entypo-check</span></a></li>
													<li><a href="#"><span class="entypo-cancel">entypo-cancel</span></a></li>
													<li><a href="#"><span
															class="entypo-cancel-circled">entypo-cancel-circled</span></a></li>
													<li><a href="#"><span
															class="entypo-cancel-squared">entypo-cancel-squared</span></a></li>
													<li><a href="#"><span class="entypo-plus">entypo-plus</span></a></li>
													<li><a href="#"><span class="entypo-plus-circled">entypo-plus-circled</span></a></li>
													<li><a href="#"><span class="entypo-plus-squared">entypo-plus-squared</span></a></li>
													<li><a href="#"><span class="entypo-minus">entypo-minus</span></a></li>
													<li><a href="#"><span class="entypo-minus-circled">entypo-minus-circled</span></a></li>
													<li><a href="#"><span class="entypo-minus-squared">entypo-minus-squared</span></a></li>
													<li><a href="#"><span class="entypo-help">entypo-help</span></a></li>
													<li><a href="#"><span class="entypo-help-circled">entypo-help-circled</span></a></li>
													<li><a href="#"><span class="entypo-info">entypo-info</span></a></li>
													<li><a href="#"><span class="entypo-info-circled">entypo-info-circled</span></a></li>
													<li><a href="#"><span class="entypo-back">entypo-back</span></a></li>
													<li><a href="#"><span class="entypo-home">entypo-home</span></a></li>
													<li><a href="#"><span class="entypo-link">entypo-link</span></a></li>
													<li><a href="#"><span class="entypo-attach">entypo-attach</span></a></li>
													<li><a href="#"><span class="entypo-lock">entypo-lock</span></a></li>
													<li><a href="#"><span class="entypo-lock-open">entypo-lock-open</span></a></li>
													<li><a href="#"><span class="entypo-eye">entypo-eye</span></a></li>
													<li><a href="#"><span class="entypo-tag">entypo-tag</span></a></li>
													<li><a href="#"><span class="entypo-bookmark">entypo-bookmark</span></a></li>
													<li><a href="#"><span class="entypo-bookmarks">entypo-bookmarks</span></a></li>
													<li><a href="#"><span class="entypo-flag">entypo-flag</span></a></li>
													<li><a href="#"><span class="entypo-thumbs-up">entypo-thumbs-up</span></a></li>
													<li><a href="#"><span class="entypo-thumbs-down">entypo-thumbs-down</span></a></li>
													<li><a href="#"><span class="entypo-download">entypo-download</span></a></li>
													<li><a href="#"><span class="entypo-upload">entypo-upload</span></a></li>
													<li><a href="#"><span class="entypo-upload-cloud">entypo-upload-cloud</span></a></li>
													<li><a href="#"><span class="entypo-reply">entypo-reply</span></a></li>
													<li><a href="#"><span class="entypo-reply-all">entypo-reply-all</span></a></li>
													<li><a href="#"><span class="entypo-forward">entypo-forward</span></a></li>
													<li><a href="#"><span class="entypo-quote">entypo-quote</span></a></li>
													<li><a href="#"><span class="entypo-code">entypo-code</span></a></li>
													<li><a href="#"><span class="entypo-export">entypo-export</span></a></li>
													<li><a href="#"><span class="entypo-pencil">entypo-pencil</span></a></li>
													<li><a href="#"><span class="entypo-feather">entypo-feather</span></a></li>
													<li><a href="#"><span class="entypo-print">entypo-print</span></a></li>
													<li><a href="#"><span class="entypo-retweet">entypo-retweet</span></a></li>
													<li><a href="#"><span class="entypo-keyboard">entypo-keyboard</span></a></li>
													<li><a href="#"><span class="entypo-comment">entypo-comment</span></a></li>
													<li><a href="#"><span class="entypo-chat">entypo-chat</span></a></li>
													<li><a href="#"><span class="entypo-bell">entypo-bell</span></a></li>
													<li><a href="#"><span class="entypo-attention">entypo-attention</span></a></li>
													<li><a href="#"><span class="entypo-alert">entypo-alert</span></a></li>
													<li><a href="#"><span class="entypo-vcard">entypo-vcard</span></a></li>
													<li><a href="#"><span class="entypo-address">entypo-address</span></a></li>
													<li><a href="#"><span class="entypo-location">entypo-location</span></a></li>
													<li><a href="#"><span class="entypo-map">entypo-map</span></a></li>
													<li><a href="#"><span class="entypo-direction">entypo-direction</span></a></li>
													<li><a href="#"><span class="entypo-compass">entypo-compass</span></a></li>
													<li><a href="#"><span class="entypo-cup">entypo-cup</span></a></li>
													<li><a href="#"><span class="entypo-trash">entypo-trash</span></a></li>
													<li><a href="#"><span class="entypo-doc">entypo-doc</span></a></li>
													<li><a href="#"><span class="entypo-docs">entypo-docs</span></a></li>
													<li><a href="#"><span class="entypo-doc-landscape">entypo-doc-landscape</span></a></li>
													<li><a href="#"><span class="entypo-doc-text">entypo-doc-text</span></a></li>
													<li><a href="#"><span class="entypo-doc-text-inv">entypo-doc-text-inv</span></a></li>
													<li><a href="#"><span class="entypo-newspaper">entypo-newspaper</span></a></li>
													<li><a href="#"><span class="entypo-book-open">entypo-book-open</span></a></li>
													<li><a href="#"><span class="entypo-book">entypo-book</span></a></li>
													<li><a href="#"><span class="entypo-folder">entypo-folder</span></a></li>
													<li><a href="#"><span class="entypo-archive">entypo-archive</span></a></li>
													<li><a href="#"><span class="entypo-box">entypo-box</span></a></li>
													<li><a href="#"><span class="entypo-rss">entypo-rss</span></a></li>
													<li><a href="#"><span class="entypo-phone">entypo-phone</span></a></li>
													<li><a href="#"><span class="entypo-cog">entypo-cog</span></a></li>
													<li><a href="#"><span class="entypo-tools">entypo-tools</span></a></li>
													<li><a href="#"><span class="entypo-share">entypo-share</span></a></li>
													<li><a href="#"><span class="entypo-shareable">entypo-shareable</span></a></li>
													<li><a href="#"><span class="entypo-basket">entypo-basket</span></a></li>
													<li><a href="#"><span class="entypo-bag">entypo-bag</span></a></li>
													<li><a href="#"><span class="entypo-calendar">entypo-calendar</span></a></li>
													<li><a href="#"><span class="entypo-login">entypo-login</span></a></li>
													<li><a href="#"><span class="entypo-logout">entypo-logout</span></a></li>
													<li><a href="#"><span class="entypo-mic">entypo-mic</span></a></li>
													<li><a href="#"><span class="entypo-mute">entypo-mute</span></a></li>
													<li><a href="#"><span class="entypo-sound">entypo-sound</span></a></li>
													<li><a href="#"><span class="entypo-volume">entypo-volume</span></a></li>
													<li><a href="#"><span class="entypo-clock">entypo-clock</span></a></li>
													<li><a href="#"><span class="entypo-hourglass">entypo-hourglass</span></a></li>
													<li><a href="#"><span class="entypo-lamp">entypo-lamp</span></a></li>
													<li><a href="#"><span class="entypo-light-down">entypo-light-down</span></a></li>
													<li><a href="#"><span class="entypo-light-up">entypo-light-up</span></a></li>
													<li><a href="#"><span class="entypo-adjust">entypo-adjust</span></a></li>
													<li><a href="#"><span class="entypo-block">entypo-block</span></a></li>
													<li><a href="#"><span class="entypo-resize-full">entypo-resize-full</span></a></li>
													<li><a href="#"><span class="entypo-resize-small">entypo-resize-small</span></a></li>
													<li><a href="#"><span class="entypo-popup">entypo-popup</span></a></li>
													<li><a href="#"><span class="entypo-publish">entypo-publish</span></a></li>
													<li><a href="#"><span class="entypo-window">entypo-window</span></a></li>
													<li><a href="#"><span class="entypo-arrow-combo">entypo-arrow-combo</span></a></li>
													<li><a href="#"><span class="entypo-down-circled">entypo-down-circled</span></a></li>
													<li><a href="#"><span class="entypo-left-circled">entypo-left-circled</span></a></li>
													<li><a href="#"><span class="entypo-right-circled">entypo-right-circled</span></a></li>
													<li><a href="#"><span class="entypo-up-circled">entypo-up-circled</span></a></li>
													<li><a href="#"><span class="entypo-down-open">entypo-down-open</span></a></li>
													<li><a href="#"><span class="entypo-left-open">entypo-left-open</span></a></li>
													<li><a href="#"><span class="entypo-right-open">entypo-right-open</span></a></li>
													<li><a href="#"><span class="entypo-up-open">entypo-up-open</span></a></li>
													<li><a href="#"><span
															class="entypo-down-open-mini">entypo-down-open-mini</span></a></li>
													<li><a href="#"><span
															class="entypo-left-open-mini">entypo-left-open-mini</span></a></li>
													<li><a href="#"><span
															class="entypo-right-open-mini">entypo-right-open-mini</span></a></li>
													<li><a href="#"><span class="entypo-up-open-mini">entypo-up-open-mini</span></a></li>
													<li><a href="#"><span class="entypo-down-open-big">entypo-down-open-big</span></a></li>
													<li><a href="#"><span class="entypo-left-open-big">entypo-left-open-big</span></a></li>
													<li><a href="#"><span
															class="entypo-right-open-big">entypo-right-open-big</span></a></li>
													<li><a href="#"><span class="entypo-up-open-big">entypo-up-open-big</span></a></li>
													<li><a href="#"><span class="entypo-down">entypo-down</span></a></li>
													<li><a href="#"><span class="entypo-left">entypo-left</span></a></li>
													<li><a href="#"><span class="entypo-right">entypo-right</span></a></li>
													<li><a href="#"><span class="entypo-up">entypo-up</span></a></li>
													<li><a href="#"><span class="entypo-down-dir">entypo-down-dir</span></a></li>
													<li><a href="#"><span class="entypo-left-dir">entypo-left-dir</span></a></li>
													<li><a href="#"><span class="entypo-right-dir">entypo-right-dir</span></a></li>
													<li><a href="#"><span class="entypo-up-dir">entypo-up-dir</span></a></li>
													<li><a href="#"><span class="entypo-down-bold">entypo-down-bold</span></a></li>
													<li><a href="#"><span class="entypo-left-bold">entypo-left-bold</span></a></li>
													<li><a href="#"><span class="entypo-right-bold">entypo-right-bold</span></a></li>
													<li><a href="#"><span class="entypo-up-bold">entypo-up-bold</span></a></li>
													<li><a href="#"><span class="entypo-down-thin">entypo-down-thin</span></a></li>
													<li><a href="#"><span class="entypo-left-thin">entypo-left-thin</span></a></li>
													<li><a href="#"><span class="entypo-right-thin">entypo-right-thin</span></a></li>
													<li><a href="#"><span class="entypo-note-beamed">entypo-note-beamed</span></a></li>
													<li><a href="#"><span class="entypo-ccw">entypo-ccw</span></a></li>
													<li><a href="#"><span class="entypo-cw">entypo-cw</span></a></li>
													<li><a href="#"><span class="entypo-arrows-ccw">entypo-arrows-ccw</span></a></li>
													<li><a href="#"><span class="entypo-level-down">entypo-level-down</span></a></li>
													<li><a href="#"><span class="entypo-level-up">entypo-level-up</span></a></li>
													<li><a href="#"><span class="entypo-shuffle">entypo-shuffle</span></a></li>
													<li><a href="#"><span class="entypo-loop">entypo-loop</span></a></li>
													<li><a href="#"><span class="entypo-switch">entypo-switch</span></a></li>
													<li><a href="#"><span class="entypo-play">entypo-play</span></a></li>
													<li><a href="#"><span class="entypo-stop">entypo-stop</span></a></li>
													<li><a href="#"><span class="entypo-pause">entypo-pause</span></a></li>
													<li><a href="#"><span class="entypo-record">entypo-record</span></a></li>
													<li><a href="#"><span class="entypo-to-end">entypo-to-end</span></a></li>
													<li><a href="#"><span class="entypo-to-start">entypo-to-start</span></a></li>
													<li><a href="#"><span class="entypo-fast-forward">entypo-fast-forward</span></a></li>
													<li><a href="#"><span class="entypo-fast-backward">entypo-fast-backward</span></a></li>
													<li><a href="#"><span class="entypo-progress-0">entypo-progress-0</span></a></li>
													<li><a href="#"><span class="entypo-progress-1">entypo-progress-1</span></a></li>
													<li><a href="#"><span class="entypo-progress-2">entypo-progress-2</span></a></li>
													<li><a href="#"><span class="entypo-progress-3">entypo-progress-3</span></a></li>
													<li><a href="#"><span class="entypo-target">entypo-target</span></a></li>
													<li><a href="#"><span class="entypo-palette">entypo-palette</span></a></li>
													<li><a href="#"><span class="entypo-list">entypo-list</span></a></li>
													<li><a href="#"><span class="entypo-list-add">entypo-list-add</span></a></li>
													<li><a href="#"><span class="entypo-signal">entypo-signal</span></a></li>
													<li><a href="#"><span class="entypo-trophy">entypo-trophy</span></a></li>
													<li><a href="#"><span class="entypo-battery">entypo-battery</span></a></li>
													<li><a href="#"><span class="entypo-back-in-time">entypo-back-in-time</span></a></li>
													<li><a href="#"><span class="entypo-monitor">entypo-monitor</span></a></li>
													<li><a href="#"><span class="entypo-mobile">entypo-mobile</span></a></li>
													<li><a href="#"><span class="entypo-network">entypo-network</span></a></li>
													<li><a href="#"><span class="entypo-cd">entypo-cd</span></a></li>
													<li><a href="#"><span class="entypo-inbox">entypo-inbox</span></a></li>
													<li><a href="#"><span class="entypo-install">entypo-install</span></a></li>
													<li><a href="#"><span class="entypo-globe">entypo-globe</span></a></li>
													<li><a href="#"><span class="entypo-cloud">entypo-cloud</span></a></li>
													<li><a href="#"><span class="entypo-cloud-thunder">entypo-cloud-thunder</span></a></li>
													<li><a href="#"><span class="entypo-flash">entypo-flash</span></a></li>
													<li><a href="#"><span class="entypo-moon">entypo-moon</span></a></li>
													<li><a href="#"><span class="entypo-flight">entypo-flight</span></a></li>
													<li><a href="#"><span class="entypo-paper-plane">entypo-paper-plane</span></a></li>
													<li><a href="#"><span class="entypo-leaf">entypo-leaf</span></a></li>
													<li><a href="#"><span class="entypo-lifebuoy">entypo-lifebuoy</span></a></li>
													<li><a href="#"><span class="entypo-mouse">entypo-mouse</span></a></li>
													<li><a href="#"><span class="entypo-briefcase">entypo-briefcase</span></a></li>
													<li><a href="#"><span class="entypo-suitcase">entypo-suitcase</span></a></li>
													<li><a href="#"><span class="entypo-dot">entypo-dot</span></a></li>
													<li><a href="#"><span class="entypo-dot-2">entypo-dot-2</span></a></li>
													<li><a href="#"><span class="entypo-dot-3">entypo-dot-3</span></a></li>
													<li><a href="#"><span class="entypo-brush">entypo-brush</span></a></li>
													<li><a href="#"><span class="entypo-magnet">entypo-magnet</span></a></li>
													<li><a href="#"><span class="entypo-infinity">entypo-infinity</span></a></li>
													<li><a href="#"><span class="entypo-erase">entypo-erase</span></a></li>
													<li><a href="#"><span class="entypo-chart-pie">entypo-chart-pie</span></a></li>
													<li><a href="#"><span class="entypo-chart-line">entypo-chart-line</span></a></li>
													<li><a href="#"><span class="entypo-chart-bar">entypo-chart-bar</span></a></li>
													<li><a href="#"><span class="entypo-chart-area">entypo-chart-area</span></a></li>
													<li><a href="#"><span class="entypo-tape">entypo-tape</span></a></li>
													<li><a href="#"><span
															class="entypo-graduation-cap">entypo-graduation-cap</span></a></li>
													<li><a href="#"><span class="entypo-language">entypo-language</span></a></li>
													<li><a href="#"><span class="entypo-ticket">entypo-ticket</span></a></li>
													<li><a href="#"><span class="entypo-water">entypo-water</span></a></li>
													<li><a href="#"><span class="entypo-droplet">entypo-droplet</span></a></li>
													<li><a href="#"><span class="entypo-air">entypo-air</span></a></li>
													<li><a href="#"><span class="entypo-credit-card">entypo-credit-card</span></a></li>
													<li><a href="#"><span class="entypo-floppy">entypo-floppy</span></a></li>
													<li><a href="#"><span class="entypo-clipboard">entypo-clipboard</span></a></li>
													<li><a href="#"><span class="entypo-megaphone">entypo-megaphone</span></a></li>
													<li><a href="#"><span class="entypo-database">entypo-database</span></a></li>
													<li><a href="#"><span class="entypo-drive">entypo-drive</span></a></li>
													<li><a href="#"><span class="entypo-bucket">entypo-bucket</span></a></li>
													<li><a href="#"><span class="entypo-thermometer">entypo-thermometer</span></a></li>
													<li><a href="#"><span class="entypo-key">entypo-key</span></a></li>
													<li><a href="#"><span class="entypo-flow-cascade">entypo-flow-cascade</span></a></li>
													<li><a href="#"><span class="entypo-flow-branch">entypo-flow-branch</span></a></li>
													<li><a href="#"><span class="entypo-flow-tree">entypo-flow-tree</span></a></li>
													<li><a href="#"><span class="entypo-flow-line">entypo-flow-line</span></a></li>
													<li><a href="#"><span class="entypo-flow-parallel">entypo-flow-parallel</span></a></li>
													<li><a href="#"><span class="entypo-rocket">entypo-rocket</span></a></li>
													<li><a href="#"><span class="entypo-gauge">entypo-gauge</span></a></li>
													<li><a href="#"><span class="entypo-traffic-cone">entypo-traffic-cone</span></a></li>
													<li><a href="#"><span class="entypo-cc">entypo-cc</span></a></li>
													<li><a href="#"><span class="entypo-cc-by">entypo-cc-by</span></a></li>
													<li><a href="#"><span class="entypo-cc-nc">entypo-cc-nc</span></a></li>
													<li><a href="#"><span class="entypo-cc-nc-eu">entypo-cc-nc-eu</span></a></li>
													<li><a href="#"><span class="entypo-cc-nc-jp">entypo-cc-nc-jp</span></a></li>
													<li><a href="#"><span class="entypo-cc-sa">entypo-cc-sa</span></a></li>
													<li><a href="#"><span class="entypo-cc-nd">entypo-cc-nd</span></a></li>
													<li><a href="#"><span class="entypo-cc-pd">entypo-cc-pd</span></a></li>
													<li><a href="#"><span class="entypo-cc-zero">entypo-cc-zero</span></a></li>
													<li><a href="#"><span class="entypo-cc-share">entypo-cc-share</span></a></li>
													<li><a href="#"><span class="entypo-cc-remix">entypo-cc-remix</span></a></li>
												</ul>
											</dd>
										</dl>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button id="myFormSubmit" class="btn btn-primary">Save</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

			<form name="viewForm" role="form">
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
								<h4 class="modal-title" id="viewModalLabel">Menu Item</h4>
							</div>
							<div class="modal-body">
								<div class="modal-body" style="overflow: auto;">
									<!-- error message -->
									<div class="alert alert-danger alert-dismissible" role="alert"
										style="display: none;">
										<button type="button" class="close"
											onclick="$('.alert').hide();" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
										<p></p>
									</div>

									<dl class="dl-horizontal">
										<dt>
											<span>Label</span>
										</dt>
										<dd>
											<span id="lbl_label"></span>
										</dd>
										<hr />
										<dt>
											<span>Parent</span>
										</dt>
										<dd>
											<span id="lbl_parent"></span>
										</dd>
										<hr />
										<dt>
											<span>Icon</span>
										</dt>
										<dd>
											<span id="lbl_icon"></span>
										</dd>
										<hr />
										<dt>
											<span>Link</span>
										</dt>
										<dd>
											<span id="lbl_link"></span>
										</dd>
										<hr />
										<dt>
											<span>Description</span>
										</dt>
										<dd>
											<span id="lbl_description"></span>
										</dd>
									</dl>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

			<form id="deleteForm" name="deleteForm" action="${urlMenuItem}">
				<!-- Model Container-->
				<div class="modal fade container" id="deleteModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title">Confirm delete operation</h4>
							</div>
							<div class="modal-body">
								<div class="modal-body" style="overflow: auto;">
									<!-- error message -->
									<div class="alert alert-danger alert-dismissible" role="alert"
										style="display: none;">
										<button type="button" class="close"
											onclick="$('.alert').hide();" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
										<p></p>
									</div>
									<p>Are you sure you want to delete this Menu Item?</p>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button class="btn btn-danger">Delete</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/menuItem.js" var="menuItemJs"></spring:url>
	<script type="text/javascript" src="${menuItemJs}"></script>
</body>
</html>