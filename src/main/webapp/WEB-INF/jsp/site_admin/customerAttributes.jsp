<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
    <jsp:include page="../fragments/m_head.jsp"></jsp:include>
    <title>SalesNrich | Customer Attributes</title>
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
            <h2>Customer Attributes</h2>
            <br>
  <div class="row">
                 <div class="form-group col-sm-2">
                     <label for="dbCompany">Company:</label>
                     <select id="dbCompany" name="companyPid" class="form-control selectpicker" data-live-search="true">
                         <option value="-1">Select Company</option>
                         <c:forEach items="${companies}" var="company">
                             <option value="${company.pid}">${company.legalName}</option>
                         </c:forEach>
                     </select>
                 </div>
                 </div>
                 <div class="row col-xs-12">
                 				<div class="pull-right">
                 				<button type="button" class="btn btn-info"
                                 						onclick="showCreateAttributeModal()">Create Attribute</button>
<button id='updateButton' name='updateButton' type="button" class="btn btn-success"
                                 						>Update</button>

                 				</div>
                 			</div>
                 			<div class="row">
                            										  <div class="col-md-12 col-sm-12 clearfix">
                                                                                         <input type="radio" id="all" value="all" name="filter" checked>
                                                                                         <label for="all">All</label>&nbsp;&nbsp;
                                                                                         <input type="radio" id="selected" value="selected" name="filter">
                                                                                         <label for="selected">Selected</label>&nbsp;&nbsp;
                                                                                         <input type="radio" id="unselected" value="unselected" name="filter">
                                                                                         <label for="unselected">Unselected</label>&nbsp;&nbsp;
                                                                                     </div>
                 			<div id="myModal" class="modal">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h4 class="modal-title">Create Attribute</h4>
                                        </div>
                                        <div class="modal-body">
                                            <label for="attributeName">Attribute </label>
                                            <input type="text" id="attributeName" name="attributeName" class="form-control">
                                            <br>
                                            <label for="attributeValue">Type</label>
                                            <select id="attributeType" name="attributeType" class="form-control">
                                                    <option value="-1" disabled selected>Select Type</option>
                                                    <option value="Text box">Text box</option>

                                                </select>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                                            <button type="button" id="saveButton" class="btn btn-success">Save</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
<div id="divCustomerAttributes">
    <table class="table table-striped table-bordered of-tbl-search mt-3">
        <thead>
            <tr>
                <th><input type="checkbox" id="selectAll">Select All</th>
                <th>Attribute</th>
                <th>Type</th>
                <th>Sort Order</th>
            </tr>
        </thead>
        <tbody id="tBodyCustomerAttributes">
            <c:forEach items="${customerAttributes}" var="attribute">
                <tr>
                    <td><input type="checkbox" class="selectRow" name="customerAttributes" value="${attribute.attributePid}"></td>
                    <td>${attribute.question}</td>
                    <td>${attribute.type}</td>
                    <td><input type="text" class='sortOrderInput' id="${attribute.attributePid}" pattern="[0-9]*"  ></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>


        </div>
    </div>
    <jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
    <spring:url value="/resources/app/customer-attributes.js" var="customerAttributesJs"></spring:url>
    <script type="text/javascript" src="${customerAttributesJs}"></script>
</body>
</html>