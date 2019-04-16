<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- ......................Administration........................ -->
<spring:url value="/web/management/users" var="usermanagement"></spring:url>
<spring:url value="/web/company" var="company"></spring:url>
<spring:url value="/web/management/websocket/tracker" var="usertracker"></spring:url>
<spring:url value="/management/metrics" var="metrics"></spring:url>
<spring:url value="/management/health" var="health"></spring:url>
<spring:url value="/management/audits" var="audits"></spring:url>
<spring:url value="/management/logs" var="logs"></spring:url>
<spring:url value="/web/copy-company" var="copyCompany"></spring:url>
<spring:url value="/web/upload-xls" var="uploadXls"></spring:url>
<spring:url value="/web/static-js-code" var="staticJsCode"></spring:url>
<spring:url value="/web/menu-item" var="menuItem"></spring:url>
<spring:url value="/web/mobile-menu-item" var="mobileMenuItem"></spring:url>
<spring:url value="/web/user-menu" var="userMenuItems"></spring:url>
<spring:url value="/web/syncOperation" var="syncOperation"></spring:url>
<spring:url value="/web/mobile-configuration" var="mobileConfiguration"></spring:url>
<spring:url value="/web/sync-operation-time" var="syncOperationTime"></spring:url>
<spring:url value="/web/document-print-email" var="documentPrintEmail"></spring:url>
<spring:url value="/web/company-user-devices" var="companyUserDevice"></spring:url>
<spring:url value="/web/user-assign-account-profile"
	var="userAssignAccountProfile"></spring:url>
<spring:url value="/web/admin/copy-data" var="copyAppData"></spring:url>
<spring:url value="/web/company-configuration"
	var="companyConfiguration"></spring:url>
<spring:url value="/web/user-remote-location" var="userRemoteLocation"></spring:url>
<spring:url value="/web/user-discontinued-status"
	var="userDiscontinuedStatus"></spring:url>
<spring:url value="/web/tallyCompany" var="tallyCompany"></spring:url>
<spring:url value="/web/partners" var="partner"></spring:url>
<spring:url value="/web/site-activities" var="activities"></spring:url>
<spring:url value="/web/site-documents" var="documents"></spring:url>

<spring:url value="/web/trial/setup" var="trialSetUp"></spring:url>
<spring:url value="/web/features-configuration-page"
	var="featuresConfigurationPage"></spring:url>

<!-- Configure company -->
<spring:url value="/web/siteadmin/copy-compnay-data"
	var="copyCompanyData"></spring:url>

<spring:url value="/web/mobileMenuItemGroups" var="mobileMenuItemGroups"></spring:url>
<spring:url value="/web/userMobileMenuItemGroups"
	var="userMobileMenuItemGroups"></spring:url>

<spring:url value="/web/companyPerformanceConfig"
	var="companyPerformanceConfig"></spring:url>

<spring:url value="/web/dynamic-report-document"
	var="dynamicReportDocument"></spring:url>

<spring:url value="/web/white-listed-devices" var="whiteListedDevices"></spring:url>

<spring:url value="/web/white-listed-devices" var="whiteListedDevices"></spring:url>

<spring:url value="/web/company-user-build-version"
	var="companyUserBuildVersion"></spring:url>

<spring:url value="/web/real-time-api" var="realTimeAPI"></spring:url>

<spring:url value="/web/user-device-key" var="userDeviceKey"></spring:url>

<spring:url value="/web/company-invoice-configuration"
	var="companyInvoiceConfiguration"></spring:url>

<spring:url value="/web/unable-to-find-locations"
	var="unableToFindLocations"></spring:url>

<spring:url value="/web/user-growth-report" var="userGrowthReport"></spring:url>

<spring:url value="/web/company-integration-module" var="companyIntegrationModule"></spring:url>


<li class="has-sub"><a href=""> <i class="entypo-users"></i> <span
		class="title">Administration</span>
</a>
	<ul>
		<li><a href="${company}"><span class="title">Company
					Management</span> </a></li>
		<li><a href="${usermanagement}"><span class="title">User
					Management</span> </a></li>
		<li><a href="${uploadXls}"> <span class="title">File
					Upload</span>
		</a></li>
		<li><a href="${staticJsCode}"><span class="title">Static
					Js Code</span> </a></li>
		<%-- <li><a href="${usertracker}"><span class="title">User
					Tracker</span> </a></li> --%>
		<li><a href="${health}"><span class="title">Health</span> </a></li>
		<li><a href="${metrics}"><span class="title">Metrics</span> </a></li>
		<li><a href="${audits}"><span class="title">Audits</span> </a></li>
		<li><a href="${logs}"><span class="title">Logs</span> </a></li>
		<li><a href="${copyCompany}"><span class="title">Copy
					Company</span> </a></li>

		<li><a href="${companyPerformanceConfig}"><span class="title">Company
					Performance Config</span> </a></li>
					
		<li><a href="${companyIntegrationModule}"><span class="title">Company
					Integration Module</span> </a></li>		
					
					
	</ul></li>
<li class="has-sub"><a href=""><i class="entypo-users"></i> <span
		class="title">Configure Company</span> </a>
	<ul>
		<li><a href="${copyCompanyData}"><span class="title">Copy
					company data</span> </a></li>
		<li><a href="${companyConfiguration}"><span class="title">
					Company Configuration</span> </a></li>
	</ul></li>
<li><a href="${companyUserDevice}"> <i class="entypo-users"></i>
		<span class="title">Company User Devices</span>
</a></li>
<li><a href="${companyUserBuildVersion}"> <i
		class="entypo-users"></i> <span class="title">Company User
			Build Versions</span>
</a></li>

<li><a href="${userDiscontinuedStatus}"> <i
		class="entypo-users"></i> <span class="title">User Discontinued</span>
</a></li>
<li><a href="${userRemoteLocation}"> <i class="entypo-users"></i>
		<span class="title">User Remote Location</span>
</a></li>
<li><a href="${menuItem}"> <i class="entypo-users"></i> <span
		class="title">Web Menu Item</span>
</a></li>
<li><a href="${userMenuItems}"> <i class="entypo-users"></i> <span
		class="title">User Web menu items</span>
</a></li>
<li><a href="${mobileMenuItem}"> <i class="entypo-users"></i> <span
		class="title">Mobile Menu Item</span>
</a></li>
<li><a href="${mobileMenuItemGroups}"> <i class="entypo-users"></i>
		<span class="title">Mobile Menu Item Groups</span>
</a></li>

<li><a href="${userMobileMenuItemGroups}"> <i
		class="entypo-users"></i> <span class="title">User Mobile Menu
			Item Group</span>
</a></li>
<li><a href="${mobileConfiguration}"> <i class="entypo-users"></i>
		<span class="title">Mobile Configuration</span>
</a></li>

<li><a href="${syncOperation}"> <i class="entypo-users"></i> <span
		class="title">TP Sync-Operation</span>
</a></li>
<li><a href="${syncOperationTime}"> <i class="entypo-users"></i>
		<span class="title">Sync Operation Time</span>
</a></li>
<li><a href="${documentPrintEmail}"> <i class="entypo-users"></i>
		<span class="title">Document Print Email</span>
</a></li>

<li><a href="${userAssignAccountProfile}"> <i
		class="entypo-users"></i> <span class="title">User Assigned
			Account-Profile</span>
</a></li>

<li><a href="${dynamicReportDocument}"> <i class="entypo-users"></i>
		<span class="title">Dynamic Report Document</span>
</a></li>
<li class="has-sub"><a href=""><i class="entypo-users"></i> <span
		class="title">Copy Application Data</span> </a>
	<ul>
		<li><a href="${copyAppData}"><span class="title">Copy
					data</span> </a></li>
	</ul></li>

<li><a href="${whiteListedDevices}"> <i class="entypo-users"></i>
		<span class="title">White Listed Devices</span>
</a></li>
<li><a href="${tallyCompany}"> <i class="entypo-users"></i> <span
		class="title">Tally Company</span>
</a></li>
<li><a href="${partner}"> <i class="entypo-users"></i> <span
		class="title">Partner</span>
</a></li>
<li><a href="${activities}"> <i class="entypo-users"></i> <span
		class="title">Activity</span>
</a></li>
<li><a href="${documents}"> <i class="entypo-users"></i> <span
		class="title">Documents</span>
</a></li>

<li><a href="${trialSetUp}"> <i class="entypo-users"></i> <span
		class="title">Company Trial SetUp</span>
</a></li>

<li><a href="${featuresConfigurationPage}"> <i
		class="entypo-users"></i> <span class="title">Features
			Configuration Page</span>
</a></li>

<li><a href="${realTimeAPI}"> <i class="entypo-users"></i> <span
		class="title">Real Time Apis</span>
</a></li>

<li><a href="${userDeviceKey}"> <i class="entypo-users"></i> <span
		class="title">User Device Key Update</span>
</a></li>

<li><a href="${companyInvoiceConfiguration}"> <i
		class="entypo-users"></i> <span class="title">Company Invoice
			Configuration</span>
</a></li>

<li><a href="${unableToFindLocations}"> <i class="entypo-users"></i>
		<span class="title">Unable To Find Locations</span>
</a></li>

<li><a href="${userGrowthReport}"> <i class="entypo-users"></i>
		<span class="title">User Growth Report</span>
</a></li>



