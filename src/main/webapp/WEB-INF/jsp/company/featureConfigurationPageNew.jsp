<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Copy Company</title>
</head>

<style>
.div-button-detail {
	padding: 20px;
	margin: 25px 0 20px;
	text-align: center;
	border: 1px solid blue;
}
</style>

<script type="text/javascript">
	var contextPath = "${pageContext.request.contextPath}";
	$(document).ready(
			function() {

				$('#saveDefaultData').on(
						'click',
						function() {

							if ($("#dbCompany").val() == "-1") {
								alert("please select company");
								return;
							}

							if ($("#userCount").val() == "0") {
								alert("please enter valid user count");
								return;
							}
							window.location = location.protocol + '//'
									+ location.host
									+ "/web/load-features-data?companyPid="
									+ $("#dbCompany").val() + "&userCount="
									+ $("#userCount").val()
						});

				$('#dbCompany').on(
						'change',
						function() {
							if ($("#dbCompany").val() == "-1") {
								alert("please select company");
								return;
							}
							$.ajax({
								url : location.protocol + '//' + location.host
										+ "/web/getUserCountByCompany",
								method : 'GET',
								async : false,
								data : {
									companyPid : $("#dbCompany").val(),
								},
								success : function(data) {
									console.log(data);
									$("#existUser").html(data);
									$('.extusr').show();
								},
							});
						});

			});
</script>


<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Feature Configuration</h2>
			<div class="clearfix"></div>
			<hr />
			<form id="rootwizard-2" method="post" action=""
				class="form-wizard validate">
				<div class="steps-progress">
					<div class="progress-indicator"></div>
				</div>
				<ul>
					<li class="active"><a href="#tab2-1" data-toggle="tab"><span>1</span>select
							company and count user</a></li>
					<li><a href="#tab2-2" data-toggle="tab"><span>2</span>features
							configuration setup</a></li>
				</ul>
				<div class="tab-content">
					<div class="tab-pane active" id="tab2-1">
						<div class="col-md-2 extusr pull-right" hidden="true">
							<span><label>existing regis users : </label><label
								style="color: red" id="existUser"></label> </span>
						</div>

						<div class="row">
							<div class="col-md-4">
								<div class="form-group" id="selectCompany">
									<label class="control-label" for="door_no">company</label> <select
										id="dbCompany" name="companyPid" class="form-control"><option
											value="-1">select company</option>
										<c:forEach items="${companies}" var="company">
											<option value="${company.pid}">${company.legalName}</option>
										</c:forEach>
									</select>
								</div>
							</div>

							<div class="col-md-4">
								<div class="form-group">
									<label class="control-label" for="door_no">user count</label> <input
										class="form-control" name="userCount" id="userCount"
										data-validate="number,required" placeholder="Numbers only" />
								</div>
							</div>
						</div>
					</div>

					<div class="tab-pane" id="tab2-2">
						<div class="row">
							<div class="col-md-1"></div>
							<div class="col-md-4">
								<div class="div-button-detail">
									<i class="fa fa-map-marker color-blue"></i> <strong>Install
										Default</strong>
									<p>Install Only default data needed for company .it consist
										of user creation,employee creation,hierarchy,document and
										activity set up ,account details etc..</p>
									<br> <br> <input type="button"
										class="btn btn-success" id="saveDefaultData"
										value="Default Data" />
								</div>
							</div>
							<div class="col-md-1"></div>
							<div class="col-md-4">
								<div class="div-button-detail">
									<i class="fa fa-building color-blue"></i> <strong>Install
										Customised</strong>
									<p>Install Only data needed by customer for company. So
										customer can select needed data .</p>
									<br> <br> <br>
									<button class="btn btn-danger">Customised Data</button>
								</div>
							</div>
						</div>
					</div>

					<ul class="pager wizard">
						<li class="previous"><a href="#"><i
								class="entypo-left-open"></i> Previous</a></li>
						<li class="next"><a href="#">Next <i
								class="entypo-right-open"></i></a></li>
					</ul>
				</div>
			</form>
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
