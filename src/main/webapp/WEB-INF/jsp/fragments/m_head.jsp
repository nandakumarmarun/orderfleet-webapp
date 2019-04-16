<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta name="description" content="Modern Admin Panel" />
<meta name="author" content="" />

<spring:url value="/resources/assets/images/favicon.ico" var="favicon"></spring:url>
<link rel="icon" href="${favicon}">

<spring:url
	value="/resources/assets/css/font-icons/entypo/css/entypo.css"
	var="entypoCss"></spring:url>
<link href="${entypoCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/bootstrap.css"
	var="bootstrapCss"></spring:url>
<link href="${bootstrapCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/font-awesome.css" var="fontAwesomeCss"></spring:url>
<link href="${fontAwesomeCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/neon-core.css"
	var="neonCoreCss"></spring:url>
<link href="${neonCoreCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/custom.css" var="customCss"></spring:url>
<link href="${customCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/neon-forms.css"
	var="neonFormsCss"></spring:url>
<link href="${neonFormsCss}" rel="stylesheet">

<c:if test="${applicationScope['currentcss'] != null}">
	<spring:url
		value="/resources/assets/css/${applicationScope['currentcss']}"
		var="facebookCss"></spring:url>
	<link href="${facebookCss}" rel="stylesheet">
</c:if>
<c:if test="${applicationScope['currentcss'] == null}">
	<spring:url value="/resources/assets/css/white.css" var="facebookCss"></spring:url>
	<link href="${facebookCss}" rel="stylesheet">
</c:if>
<%-- <spring:url value="/resources/assets/css/white.css" var="facebookCss"></spring:url>
<link href="${facebookCss}" rel="stylesheet"> --%>

<spring:url value="/resources/assets/js/jquery-1.11.3.min.js"
	var="jQuery"></spring:url>
<script type="text/javascript" src="${jQuery}"></script>

<spring:url value="/resources/assets/js/jquery.validate.min.js"
	var="jQueryValidate"></spring:url>
<script type="text/javascript" src="${jQueryValidate}"></script>

<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
	var="jQueryUIJs"></spring:url>
<script type="text/javascript" src="${jQueryUIJs}"></script>

<spring:url value="/resources/assets/css/jquery-ui-1.11.4.css"
	var="jqueryUICss"></spring:url>
<link href="${jqueryUICss}" rel="stylesheet">

<!-- preloader -->
<%-- <spring:url value="/resources/assets/css/preloader.css" var="preloader"></spring:url>
<link href="${preloader}" rel="stylesheet"> --%>

<!-- bootstrap date range picker -->
<spring:url value="/resources/assets/css/daterangepicker.css"
	var="daterangepickerCss"></spring:url>
<link rel="stylesheet" type="text/css" media="all"
	href="${daterangepickerCss}" />

<!-- <script
	src="http://cdnjs.cloudflare.com/ajax/libs/modernizr/2.8.2/modernizr.js"></script> -->