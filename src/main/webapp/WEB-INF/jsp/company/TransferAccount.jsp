<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
    <jsp:include page="../fragments/m_head.jsp"></jsp:include>
    <title>SalesNrich | Transfer Account</title>
    <style type="text/css">
           .error {color: red;}
                .form-group {display: inline-block; margin-right: 10px; vertical-align: top;}
                .mt-3 {margin-top: 20px;}
                .text-center {text-align: center;}
                .row {margin-bottom: 10px;}
                .btn-success {display: inline-block; vertical-align: top;}
                .table {clear: both;}
                .loading-symbol {display: none; text-align: center; margin-top: 20px; font-style: italic; font-size: 18px; color: #555;}
    </style>
</head>
<body class="page-body" data-url="">
    <div class="page-container">
        <jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
        <div class="main-content">
            <jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
            <hr />
            <h2>Transfer Account</h2>
            <br>
            <div class="row">
                <div class="form-group col-sm-2">
                    <label for="dbUser">User:</label>
                    <select id="dbUser" name="userPid" class="form-control selectpicker" onchange="window.TransferAccount.getLocation()" data-live-search="true">
                        <option value="-1">Select User</option>
                        <c:forEach items="${users}" var="user">
                            <option value="${user.pid}">${user.firstName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group col-sm-3">
                    <label for="dbLocation">Source Location:</label>
                    <select id="dbLocation" name="locationPid" class="form-control" >
                        <option value="-1">Select Location</option>
                    </select>
                </div>
                <div class="form-group col-sm-3">
                    <label for="locationDropdown">Target Location:</label>
                    <select id="locationDropdown" name="locationDropdown" class="form-control selectpicker" data-live-search="true">
                        <option value="-1">Set New Location</option>
                        <c:forEach items="${locations}" var="location">
                            <option value="${location.pid}">${location.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group col-sm-3 mt-3">
                 <button type="button" class="btn btn-orange" onclick="window.TransferAccount.onChangeLocation();">Load</button>
               </div>
                <div class="form-group col-sm-4 mt-4">

                                    <button id='transferButton' name='transferButton' type="button" class="btn btn-success">Transfer to New Location</button>
                                </div>
                </div>

            <div id="loadingSymbol" style="display: none; text-align: center; margin-top: 20px; font-style: italic; font-size: 18px; color: #555;">
                <!-- Add your loading symbol here, such as a spinning icon or a progress bar -->
                Loading........ please wait... Do not refresh the page.
            </div>


            <div id="result-container"></div>
            <table class="table table-striped table-bordered of-tbl-search mt-3">
                <thead>
                    <tr>
                        <th><input type="checkbox" id="selectAll">Select All</th>
                        <th>Name</th>
                        <th>Address</th>
                        <th>Location</th>
                        <th>Created By</th>
                    </tr>
                </thead>
                <tbody id="tBodyAccountProfile"></tbody>
            </table>
        </div>
    </div>
    <jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
    <spring:url value="/resources/app/TransferAccount.js" var="TransferAccountJs"></spring:url>
    <script type="text/javascript" src="${TransferAccountJs}"></script>
</body>
</html>
