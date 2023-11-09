<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> <%@
taglib prefix="spring" uri="http://www.springframework.org/tags"%> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> <%@ taglib
tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
  <head>
    <jsp:include page="../fragments/m_head.jsp"></jsp:include>
    <title>SalesNrich | Kilometer Calculation</title>

    <script
      type="text/javascript"
      src="http://maps.google.com/maps/api/js?sensor=false&v=3&libraries=geometry"
    ></script>

    <style type="text/css">
      .error {
        color: red;
      }

      @media (min-width: 1360px) and (max-width: 1370px) {
        .width83 {
          width: 83%;
        }
      }
    </style>
  </head>
  <body class="page-body" data-url="">
    <div class="page-container">
      <jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
      <div class="main-content">
        <jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
        <hr />
        <h2>Non-Assigned Customers Report</h2>
        <div class="row">
          <!-- Profile Info and Notifications -->
          <div class="col-md-12 col-sm-12 clearfix">
            <form role="form" class="form-horizontal form-groups-bordered">
              <div class="form-group">
                <div class="col-sm-1">
                  <br />
                  <button id="btnSubmit" type="button" class="btn btn-info">
                    Apply
                  </button>
                </div>
                <div class="col-sm-2">
                  <br />
                  <button
                    id="btnDownload"
                    type="button"
                    class="btn btn-success">
                    Download Xls
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
        <div
          class="col-md-12 col-sm-12 clearfix"
          style="font-size: 14px; color: black"
        ></div>
        <div class="clearfix"></div>
        <hr />

        <div class="table-responsive">
          <table class="table table-striped table-bordered" id="tblTaskToDo">
            <thead>
              <tr>
                <th>Customers</th>
                <th>Activity</th>
              </tr>
            </thead>
            <tbody id="tBodyTaskToDo"></tbody>
          </table>
        </div>

        <hr />
        <!-- Footer -->
        <jsp:include page="../fragments/m_footer.jsp"></jsp:include>
      </div>
    </div>
    <jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

    <%--
    <spring:url
      value="/resources/assets/js/table2excel.js"
      var="table2excel"
    ></spring:url>
    <script type="text/javascript" src="${table2excel}"></script>
    --%>

    <!-- tableExport.jquery.plugin -->
    <spring:url
      value="/resources/assets/js/tableexport/xlsx.core.min.js"
      var="jsXlsx"
    ></spring:url>
    <spring:url
      value="/resources/assets/js/tableexport/FileSaver.min.js"
      var="fileSaver"
    ></spring:url>
    <spring:url
      value="/resources/assets/js/tableexport/tableexport.min.js"
      var="tableExport"
    ></spring:url>
    <script type="text/javascript" src="${jsXlsx}"></script>
    <script type="text/javascript" src="${fileSaver}"></script>
    <script type="text/javascript" src="${tableExport}"></script>

    <spring:url
      value="/resources/app/day-plan-to-do.js"
      var="TaskToDoJs"
    ></spring:url>
    <script type="text/javascript" src="${TaskToDoJs}"></script>
  </body>
</html>
