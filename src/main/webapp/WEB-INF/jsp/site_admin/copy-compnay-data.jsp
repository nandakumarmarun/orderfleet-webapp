<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Copy company data</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Copy Company Data</h2>
			<div class="clearfix"></div>
			<hr />
			<section class="master-data-blocks">
				<div class="container">
					<div class="row">
						<div class="col-sm-3">
							<div class="form-group">
								<label class="control-label" for="field_from_schema">From
									Schema</label> <input readonly="readonly" type="text"
									class="form-control" name="fromSchema" id="field_from_schema"
									maxlength="255" placeholder="Schema Name"
									value="${currentSchema}" />
							</div>
						</div>
						<div class="col-sm-3">
							<div class="form-group">
								<label class="control-label" for="field_to_schema">To
									Schema</label> <input autofocus="autofocus" type="text"
									class="form-control" name="toSchema" id="field_to_schema"
									maxlength="255" placeholder="Schema Name" />
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-3">
							<div class="form-group">
								<label class="control-label">From Company</label> <select
									id="dbCompanyFrom" class="form-control"><option
										value="-1">-- Select --</option>
									<c:forEach items="${companies}" var="company">
										<option value="${company.id}">${company.legalName}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="col-sm-3">
							<div class="form-group">
								<label class="control-label">To Company ID</label> <input
									type="text" class="form-control" name="toCompany"
									id="field_to_company" maxlength="255"
									placeholder="Enter Company-ID in 'to Schama'" />
								<%-- <select
									id="dbCompanyTo" class="form-control"><option
										value="-1">-- Select --</option>
									<c:forEach items="${companies}" var="company">
										<option value="${company.id}">${company.legalName}</option>
									</c:forEach>
								</select> --%>
							</div>
						</div>
					</div>
				</div>
			</section>
			<section class="master-data-blocks" style="background-color: #eee">
				<div class="alert alert-danger alert-dismissible" role="alert"
					style="display: none;">
					<button type="button" class="close" onclick="$('.alert').hide();"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<p></p>
				</div>
				<div class="row">
					<div class="col-sm-6">
						<h3>
							<input type="checkbox" id="cbMasterDataAll"
								style="width: 20px; height: 20px;"> Master Data
						</h3>
					</div>
					<div class="col-sm-6"></div>
					<div class="col-sm-6">
						<br />
						<div class="pull-right">
							<button id="btnCMD" type="button" class="btn btn-success">Copy</button>
						</div>
					</div>
				</div>
				<div id="divCheckboxInline">
					<label class="checkbox-inline"> <input type="checkbox"
						value="activities"> Activities
					</label> <label class="checkbox-inline"> <input type="checkbox"
						value="documents"> Documents
					</label> <label class="checkbox-inline"> <input type="checkbox"
						value="forms"> Forms
					</label> <label class="checkbox-inline"> <input type="checkbox"
						value="questions"> Questions
					</label> <label class="checkbox-inline"> <input type="checkbox"
						value="division"> Division
					</label><label class="checkbox-inline"> <input type="checkbox"
						value="pCategory"> Product Category
					</label><label class="checkbox-inline"> <input type="checkbox"
						value="pGroup"> Product Group
					</label> <label class="checkbox-inline"> <input type="checkbox"
						value="ecomPGroup"> Ecom Product Group/Profile
					</label> <label class="checkbox-inline"> <input type="checkbox"
						value="ecomPGroup"> Ecom Product Group/Profile
					</label> <label class="checkbox-inline"> <input type="checkbox"
						value="stockLocation"> Stock Location
					</label> <label class="checkbox-inline"> <input type="checkbox"
						value="priceLevel"> Price Level
					</label>
				</div>
				<br />
			</section>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	<spring:url value="/resources/app/copy-company-data.js"
		var="copyCompanyDataJs"></spring:url>
	<script type="text/javascript" src="${copyCompanyDataJs}"></script>
</body>
</html>