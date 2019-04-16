<!DOCTYPE html>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>SalesNrich | OTP Verification</title>
<spring:url value="/resources/assets/css/bootstrap.css"
	var="bootstrapCss"></spring:url>
<link href="${bootstrapCss}" rel="stylesheet">
<style type="text/css">
     h1{
        text-align: center; 
        color: black;
        }
    .fah{
		min-height: 20px;
		padding: 19px;
		margin-bottom: 20px;
		margin-top: 10%;
		background-color: white;
		border: 1px solid #e3e3e3;
		border-radius: 3px;
		-webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.05);
		box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.05);
		color: black;
		}
	p{
		font-size: medium;
	}
	li.borderless {
  		/* border: 0 none; */
  		padding-bottom: 0px;
	}
	a{
		color:blue;
	}
 </style>
 </head>
 
 <body>
 
 	<div class="col-lg-3">
	</div>
	<div class="col-lg-6 fah">
		<div class="row">
			<div class="col-sm-12">
				<h1>Company Successfully Registered!</h1>
				<h3>Instructions</h3>
				<ul class="list-group">
					<li class="list-group-item borderless">
						<p>1. Goto <a href="https://play.google.com/store/apps/details?id=com.aitrich.android.orderpro&hl=en" target="_blank">here</a> and install mobile app.</p>
					</li>
				</ul>
				<div class="col-sm-3 form-group"  style="margin-left:40%">
					<a href="/login" id="signIn" class="btn btn-md btn-info">LOGIN</a>
				</div>
			</div>
		</div>
	</div>
 	
 	
 	
 	
 	
 <spring:url value="/resources/assets/js/jquery-1.11.3.min.js"
	var="jQuery"></spring:url>
<script type="text/javascript" src="${jQuery}"></script>

 </body>
</html>