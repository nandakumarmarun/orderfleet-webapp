<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Influencer Association</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
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
			<h2>Account Profile - Influencer Association</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">

						<div class="form-group">
							<div class="col-sm-6">
								<div class="form-check">
									<input type="checkbox" name="loadAssociatedAccountsOnly" class="form-check-input"
										id="loadAssociatedAccountsOnly" checked="checked"> <label
										class="form-check-label" for="loadAssociatedAccountsOnly">Load
										Associated Account Only</label>
								</div>
							</div>
						</div>
						<div class="form-group">
							<div class="col-sm-2">
								Influencer Account Types<select id="dbAccountTypes"
									name="accountType" class="form-control">
									<option value="-1">--Select--</option>
									<c:forEach items="${accountTypes}" var="accountType">
										<option value="${accountType.pid}">${accountType.name}</option>
									</c:forEach>
								</select>
							</div>

							<div class="col-sm-2">
								Influencer Accounts<select id="dbAccount" name="accountPid"
									class="form-control">
									<option value="-1">--Select--</option>
								</select>
							</div>
							<div class="col-sm-2">
								Associated Account Types<select id="dbAssociatedAccountTypes"
									name="associatedAccountType" class="form-control">
									<option value="no">All</option>
								</select>
							</div>

							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-info" id="btnApply">Apply</button>
							</div>
							<div class="col-sm-1">
								<br> <input class="btn btn-success" type="button"
									id="btnSaveAssociatedAccountProfile" value="Save" />
									<br>
									<label id="savingStatus" style="color: red;"></label>
							</div>
							<!-- <div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-Primary" id="downloadXls">Download</button>
							</div> -->
						</div>
					</form>
				</div>
			</div>
			<hr />
			<div style="overflow: auto; height: 500px">
				<div class="form-group">
					<div id="associatedAccountProfileCheckboxes">
						<div class="row">
							<div class="col-md-12 col-sm-12 clearfix">
								<input type="radio" value="all" name="filter_accountProfile">
								&nbsp;All&nbsp;&nbsp; <input type="radio" value="selected"
									name="filter_accountProfile">
								&nbsp;Selected&nbsp;&nbsp; <input type="radio"
									value="unselected" name="filter_accountProfile">
								&nbsp;Unselected&nbsp;&nbsp;
								<button type="button" class="btn btn-info"
									id="btnSearch_accountProfile" style="float: right;">Search</button>
								<input type="text" id="search_accountProfiles"
									placeholder="Search..." class="form-control"
									style="width: 200px; float: right;">
							</div>
						</div>

						<br>

						<table class='table table-striped table-bordered'
							id="allAssociateAccountProfiles">
							<thead>
								<tr>
									<th><input type="checkbox" class="allcheckbox">
										All</th>
									<th>Account Profiles</th>
								</tr>
							</thead>
							<tbody id="tblAllAssociateAccountProfiles">
								<tr>
									<td>No Data Available</td>
								</tr>
							</tbody>
						</table>
					</div>

				</div>
				<label class="error-msg" style="color: red;"></label>
			</div>

			<!-- <div class="table-responsive">
				<table class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th><input type="checkbox" id="selectAll"/>&nbsp;&nbsp;Select All</th>
							<th>Employee</th>
							<th>Receiver</th>
							<th>Document</th>
							<th>Amount<p id="totalDocument" style="float: right;"></p></th>
							<th>Volume<p id="totalVolume" style="float: right;"></p></th>
							<th>Total Quantity</th>
							<th>Date</th>
							<th>Status</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody id="tBodyInventoryVoucher">
					</tbody>
				</table>
			</div> -->
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<!--<spring:url value="/web/inventory-vouchers" var="urlInventoryVoucher"></spring:url>-->
		</div>
	</div>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url
		value="/resources/app/account-profile-influencer-association.js"
		var="accountProfileInfluencerAssociationJs"></spring:url>
	<script type="text/javascript"
		src="${accountProfileInfluencerAssociationJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

</body>
</html>