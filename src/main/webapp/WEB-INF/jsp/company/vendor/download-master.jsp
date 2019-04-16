<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/fragments/m_head.jsp"></jsp:include>
<style type="text/css">
@import
	url("https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css")
	;

body {
	padding: 50px;
}

label {
	position: relative;
	cursor: pointer;
	color: #666;
	font-size: 20px;
	font-weight: normal;
}

input[type="checkbox"], input[type="radio"] {
	position: absolute;
	right: 9000px;
}

/*Check box*/
input[type="checkbox"]+.label-text:before {
	content: "\f096";
	font-family: "FontAwesome";
	speak: none;
	font-style: normal;
	font-weight: normal;
	font-variant: normal;
	text-transform: none;
	line-height: 1;
	-webkit-font-smoothing: antialiased;
	width: 1em;
	display: inline-block;
	margin-right: 5px;
}

input[type="checkbox"]:checked+.label-text:before {
	content: "\f14a";
	color: #2980b9;
	animation: effect 250ms ease-in;
}

input[type="checkbox"]:disabled+.label-text {
	color: #aaa;
}

input[type="checkbox"]:disabled+.label-text:before {
	content: "\f0c8";
	color: #ccc;
}

/*Radio box*/
input[type="radio"]+.label-text:before {
	content: "\f10c";
	font-family: "FontAwesome";
	speak: none;
	font-style: normal;
	font-weight: normal;
	font-variant: normal;
	text-transform: none;
	line-height: 1;
	-webkit-font-smoothing: antialiased;
	width: 1em;
	display: inline-block;
	margin-right: 5px;
}

input[type="radio"]:checked+.label-text:before {
	content: "\f192";
	color: #8e44ad;
	animation: effect 250ms ease-in;
}

input[type="radio"]:disabled+.label-text {
	color: #aaa;
}

input[type="radio"]:disabled+.label-text:before {
	content: "\f111";
	color: #ccc;
}

/*Radio Toggle*/
.toggle input[type="radio"]+.label-text:before {
	content: "\f204";
	font-family: "FontAwesome";
	speak: none;
	font-style: normal;
	font-weight: normal;
	font-variant: normal;
	text-transform: none;
	line-height: 1;
	-webkit-font-smoothing: antialiased;
	width: 1em;
	display: inline-block;
	margin-right: 10px;
}

.toggle input[type="radio"]:checked+.label-text:before {
	content: "\f205";
	color: #16a085;
	animation: effect 250ms ease-in;
}

.toggle input[type="radio"]:disabled+.label-text {
	color: #aaa;
}

.toggle input[type="radio"]:disabled+.label-text:before {
	content: "\f204";
	color: #ccc;
}

/* Center the loader */
#loader {
  position: absolute;
  left: 50%;
  z-index: 1;
  width: 150px;
  height: 150px;
  margin: -75px 0 0 -75px;
  border: 16px solid #f3f3f3;
  border-radius: 50%;
  border-top: 16px solid #3498db;
  width: 120px;
  height: 120px;
  -webkit-animation: spin 2s linear infinite;
  animation: spin 2s linear infinite;
}

@-webkit-keyframes spin {
  0% { -webkit-transform: rotate(0deg); }
  100% { -webkit-transform: rotate(360deg); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Add animation to "page content" */
.animate-bottom {
  position: relative;
  -webkit-animation-name: animatebottom;
  -webkit-animation-duration: 1s;
  animation-name: animatebottom;
  animation-duration: 1s
}

@-webkit-keyframes animatebottom {
  from { bottom:-100px; opacity:0 } 
  to { bottom:0px; opacity:1 }
}

@keyframes animatebottom { 
  from{ bottom:-100px; opacity:0 } 
  to{ bottom:0; opacity:1 }
}
</style>
<title>SalesNrich | Download Masters</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="/WEB-INF/jsp/fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="/WEB-INF/jsp/fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2></h2>
			<div class="clearfix"></div>
			<hr />
			<div id="alertMsg" class="alert alert-dismissible collapse" role="alert">
				<button type="button" class="close" onclick="$('#alertMsg').hide();"
					aria-label="Close">
					<span aria-hidden="true">×</span>
				</button>
				<strong></strong>
			</div>

			<spring:url value="/web/demo/download/masters"
				var="urlDownloadMasters"></spring:url>
			<div class="row" style="height: 150px;">
				<div id="loader" style="display: none;"></div>
				<div class="col-md-6">
					<form:form id="mastersForm" role="form" method="post">
						<h2>Master Downloads</h2>
						<div class="form-check">
							<label> <input type="checkbox" name="check"
								value="USERACCOUNT"> <span class="label-text">User Account</span>
							</label>
						</div>
						<div class="form-check">
							<label> <input type="checkbox" name="check"
								value="PRODUCTCATEGORY"> <span class="label-text">Product
									Categories</span>
							</label>
						</div>
						<div class="form-check">
							<label> <input type="checkbox" name="check"
								value="PRODUCTPROFILE" > <span class="label-text">Product
									Profiles</span>
							</label>
						</div>
						<div class="form-check">
							<label> <input type="checkbox" name="check"
								value="TERRITORY" > <span
								class="label-text">Territories</span>
							</label>
						</div>
						<div class="form-check">
							<label> <input type="checkbox" name="check"
								value="ACCOUNT_PROFILE" > <span
								class="label-text">Account Profiles</span>
							</label>
						</div>
						<div class="form-check">
							<label> <input type="checkbox" name="check"
								value="RECEIVABLE_PAYABLE" > <span
								class="label-text">Receivables</span>
							</label>
						</div>
						<button id="dwnldMasters" type="button" class="btn btn-success">Download</button>
					</form:form>
				</div>
			</div>
			<!-- Footer -->
			<jsp:include page="/WEB-INF/jsp/fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="/WEB-INF/jsp/fragments/m_bottom_script.jsp"></jsp:include>

	<script type="text/javascript">
		var downloadContextPath = location.protocol + '//' + location.host;
		$(document).ready(function() {
			$('#dwnldMasters').on('click', function() {
				downloadMasters();
			});
		});
		function downloadMasters() {
			$('#alertMsg').hide();
			var masters = $("#mastersForm input:checkbox:checked").map(
					function() {
						return $(this).val();
					}).get();
			if (masters.length == 0) {
				alert("Please select an option to save or update");
				return;
			}
			var requestData = {};
			requestData["masters"] = masters;
			document.getElementById("mastersForm").style.display = "none";
			document.getElementById("loader").style.display = "block";
			$.ajax({
				url : downloadContextPath + "/web/masters/download",
				type : "POST",
				contentType : "application/json",
				data : JSON.stringify(requestData),
				success : function(data) {
					if ("SUCCESS" == data) {
						$('#alertMsg').find("strong").html(
								"Masters successfully downloaded.");
						$('#alertMsg').addClass("alert-success");
					} else {
						$('#alertMsg').find("strong").html(data);
						$('#alertMsg').addClass("alert-danger");
					}
					$('#alertMsg').show();
					document.getElementById("mastersForm").style.display = "block";
					document.getElementById("loader").style.display = "none";
				},
				error : function(xhr, error) {
					$('#alertMsg').find("strong").html(error);
					$('#alertMsg').addClass("alert-error");
					$('#alertMsg').show();
					document.getElementById("mastersForm").style.display = "block";
					document.getElementById("loader").style.display = "none";
				}
			});
		}
	</script>
</body>
</html>