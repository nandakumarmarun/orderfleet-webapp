<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
    <jsp:include page="../fragments/m_head.jsp"></jsp:include>
    <title>SalesNrich | Internet Speed</title>
    <style type="text/css">
        .error {
            color: red;
        }
    </style>
    <spring:url
            value="/resources/assets/plugin/jstree/themes/default/style.min.css"
            var="jstreeCss"></spring:url>
    <link href="${jstreeCss}" rel="stylesheet">
</head>
<body class="page-body" data-url="">

<div class="page-container">
    <jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
    <div class="main-content">
        <jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
        <hr />
        <h2>Internet Speed</h2>
        <hr />
        <div class="row">
            <!-- Profile Info and Notifications -->
            <div class="col-md-12  clearfix">
                <form role="form" class="form-horizontal">
                    <div class="form-group">
                        <div class="col-sm-3">
                            <div class=" input-group">
									<span
                                            class='input-group-addon btn  dropdown-toggle glyphicon glyphicon-filter'
                                            data-toggle='dropdown' aria-haspopup='true'
                                            aria-expanded='false' title='filter employee'></span>
                                <div class='dropdown-menu dropdown-menu-left'
                                     style='background-color: #F0F0F0'>
                                    <div>
                                        <a class='btn btn-default dropdown-item'
                                           style='width: 100%; text-align: left;'
                                           onclick='GetDashboardEmployees(this,"Dashboard Employee","All Dashboard Employee")'>Dashboard
                                            Employee</a>
                                    </div>
                                    <div>
                                        <a class='btn btn-default dropdown-item'
                                           style='width: 100%; text-align: left;'
                                           onclick='GetAllEmployees(this,"no","All Employee")'>All
                                            Employee</a>
                                    </div>
                                    <!-- <div>
                                        <a class='btn btn-default dropdown-item'
                                            style='width: 100%; text-align: left;'
                                            onclick='GetOtherEmployees(this,"no","Other Employee")'>Other
                                            Employees</a>
                                    </div> -->
                                </div>
                                <select id="dbEmployee" name="employeePid"
                                        class="form-control">
                                    <option value="Dashboard Employee">All Dashboard
                                        Employees</option>
                                </select>
                            </div>
                        </div>
                        <div class="input-group col-sm-2">
                            <div class="col-sm-3">
                                <button type="button" class="btn btn-info entypo-search"
                                        style="font-size: 18px"
                                        onclick="InternetSpeed.filter()" title="Apply"></button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <div class="clearfix"></div>
        <hr />
        <table class="table  table-striped table-bordered">
            <thead>
            <tr>
                <th>User Name</th>
                <th>Upload Speed</th>
                <th>Download Speed</th>
                <th>Current Date & Time</th>
            </tr>
            </thead>
            <tbody  id="tbodyemployeeInternetSpeed">
            </tbody>
        </table>
        <hr />
        <!-- Footer -->
        <jsp:include page="../fragments/m_footer.jsp"></jsp:include>
        <spring:url value="/web/internetSpeed"
                    var="urlInternetSpeed"></spring:url>


    </div>
</div>
<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
<spring:url value="/resources/assets/plugin/jstree/jstree.min.js"
            var="jstreeJS"></spring:url>
<spring:url value="/resources/app/report-common-js-file.js"
            var="reportcommonjsfileJS"></spring:url>
<spring:url value="/resources/app/internet-speed.js"
            var="internetSpeed"></spring:url>
<spring:url value="/resources/app/employee-location.js"
            var="employeeProfileLocation"></spring:url>


<script type="text/javascript" src="${jstreeJS}"></script>
<script type="text/javascript" src="${reportcommonjsfileJS}"></script>
<script type="text/javascript" src="${internetSpeed}"></script>
<script type="text/javascript" src="${employeeProfileLocation}"></script>

<script type="text/javascript">
    $(document).ready(function() {
        var employeePid = getParameterByName('user-key-pid');
        getEmployees(employeePid);
        InternetSpeed.filter();
    });
</script>

</body>
</html>