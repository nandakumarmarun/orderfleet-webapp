<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- ......................Administration........................ -->

<spring:url value="/web/company" var="company"></spring:url>
<spring:url value="/web/trial/setup" var="trialSetUp"></spring:url>
<spring:url value="/web/features-configuration-page"
	var="featuresConfigurationPage"></spring:url>
<spring:url value="/web/tallyCompany" var="tallyCompany"></spring:url>

<li><a href="${company}"> <i class="glyphicon glyphicon-home"></i>
		<span class="title">Company Management</span>
</a></li>

<li><a href="${trialSetUp}"> <i class="entypo-users"></i> <span
		class="title">Company Trial SetUp</span>
</a></li>

<li><a href="${featuresConfigurationPage}"> <i
		class="entypo-users"></i> <span class="title">Features
			Configuration Page</span>
</a></li>
<li><a href="${tallyCompany}"> <i class="entypo-users"></i> <span
		class="title">Tally Company</span>
</a></li>