<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- ......................Document........................ -->
<spring:url value="/web/documents" var="documents"></spring:url>

<spring:url value="/web/formElements" var="formElements"></spring:url>
<spring:url value="/web/forms" var="forms"></spring:url>
<spring:url value="/web/document-forms" var="documentForms"></spring:url>
<spring:url value="/web/document-inventory-voucher-columns"
	var="documentInventoryVoucherColumns"></spring:url>
<spring:url value="/web/document-product-categories"
	var="documentProductCategories"></spring:url>
<spring:url value="/web/document-product-groups"
	var="documentProductGroups"></spring:url>
<spring:url value="/web/document-stock-location-source"
	var="documentStockLocationSource"></spring:url>
<spring:url value="/web/document-stock-location-destination"
	var="documentStockLocationDestination"></spring:url>
<spring:url value="/web/document-accounting-voucher-columns"
	var="documentAccountingVoucherColumns"></spring:url>
<spring:url value="/web/reference-documents" var="ReferenceDocuments"></spring:url>
<spring:url value="/web/income-expense-heads" var="incomeExpenseHeads"></spring:url>

<spring:url value="/web/documentApprovalLevels"
	var="documentApprovalLevels"></spring:url>
<spring:url value="/web/document-approval-levels-order"
	var="documentApprovalLevelOrder"></spring:url>

<!-- ......................Activity........................ -->
<spring:url value="/web/activities" var="activities"></spring:url>
<spring:url value="/web/activity-groups" var="activityGroups"></spring:url>

<!-- ......................User wise feature configuration........................ -->
<spring:url value="/web/user-activities" var="userActivities"></spring:url>
<spring:url value="/web/user-activity-groups" var="userActivityGroups"></spring:url>
<spring:url value="/web/user-documents" var="userDocuments"></spring:url>
<spring:url value="/web/user-favourite-documents"
	var="userFavouriteDocuments"></spring:url>

<!-- ......................User wise data configuration........................ -->
<spring:url value="/web/user-product-groups" var="userProductGroups"></spring:url>
<spring:url value="/web/user-product-categories"
	var="userProductCategories"></spring:url>
<spring:url value="/web/user-stock-locations" var="userStockLocations"></spring:url>
<spring:url value="/web/user-price-levels" var="userPriceLevels"></spring:url>

<!-- ......................Competitor Activity........................ -->
<spring:url value="/web/competitor-profiles" var="competitorProfiles"></spring:url>
<spring:url value="/web/price-trend-products" var="priceTrendProducts"></spring:url>
<spring:url value="/web/price-trend-product-groups"
	var="priceTrendProductGroups"></spring:url>
<spring:url value="/web/price-trend-configuration"
	var="priceTrendConfiguration"></spring:url>
<spring:url value="/web/competitor-price-trends"
	var="competitorPriceTrends"></spring:url>

<!-- ......................Setting\Configurations........................ -->
<spring:url value="/web/dashboard-users" var="dashboardUsers"></spring:url>
<spring:url value="/web/dashboard-activities" var="dashboardActivities"></spring:url>
<spring:url value="/web/receipt_documents" var="ReceiptDocument"></spring:url>
<spring:url value="/web/secondary-sales-documents"
	var="SecondarySalesDocument"></spring:url>
<spring:url value="/web/primary-sales-documents"
	var="PrimarySalesDocument"></spring:url>
<spring:url value="/web/taskSettings" var="taskSettings"></spring:url>
<spring:url value="/web/taskUserSettings" var="taskUserSettings"></spring:url>
<spring:url value="/web/form-element-master-type"
	var="formElementMasterType"></spring:url>



<li class="has-sub"><a href=""> <i class="entypo-users"></i> <span
		class="title">Document \ Voucher Management</span>
</a>
	<ul>
		<li><a href="${documents}"><span class="title">Document
					Profile</span> </a></li>
		<li><a href="${ReferenceDocuments}"><span class="title">Config
					Reference Document</span> </a></li>
		<li><a href="${documentApprovalLevels}"><span class="title">Document
					Approval Levels</span> </a></li>
		<li><a href="${documentApprovalLevelOrder}"><span
				class="title">Document Approval Levels Order</span> </a></li>
		<li class="has-sub"><a href=""> <span class="title">Dynamic
					Document Configuration</span></a>
			<ul>
				<li><a href="${formElements}"><span class="title">Form
							components (Questions)</span> </a></li>
				<li><a href="${forms}"><span class="title">Forms</span> </a></li>
				<li><a href="${documentForms}"><span class="title">Document-Form
							Association</span> </a></li>
			</ul></li>
		<li class="has-sub"><a href=""> <span class="title">Inventory
					Voucher \ Document Configuration</span></a>
			<ul>
				<li><a href="${documentInventoryVoucherColumns}"><span
						class="title">Voucher \ Document Columns</span> </a></li>
				<li><a href="${documentProductCategories}"><span
						class="title">Product Category Association</span> </a></li>
				<li><a href="${documentProductGroups}"><span class="title">Product
							Group Association</span> </a></li>
				<li><a href="${documentStockLocationSource}"><span
						class="title">Stock location source</span> </a></li>
				<li><a href="${documentStockLocationDestination}"><span
						class="title">Stock location destination</span> </a></li>
			</ul></li>
		<li class="has-sub"><a href=""> <span class="title">Accounts
					Vouchers \ Documents Configuration</span></a>
			<ul>
				<li><a href="${documentAccountingVoucherColumns}"><span
						class="title">Voucher \ Document Columns</span> </a></li>
				<li><a href="${incomeExpenseHeads}"><span class="title">Income
							Expense Heads</span> </a></li>
			</ul></li>
	</ul></li>
<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">Activities \ Features</span></a>
	<ul>
		<li><a href="${activities}"><span class="title">Activity
					\ Feature Profile</span> </a></li>
		<li><a href="${activityGroups}"><span class="title">Activity
					Groups</span> </a></li>
	</ul></li>

<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">User wise feature configuration</span></a>
	<ul>
		<li><a href="${userActivities}"><span class="title">User-Activities
					Association</span> </a></li>
		<li><a href="${userActivityGroups}"><span class="title">User-Activity
					Group Association</span> </a></li>
		<li><a href="${userDocuments}"><span class="title">User-Documents
					Association</span> </a></li>
		<li><a href="${userFavouriteDocuments}"><span class="title">User-Favourite
					Documents</span> </a></li>

	</ul></li>

<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">User wise data configuration</span></a>
	<ul>
		<li><a href="${userProductGroups}"><span class="title">User-Product
					Group Association</span> </a></li>
		<li><a href="${userProductCategories}"><span class="title">User-Product
					Category Association</span> </a></li>
		<li><a href="${userStockLocations}"><span class="title">User-Stock
					Locations Association</span> </a></li>
		<li><a href="${userPriceLevels}"><span class="title">User-Price
					Level Association</span> </a></li>
	</ul></li>

<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">Competitor Activity</span></a>
	<ul>
		<li><a href="${competitorProfiles}"><span class="title">Competitor
					Profiles</span> </a></li>
		<li><a href="${priceTrendProducts}"><span class="title">Price
					Trend Products</span> </a></li>
		<li><a href="${priceTrendProductGroups}"><span class="title">Price
					Trend Product Groups</span> </a></li>
		<li><a href="${priceTrendConfiguration}"><span class="title">Price
					Trend Configuration</span> </a></li>
		<li><a href="${competitorPriceTrends}"><span class="title">Competitor
					Price Trends </span> </a></li>
	</ul></li>

<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">Setting\Configurations</span></a>
	<ul>
		<li><a href="${dashboardUsers}"><span class="title">Dashboard
					Users</span> </a></li>
		<li><a href="${dashboardActivities}"><span class="title">Dashboard
					Activities</span> </a></li>
		<li><a href="${ReceiptDocument}"><span class="title">Receipt
					Document</span> </a></li>
		<li><a href="${SecondarySalesDocument}"><span class="title">Secondary
					Sales Document</span> </a></li>
		<li><a href="${PrimarySalesDocument}"><span class="title">Primary
					Sales Document</span> </a></li>
		<li><a href="${taskSettings}"><span class="title">Task
					Settings</span> </a></li>
		<li><a href="${taskUserSettings}"><span class="title">Task
					User Settings</span> </a></li>
		<li><a href="${formElementMasterType}"><span class="title">Form
					Element Master Type</span> </a></li>


	</ul></li>

