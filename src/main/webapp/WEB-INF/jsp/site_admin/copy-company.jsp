<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Copy Company</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Copy Company</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="well well-sm">
				<h4>Please fill the details to copy company.</h4>
			</div>

			<form id="rootwizard-2" method="post" action=""
				class="form-wizard validate">

				<div class="steps-progress">
					<div class="progress-indicator"></div>
				</div>

				<ul>
					<li class="active"><a href="#tab-1" data-toggle="tab"><span>1</span>Company
							Info</a></li>
					<li><a href="#tab-2" data-toggle="tab"><span>2</span>Users
							Info</a></li>
				</ul>

				<div class="tab-content">
					<div class="form-group">
						<div class="alert alert-danger alert-dismissible" role="alert"
							style="display: none;">
							<button type="button" class="close" onclick="$('.alert').hide();"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<p></p>
						</div>
					</div>
					<div class="tab-pane active" id="tab-1">
						<h4><strong>Company Info</strong></h4> <br /> <br />
						<div class="row">
							<div class="col-md-12">
								<div class="form-group">
									<label class="control-label" for="field_from_company">From
										Company</label><select id="field_company" data-validate="required"
										class="form-control" onchange="CopyCompany.onChangeCompany()"><optgroup
											label="From Company">
											<c:forEach items="${companies}" var="company">
												<option value="${company.pid}">${company.legalName}</option>
											</c:forEach>
										</optgroup>
									</select>
								</div>
							</div>
							<div class="col-md-12">
								<div class="form-group">
									<label class="control-label" for="field_legalName">New
										Company Legal Name</label> <input autofocus="autofocus" type="text"
										data-validate="required" class="form-control" name="legalName"
										id="field_legalName" maxlength="255"
										placeholder="Company Legal Name" />
								</div>
							</div>
							<div class="col-md-12">
								<div class="form-group">
									<label class="control-label" for="field_email">New
										Company Email</label> <input type="email" data-validate="required"
										class="form-control" name="email" id="field_email"
										maxlength="100" placeholder="Company Email" />
								</div>
							</div>
						</div>
					</div>

					<div class="tab-pane" id="tab-2">
						<h4><strong>Users Info</strong></h4> <br /> <br />
						<div class="responsive">
							<table class="table  table-striped table-bordered">
								<thead>
									<tr>
										<th>User Name</th>
										<th>New User Name</th>
										<th>Email</th>
									</tr>
								</thead>
								<tbody id="tbodyCompanyUsers">

								</tbody>
							</table>
						</div>

						<div class="form-group" align="right">
							<button id="btnFinish" type="button" class="btn btn-success">Finish
								Copy</button>
						</div>
					</div>

					<ul class="pager wizard">
						<li class="previous"><a href="#"><i
								class="entypo-left-open"></i>Previous</a></li>

						<li class="next"><a href="#">Next <i
								class="entypo-right-open"></i></a></li>
					</ul>
				</div>

			</form>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/company" var="urlCompany"></spring:url>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/jquery.bootstrap.wizard.min.js"
		var="wizardJs"></spring:url>
	<script type="text/javascript" src="${wizardJs}"></script>
	
	<spring:url value="/resources/app/copy-company.js" var="copyCompanyJs"></spring:url>
	<script type="text/javascript" src="${copyCompanyJs}"></script>
</body>
</html>