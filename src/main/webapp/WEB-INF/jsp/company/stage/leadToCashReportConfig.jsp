<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Lead To Cash Report Config</title>
<style type="text/css">
.error {
   color: red;
}

@media ( min-width : 1360px) and (max-width: 1370px) {
   .width83 {
      width: 83%
   }
}
</style>
</head>
<body class="page-body" data-url="">
   <div class="page-container">
      <jsp:include page="/WEB-INF/jsp/fragments/m_navbar.jsp"></jsp:include>
      <div class="main-content">
         <jsp:include page="/WEB-INF/jsp/fragments/m_header_main.jsp"></jsp:include>

         <h2>Lead To Cash Report Config</h2>
         <hr />
         
         <div class="clearfix"></div>
         <hr/>
          <table class="table table-striped table-bordered of-tbl-search" id="tblLeadToCashReportConfig">
            <thead>
               <tr>
                  <th>Name</th>
                  <th>Stage </th>
                  <th>Actions</th>
               </tr>
            </thead>
            <tbody id="tBodyLeadToCashReportConfig">
             <tr id="col1"><td>COLUMN1</td><td>
             	<select id="stageCol1" name="stagetPid" class="form-control">
             	<option value="-1" >Select Stage</option>
             	<c:forEach items="${stages}" var="stage">
             		<option value="${stage.pid }">${stage.name}</option>
             	</c:forEach>
             	
             	</select>
             	</td><td><button id="btnCol1" class="btn btn-primary" onclick="LeadToCashReportConfig.saveConfig('stageCol1');">Update</button></td>
             </tr>
              <tr id="col2"><td>COLUMN2</td><td>
             	<select id="stageCol2" name="stagetPid" class="form-control">
             <option value="-1" >Select Stage</option>
             	<c:forEach items="${stages}" var="stage">
             		<option value="${stage.pid }">${stage.name}</option>
             	</c:forEach>
             	</select>
             	</td><td><button id="btnCol2" class="btn btn-primary" onclick="LeadToCashReportConfig.saveConfig('stageCol2');">Update</button></td>
             </tr>
              <tr id="col3"><td>COLUMN3</td><td>
             	<select id="stageCol3" name="stagetPid" class="form-control">
             	<option value="-1" >Select Stage</option>
             	<c:forEach items="${stages}" var="stage">
             		<option value="${stage.pid }">${stage.name}</option>
             	</c:forEach>
             	</select>
             	</td><td><button id="btnCol3" class="btn btn-primary" onclick="LeadToCashReportConfig.saveConfig('stageCol3');">Update</button></td>
             </tr>
              <tr id="col4"><td>COLUMN4</td><td>
             	<select id="stageCol4" name="stagetPid" class="form-control">
             	<option value="-1" >Select Stage</option>
             	<c:forEach items="${stages}" var="stage">
             		<option value="${stage.pid }">${stage.name}</option>
             	</c:forEach>
             	</select>
             	</td><td><button id="btnCol4" class="btn btn-primary" onclick="LeadToCashReportConfig.saveConfig('stageCol4');">Update</button></td>
             </tr>
            </tbody>
         </table> 
         <hr />
         <!-- Footer -->
         <jsp:include page="/WEB-INF/jsp/fragments/m_footer.jsp"></jsp:include>
         <spring:url value="/web/lead-to-cash-report-config" var="urlLeadToCashReportConfig"></spring:url>
      </div>
   </div>

   <jsp:include page="/WEB-INF/jsp/fragments/m_bottom_script.jsp"></jsp:include>

   <spring:url value="/resources/app/lead-to-sales/lead-to-cash-report-cofig.js"
      var="leadToCashReportConfigJs"></spring:url>
   <script type="text/javascript" src="${leadToCashReportConfigJs}"></script>
   
</body>
</html>