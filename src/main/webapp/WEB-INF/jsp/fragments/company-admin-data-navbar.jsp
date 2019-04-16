<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- ......................Employee Management........................ -->
<spring:url value="/web/designations" var="designations"></spring:url>
<spring:url value="/web/departments" var="departments"></spring:url>
<spring:url value="/web/employee-profiles" var="employeeProfiles"></spring:url>
<spring:url value="/web/employee-hierarchical-view"
	var="employeeHierarchies"></spring:url>
<spring:url value="/web/employee-user" var="employeeUser"></spring:url>
<spring:url value="/web/employeeProfile-locations"
	var="employeeLocations"></spring:url>

<!-- ......................Territory Management........................ -->
<spring:url value="/web/locations" var="locations"></spring:url>
<spring:url value="/web/location-hierarchy" var="locationsHeirarchy"></spring:url>

<!-- ......................Account Management........................ -->
<spring:url value="/web/accountTypes" var="accountTypes"></spring:url>
<spring:url value="/web/accountGroups" var="accountGroups"></spring:url>
<spring:url value="/web/accountProfiles" var="accountProfiles"></spring:url>
<spring:url value="/web/banks" var="banks"></spring:url>

<!-- ......................Product Management........................ -->
<spring:url value="/web/divisions" var="divisions"></spring:url>
<spring:url value="/web/mobile" var="mobile"></spring:url>
<spring:url value="/web/productCategories" var="productCategories"></spring:url>
<spring:url value="/web/productProfiles" var="productProfiles"></spring:url>
<spring:url value="/web/productGroups" var="productGroups"></spring:url>
<spring:url value="/web/stock-locations" var="stockLocations"></spring:url>
<spring:url value="/web/price-levels" var="priceLevels"></spring:url>
<spring:url value="/web/price-level-list" var="priceLevelList"></spring:url>
<spring:url value="/web/openingStocks" var="openingStock"></spring:url>
<spring:url value="/web/productGroupPrice" var="productGroupPrice"></spring:url>
<spring:url value="/web/productNameTextSettings"
	var="productNameTextSettings"></spring:url>


<!-- ......................Performance Management/Settings........................ -->
<spring:url value="/web/activity-user-targets" var="activityUserTargets"></spring:url>
<spring:url value="/web/set-activity-targets" var="setActivityTargets"></spring:url>
<spring:url value="/web/activity-group-user-targets"
	var="activityGroupUserTargets"></spring:url>
<spring:url value="/web/salesTargetGroups" var="salesTargetGroups"></spring:url>
<spring:url value="/web/sales-target-group-userTargets"
	var="salesTargetGroupUserTarget"></spring:url>
<spring:url value="/web/user-receipt-targets" var="userReceiptTargets"></spring:url>
<spring:url value="/web/user-monthly-sales-targets"
	var="userMonthlySalesTargets"></spring:url>

<!-- ......................Performance Management/Settings - Tasks........................ -->
<spring:url value="/web/tasks" var="tasks"></spring:url>
<spring:url value="/web/task-lists" var="taskLists"></spring:url>
<spring:url value="/web/task-groups" var="taskGroups"></spring:url>

<!-- ......................Performance Management/Settings - Schedules........................ -->
<spring:url value="/web/user-task-assignment" var="userTaskAssignment"></spring:url>
<spring:url value="/web/user-task-group-assignment"
	var="userTaskGroupAssignment"></spring:url>
<spring:url value="/web/user-task-list-assignment" var="userTaskList"></spring:url>
<spring:url value="/web/schedule-tasks" var="scheduleTasks"></spring:url>

<!-- ......................Report........................ -->
<spring:url value="/web/executive-task-executions"
	var="executiveTaskExecutions"></spring:url>
<spring:url value="/web/verification" var="verification"></spring:url>

<spring:url value="/web/accounting-vouchers" var="accountingVouchers"></spring:url>
<spring:url value="/web/dynamic-documents" var="dynamicDocuments"></spring:url>
<spring:url value="/web/dynamic-document-forms"
	var="dynamicDocumentForms"></spring:url>
<spring:url value="/web/attendance-report" var="attendanceReport"></spring:url>
<spring:url value="/web/stock-consumption-report"
	var="stockConsumptionReport"></spring:url>
<spring:url value="/web/activity-performance-report"
	var="activityPerformanceReport"></spring:url>
<spring:url value="/web/activity-hour-reporting"
	var="activityHourReporting"></spring:url>

<spring:url value="/web/receivable-payables" var="receivablePayables"></spring:url>
<spring:url value="/web/day-plans" var="dayPlans"></spring:url>
<spring:url value="/web/day-plans-execution-summary"
	var="dayPlansExecutionSummary"></spring:url>



<!-- .................Report- Sales Performance................ -->
<spring:url value="/web/primary-sales-performance"
	var="primarySalesPerformance"></spring:url>
<spring:url value="/web/product-wise-sales" var="productWiseSales"></spring:url>
<spring:url value="/web/location-wise-sales" var="locationWiseSales"></spring:url>
<spring:url value="/web/ytd-report" var="ytdReport"></spring:url>
<spring:url value="/web/user-task-execution-logs"
	var="userTaskExecutionLogs"></spring:url>


<spring:url value="/web/sales-target-vs-achieved-report"
	var="salesTargetAchieved"></spring:url>

<!-- .................Transaction................-->
<spring:url value="/web/sales-summary-achievments"
	var="achievementSummary"></spring:url>
<spring:url value="/web/inventory" var="inventory"></spring:url>

<!-- .................Voucher Transaction................-->
<spring:url value="/web/inventory-voucher-transaction"
	var="inventoryVoucherTransaction"></spring:url>
<spring:url value="/web/accounting-voucher-transaction"
	var="accountingVoucherTransaction"></spring:url>
<spring:url value="/web/other-voucher-transaction"
	var="otherVoucherTransaction"></spring:url>

<!-- ......................Utilities........................ -->
<spring:url value="/web/knowledgebase" var="knowledgebaseTags"></spring:url>
<spring:url value="/web/knowledgebase-files" var="knowledgebaseTagFiles"></spring:url>
<spring:url value="/web/notifications" var="Notification"></spring:url>
<spring:url value="/web/notifications-report" var="NotificationReport"></spring:url>

<!-- ......................Ecom Management ........................ -->
<spring:url value="/web/product-group-info-sections"
	var="productGroupInfoSections"></spring:url>
<spring:url value="/web/ecom-product-profiles" var="ecomProductProfiles"></spring:url>
<spring:url value="/web/user-account-profile" var="userAccountProfile"></spring:url>


<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">Employee Management</span></a>
	<ul>
		<li><a href="${departments}"><span class="title">Departments</span>
		</a></li>
		<li><a href="${designations}"><span class="title">Designations</span>
		</a></li>
		<li><a href="${employeeProfiles}"><span class="title">Employee
					profile</span> </a></li>
		<li><a href="${employeeHierarchies}"><span class="title">Employee
					Hierarchy</span> </a></li>
		<li><a href="${employeeUser}"><span class="title">Employee-User
					Association</span> </a></li>
		<li><a href="${employeeLocations}"><span class="title">Employee-Location
					Association</span> </a></li>
	</ul></li>
<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">Territory Management</span></a>
	<ul>
		<li><a href="${locations}"><span class="title">Locations
					\ Territory</span></a></li>
		<li><a href="${locationsHeirarchy}"><span class="title">Location
					Hierarchy</span></a></li>
	</ul></li>
<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">Account Management</span></a>
	<ul>
		<li><a href="${accountTypes}"><span class="title">Account
					Types</span> </a></li>
		<li><a href="${accountProfiles}"><span class="title">Account
					Profile</span> </a></li>
		<li><a href="${accountGroups}"><span class="title">Account
					Groups</span> </a></li>
		<li><a href="${banks}"><span class="title">Banks</span> </a></li>

	</ul></li>

<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">Product Management</span></a>
	<ul>
		<li><a href="${divisions}"><span class="title">Product
					Divisions</span> </a></li>
		<li><a href="${mobile}"><span class="title">Mobile</span></a></li>
		<li><a href="${productCategories}"><span class="title">Product
					Categories</span> </a></li>
		<li><a href="${productProfiles}"><span class="title">Product
					Profiles</span> </a></li>
		<li><a href="${productGroups}"><span class="title">Product
					Groups</span> </a></li>
		<li><a href="${stockLocations}"><span class="title">Stock
					Locations</span> </a></li>
		<li><a href="${openingStock}"><span class="title">Opening
					Stock</span> </a></li>
		<li><a href="${priceLevels}"><span class="title">Price
					Levels</span> </a></li>
		<li><a href="${priceLevelList}"><span class="title">Price
					Level List</span> </a></li>
		<li><a href="${productGroupPrice}"><span class="title">Product
					Group Price</span> </a></li>
		<li><a href="${productNameTextSettings}"><span class="title">Product
					Name Text Settings</span> </a></li>
	</ul></li>

<li class="has-sub"><a href=""> <i class="entypo-users"></i> <span
		class="title">Performance Management/Settings</span>
</a>
	<ul>
		<li class="has-sub"><a href=""> <span class="title">Tasks</span></a>
			<ul>
				<li><a href="${tasks}"><span class="title">Tasks </span> </a></li>
				<li><a href="${taskGroups}"><span class="title">Task
							Groups </span> </a></li>
				<li><a href="${taskLists}"><span class="title">Task
							Lists </span> </a></li>
			</ul></li>

		<li class="has-sub"><a href=""> <span class="title">Schedules</span></a>
			<ul>
				<li><a href="${userTaskAssignment}"><span class="title">User-Tasks</span></a></li>
				<li><a href="${userTaskGroupAssignment}"><span
						class="title">User-Task Groups</span> </a></li>
				<li><a href="${userTaskList}"><span class="title">User-Task
							List</span> </a></li>
				<li><a href="${scheduleTasks}"><span class="title">Schedule
							Tasks</span> </a></li>

			</ul></li>
		<li class="has-sub"><a href=""> <span class="title">Target
					Setting</span></a>
			<ul>
				<li class="has-sub"><a href=""> <span class="title">Activity
							Targets</span></a>
					<ul>
						<li><a href="${activityUserTargets}"><span class="title">Activity
									Targets</span> </a></li>

						<li><a href="${setActivityTargets}"><span class="title">Set
									Monthly Activity Targets</span> </a></li>
						<li><a href="${activityGroupUserTargets}"><span
								class="title">Activity Group Targets</span> </a></li>
					</ul></li>
				<li class="has-sub"><a href=""> <span class="title">Sales
							Targets</span></a>
					<ul>
						<li><a href="${salesTargetGroups}"><span class="title">Target
									Group Definition</span> </a></li>
						<li><a href="${salesTargetGroupUserTarget}"><span
								class="title">Group wise-User wise Target Setting</span> </a></li>
						<li><a href="${userMonthlySalesTargets}"><span
								class="title">Set Monthly Sales Targets</span></a></li>
					</ul></li>
				<li><a href="${userReceiptTargets}"><span class="title">User
							Receipt Targets</span></a></li>

			</ul></li>



	</ul></li>
<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">Reports</span></a>
	<ul>
		<li><a href="${executiveTaskExecutions}"><i></i> <span
				class="title">Activities\Transactions</span> </a></li>
		<li><a href="${verification}"><i></i> <span class="title">Verification</span>
		</a></li>

		<li class="has-sub"><a href=""> <span class="title">Performance
					Reports</span></a>
			<ul>
				<li><a href="${primarySalesPerformance}"> <i></i> <span
						class="title">Primary Vouchers</span></a></li>
				<li><a href="${inventoryVouchers}"> <i></i> <span
						class="title">Secondary Vouchers</span></a></li>
				<li><a href="${productWiseSales}"> <i></i> <span
						class="title">Product Wise</span></a></li>
				<li><a href="${locationWiseSales}"> <i></i> <span
						class="title">Location Wise</span></a></li>
				<li><a href="${ytdReport}"> <i></i> <span class="title">Ytd
							Report</span></a></li>
				<li><a href="${userTaskExecutionLogs}"> <i></i> <span
						class="title">User Task Execution Logs</span></a></li>
			</ul></li>
		<li><a href="${salesTargetAchieved}"> <i></i> <span
				class="title">Target Vs Achieved</span>
		</a></li>

		<li><a href="${accountingVouchers}"> <i></i> <span
				class="title">Accounting Vouchers</span>
		</a></li>
		<li><a href="${dynamicDocuments}"> <i></i> <span
				class="title">Dynamic Documents</span>
		</a></li>
		<li><a href="${dynamicDocumentForms}"> <i></i> <span
				class="title">Dynamic Document Forms</span>
		</a></li>
		<li><a href="${attendanceReport}"> <i></i> <span
				class="title">Attendance Report</span>
		</a></li>

		<li><a href="${stockConsumptionReport}"> <i></i> <span
				class="title">Stock Consumptions</span>
		</a></li>

		<li><a href="${activityPerformanceReport}"> <i></i> <span
				class="title">Activity Performance Report</span>
		</a></li>
		<li><a href="${activityHourReporting}"> <i></i> <span
				class="title">Activity Hour Reporting</span>
		</a></li>
		<li><a href="${receivablePayables}"> <i></i> <span
				class="title">Receivables Payables</span>
		</a></li>
		<li><a href="${dayPlans}"> <i></i> <span class="title">Day
					Plans</span>
		</a></li>
		<li><a href="${dayPlansExecutionSummary}"> <i></i> <span
				class="title">Day Plans Execution Summary</span>
		</a></li>
	</ul></li>

<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">Transaction</span></a>
	<ul>
		<li><a href="${achievementSummary}"><i></i> <span
				class="title">Sales Summary Achievement</span> </a></li>
		<li><a href="${inventory}"><i></i> <span class="title">Inventory</span>
		</a></li>
	</ul></li>

<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">Voucher Transaction</span></a>
	<ul>
		<li><a href="${inventoryVoucherTransaction}"><i></i> <span
				class="title">Inventory Voucher</span> </a></li>
		<li><a href="${accountingVoucherTransaction}"><i></i> <span
				class="title">Accounting Voucher</span> </a></li>
		<li><a href="${otherVoucherTransaction}"><i></i> <span
				class="title">Other Voucher</span> </a></li>
	</ul></li>

<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">Utilities</span></a>
	<ul>
		<li><a href="${knowledgebaseTags}"><span class="title">Knowledgebase</span>
		</a></li>
		<li><a href="${knowledgebaseTagFiles}"><span class="title">Knowledgebase
					Files</span> </a></li>
		<li><a href="${Notification}"><span class="title">Notifications</span>
		</a></li>
		<li><a href="${NotificationReport}"><span class="title">Notification
					Report</span> </a></li>
	</ul></li>

<li class="has-sub"><a href=""><i class="entypo-users"></i><span
		class="title">Ecom Management</span></a>
	<ul>
		<li><a href="${productGroupInfoSections}"><span class="title">Product
					Group Info Sections</span> </a></li>
		<li><a href="${ecomProductProfiles}"><span class="title">Ecom
					Product Profiles</span> </a></li>
		<li><a href="${userAccountProfile}"><span class="title">User-Account
					Profile Association</span> </a></li>
	</ul></li>

