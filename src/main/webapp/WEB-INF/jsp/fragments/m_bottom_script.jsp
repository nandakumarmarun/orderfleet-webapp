<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:url value="/resources/assets/js/gsap/TweenMax.min.js"
	var="tweenMax"></spring:url>
<script type="text/javascript" src="${tweenMax}"></script>

<spring:url value="/resources/assets/js/bootstrap.js" var="btStrap"></spring:url>
<script type="text/javascript" src="${btStrap}"></script>

<spring:url value="/resources/assets/js/resizeable.js" var="resizeable"></spring:url>
<script type="text/javascript" src="${resizeable}"></script>

<spring:url value="/resources/assets/js/neon-api.js" var="neonApi"></spring:url>
<script type="text/javascript" src="${neonApi}"></script>

<spring:url value="/resources/assets/js/jquery.sparkline.min.js"
	var="sparkline"></spring:url>
<script type="text/javascript" src="${sparkline}"></script>

<spring:url value="/resources/assets/js/icheck/icheck.min.js"
	var="icheck"></spring:url>
<script type="text/javascript" src="${icheck}"></script>

<spring:url value="/resources/assets/js/neon-custom.js" var="neonCustom"></spring:url>
<script type="text/javascript" src="${neonCustom}"></script>

<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
<script type="text/javascript" src="${momentJs}"></script>

<spring:url value="/resources/assets/js/daterangepicker.js" var="daterangepickerJs"></spring:url>
<script type="text/javascript" src="${daterangepickerJs}"></script>

<spring:url value="/resources/assets/orderfleet-app.js" var="orderfleetApp"></spring:url>
<script type="text/javascript" src="${orderfleetApp}"></script>
<spring:url value="/resources/app/app-common.js" var="appCommonJs"></spring:url>
<script type="text/javascript" src="${appCommonJs}"></script>


<script type="text/javascript">
	$(window).load(function() {
		// Animate loader off screen
		$(".se-pre-con").fadeOut("slow");
	});
</script>




