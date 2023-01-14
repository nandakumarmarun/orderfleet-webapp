<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<title>SalesNrich | Locations Hierarchy Edit</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Employee Hierarchy</h2>
			<hr />

			<c:if test="${empty employee}">
				<div class="alert alert-danger alert-dismissible" role="alert">
					<button type="button" class="close" data-dismiss="alert"
						aria-label="alert">
						<span aria-hidden="true">x</span>
					</button>
					<strong>Employee not Found.</strong>
				</div>
			</c:if>
			<div class="alert alert-danger alert-dismissible" role="alert"
				style="display: none;">
				<button type="button" class="close" onclick="$('.alert').hide();"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<p></p>
			</div>
			<br />

			<table class="table table-striped table-bordered ">
				<tr>
					<td>Parent</td>
					<td><label> <select id="parent" class="form-control">
								<option value="-1">-- parent --</option>
								<c:forEach items="${employees}" var="employee">
									<option value="${employee.pid}">${employee.name}</option>
								</c:forEach>
						</select></label></td>
				</tr>
				<tr>
					<td>Name</td>
					<td><span>${employee.name}</span></td>
					<td rowspan="7" align="center"><img src="${image}"
						class="img-circle" width="80" /></td>
				</tr>
				<tr>
					<td>LoginId</td>
					<td><span>${employee.userLogin}</span></td>
				</tr>
				<tr>
					<td>Designation</td>
					<td><span>${employee.designationName}</span></td>
				</tr>
				<tr>
					<td>Department</td>
					<td><span>${employee.departmentName}</span></td>
				</tr>
				<tr>
					<td>Phone</td>
					<td><span>${employee.phone}</span></td>
				</tr>
				<tr>
					<td>Email</td>
					<td><span>${employee.email}</span></td>
				</tr>
				<tr>
					<td>Address</td>
					<td><span>${employee.address}</span></td>
				</tr>
			</table>
			<div class="pull-right">
				<button id="btn-delete" type="button" class="btn btn-danger">Delete</button>
				<button id="btn-update" type="button" class="btn btn-success">Update</button>
			</div>
		</div>
	</div>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	
	<script type="text/javascript">
	$( document ).ready(function() {
		$("#parent").val('${parentEmployeePid}');
		
		$('#btn-update').on('click', function() {
			var parentId = $('#parent').val();
			
			if(parentId < 0){
				alert("Please select a parent employee.");
				return;
			}
			
			$.ajax({
				method: 'PUT',
				url: '${pageContext.request.contextPath}' + '/web/employee-hierarchical-view',
	            contentType: 'application/x-www-form-urlencoded;',
	            data: {employeeId : '${employee.pid}',parentId : parentId},
	            success:  function(data){
	            	window.location = '${pageContext.request.contextPath}' + '/web/employee-hierarchical-view'; 
	            },
	            error: function(xhr, error){
	            	onError(xhr, error);
	            }
			});
		});
		
		$('#btn-delete').on('click', function() {
			if (confirm('Are you sure?')) {
				$.ajax({
					method: 'DELETE',
					url: '${pageContext.request.contextPath}' + '/web/employee-hierarchical-view/' + '${employee.pid}',
		            contentType: 'application/x-www-form-urlencoded;',
		            success:  function(data){
		            	window.location = '${pageContext.request.contextPath}' + '/web/employee-hierarchical-view'; 
		            },
		            error: function(xhr, error){
		            	onError(xhr, error);
		            }
				});
			}
		});
		
		function addErrorAlert (message, key, data) {
			$(".alert > p").html(message);
			$('.alert').show();
	    }
		
		function onError(httpResponse, exception) {
			var i;
			switch (httpResponse.status) {
				// connection refused, server not reachable
		        case 0:
		            addErrorAlert('Server not reachable','error.server.not.reachable');
		            break;
		        case 400:
		        	var errorHeader = httpResponse.getResponseHeader('X-orderfleetwebApp-error');
		        	var entityKey = httpResponse.getResponseHeader('X-orderfleetwebApp-params');
		        	if (errorHeader) {
	                    var entityName = entityKey;
	                    addErrorAlert(errorHeader, errorHeader, {entityName: entityName});
	                } else if(httpResponse.responseText) {
	                	var data = JSON.parse(httpResponse.responseText);
	                	if (data && data.fieldErrors) {
	                		for (i = 0; i < data.fieldErrors.length; i++) {
	                			var fieldError = data.fieldErrors[i];
	                			var convertedField = fieldError.field.replace(/\[\d*\]/g, '[]');
	                			var fieldName = convertedField.charAt(0).toUpperCase() + convertedField.slice(1);
	                			addErrorAlert('Field ' + fieldName + ' cannot be empty', 'error.' + fieldError.message, {fieldName: fieldName});
	                		}
	                	}else if (data && data.message) {
	                		addErrorAlert(data.message, data.message, data);
	                	} else {
	                		addErrorAlert(data);
	                	}
	                } else {
	                	addErrorAlert(exception);
	                }
	                break;
		        default:
		        	if(httpResponse.responseText) {
		        		var data = JSON.parse(httpResponse.responseText);
		        		if (data && data.description) {
		        			addErrorAlert(data.description);
		        		}else if (data && data.message) {
			        		addErrorAlert(data.message);
			        	} else {
			        		addErrorAlert(data);
			        	}
		        	}else {
		        		addErrorAlert(exception);
		        	} 
			}
		}
		
	});
	</script>

</body>
</html>