<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Location-Hierarchies-View</title>
<style type="text/css">
.error {
	color: red;
}
</style>
<spring:url
	value="/resources/assets/plugin/jstree/themes/default/style.min.css"
	var="jstreeCss"></spring:url>
<link href="${jstreeCss}" rel="stylesheet">
<spring:url
	value="/resources/assets/plugin/jstree/themes/default/style.css"
	var="jstreeCsss"></spring:url>
<link href="${jstreeCsss}" rel="stylesheet">
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Location-Hierarchies-View</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>


			<div class="clearfix"></div>
			<div id="edit-panel" class="view-state">
				<span id="chart-state-panel" class="radio-panel"> <input
					type="radio" class="chart-state-class" name="chart-state"
					id="rd-edit" value="edit" checked> &nbsp;<label
					for="rd-edit">View</label>
				</span>
				<div>
					<h4>
						<label class="error-msg" style="color: red;"></label>
					</h4>
				</div>
				<div id="divEdit" class="col-md-12 col-sm-12 clearfix"
					style="display: none; padding: 1%; background: #3B5998;">
					<div class="col-sm-10">
						<label class="selected-node-group">Selected Node:</label> <input
							readonly="readonly" type="text" id="selected-node"
							class="selected-node-group"> &nbsp; <label>New
							Node:</label> <select id="field_location" name="location">
							<option value="-1">Select Location</option>
							<c:forEach items="${locations}" var="location">
								<option value="${location.id}">${location.name}</option>
							</c:forEach>
						</select> &nbsp;&nbsp; <span id="node-type-panel" class="radio-panel">
							<input type="radio" name="node-type" id="rd-child"
							value="children"> &nbsp; <label for="rd-child">Child</label>&nbsp;
							<input type="radio" name="node-type" id="rd-sibling"
							value="siblings"> &nbsp;<label for="rd-sibling">Sibling</label>
						</span> &nbsp;&nbsp;
						<button type="button" id="btn-add-nodes" class="btn btn-info">Add</button>
						<button type="button" id="btn-delete-nodes" class="btn btn-danger">Delete</button>
						<button type="button" id="btn-reset" class="btn">Reset</button>
					</div>
					<div class="col-sm-2" class="pull-right">
						<button type="button" id="btn-change-parent"
							class="btn btn-success" onclick="showUpdateModel()">Change
							Parent</button>
					</div>
				</div>
			</div>
			<br> <br> <br> <br>
			<div class="form-group">
				<div id="tree-container1"></div>
			</div>
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>


			<div class="modal fade container" id="menuItemsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Parent Location</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: auto;">
							<label>current parent:</label> <input readonly="readonly"
								type="text" id="currentparent" class="form-control"> <br />
							<label>Selected Node:</label> <input readonly="readonly"
								type="text" id="selectedNode" class="form-control"> <br />
							<label>New Node:</label><select id="field_location_perant"
								class="form-control" name="location">
								<option value="-1">Select Location</option>
								<c:forEach items="${locationsall}" var="location">
									<option value="${location.id}">${location.name}</option>
								</c:forEach>
							</select>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btn-Change-parent"
								value="Update Location" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>


			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>




	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	<spring:url value="/resources/assets/plugin/jstree/jstree.min.js"
		var="jstreeJS"></spring:url>
	<spring:url
		value="/resources/assets/plugin/orgchart/flat-to-nested-js.js"
		var="flatToNestedJS"></spring:url>
	<script type="text/javascript" src="${jstreeJS}"></script>
	<script type="text/javascript" src="${flatToNestedJS}"></script>
	<script type="text/javascript">
		var rootUrl = location.protocol + '//' + location.host
				+ '/web/location-hierarchies';
		var selectednode;
		var locationdata = [];
		$(document).ready(function() {
			loadLocations();
			
		});
//load location
		function loadLocations() {
			$.ajax({
				url : rootUrl,
				method : 'GET',
				success : function(responseData) {
					console.log("Location hierarchy locaded....");
					if (responseData.length > 0) {
						buildLocationTree(responseData);
					}
				},
				error : function(xhr, error) {
					console.log("error : " + error);
				}
			});
		}

		function buildLocationTree(responseData) {
			var menuItemsTreeData = [];
			responseData.forEach(function(element) {
				var item = {};
				item.id = element.id;
				if (element.parentId == null) {
					item.parent = "#";
				} else {
					item.parent = element.parentId;

				}
				item.text = element.locationName;

				locationdata.push(item);
			});
			$('#tree-containerxfff').jstree({
				'core' : {
					'data' : locationdata
				},
				"checkbox" : {
					"three_state" : false
				}
			}).on('loaded.jstree', function(e, data) {
				$(this).jstree("open_all");
			});
			loadHerarchyView();
		}
// show Update Parent Model
		function showUpdateModel() {
			var node = selectednode;
			if (!node) {
				alert('Please select one node');
				return;
			}
			$('#menuItemsModal').modal('show');

			$('#selectedNode').val(selectednode.text);
			var parentname=findParentName(selectednode.parent)
			$('#currentparent').val(parentname);
		}

		//find parentname
		
		function findParentName(parentid){
			var parentName;
			locationdata.forEach(function (data){
				if(parentid == data.id ){
					parentName = data.text;
					}
				});
			return parentName;
			}

// radio Click 
		$('input[name="chart-state"]')
				.on(
						'click',
						function() {
							$('#edit-panel, .orgchart').toggleClass(
									'view-state');
							if ($(this).val() === 'edit') {
								$('#divEdit').show(); //show the div
								$('#tree-container1').jstree({
									'core' : {
										"initially_open" : [ "1" ],
										"check_callback" : true,
										'data' : locationdata
									},
									"checkbox" : {
										"three_state" : false
									},
									"plugins" : [ "crrm", "themes" ]
								}).on('loaded.jstree', function(e, data) {
									$(this).jstree("open_all");
								});
								$('#tree-container1')
										.on(
												"changed.jstree",
												function(e, data) {
													if (data.selected.length) {
														$(data.selected)
																.each(
																		function(
																				idx) {
																			selectednode = data.instance
																					.get_node(data.selected[idx]);
																			console
																					.log(selectednode);
																			$(
																					'#selected-node')
																					.val(
																							selectednode.text);
																		});
													}
												});
							} else {
								$('#btn-reset').trigger('click');
							}
						});

// Show  HerarchyView
		function loadHerarchyView() {
			
			if ('edit' === 'edit') {
				$('#divEdit').show(); //show the div
				$('#tree-container1').jstree({
					'core' : {
						"initially_open" : [ "1" ],
						"check_callback" : true,
						'data' : locationdata
					},
					"checkbox" : {
						"three_state" : false
					},
					"plugins" : [ "crrm", "themes" ]
				}).on('loaded.jstree', function(e, data) {
					$(this).jstree("open_all");
				});
				$('#tree-container1')
						.on(
								"changed.jstree",
								function(e, data) {
									if (data.selected.length) {
										$(data.selected)
												.each(
														function(idx) {
															selectednode = data.instance
																	.get_node(data.selected[idx]);
															console
																	.log(selectednode);
															$('#selected-node')
																	.val(
																			selectednode.text);
														});
									}
								});
			} else {
				$('#btn-reset').trigger('click');
			}
		}

		//delete node
		$('#btn-delete-nodes').on(
				'click',
				function() {
					var node = selectednode;
					if (!node) {
						alert('Please select one node');
						return;
					}
					if (confirm("Do you want to delete "+ selectednode.text+" ? ") == true) {
						$('#tree-container1').jstree(true).delete_node(
								[ selectednode.id ]);
						$('#selected-node').val('');
						$("#tree-container1").jstree().deselect_all(true);
						selectednode = null;
						updateNodeDelection();
						$(".error-msg").html("Deleted Successfully");
					} else {
						$('#selected-node').val('');
						$("#tree-container1").jstree().deselect_all(true);
						selectednode = null;
					}
				});

		//reset button
		$('#btn-reset').on('click', function() {

			$('#selected-node').val('');
			$('#node-type-panel').find('input').prop('checked', false);
			$('#rd-view').prop('checked', true); //reset edit-view
			//reset dropdown and its li
			$('#field_location').val("-1");
			$("#tree-container1").jstree().deselect_all(true);
			selectednode = null;

		});

		// ADD Node
		$('#btn-add-nodes')
				.on(
						'click',
						function() {
							var nodeVals = [];
							var selectedVal = $('#field_location').val();
							var selectedText = $('#field_location').find(
									"option:selected").text();
							var node = selectednode;
							var nodeId = selectednode.id;
							var nodeparent = selectednode.parent;
							var nodeType = $('input[name="node-type"]:checked');

							if (selectedVal < 0) {
								alert('Please select a value for new node');
								return;
							}

							if (!node) {
								alert('Please select one node');
								return;
							}

							if (!nodeType.length) {
								alert('Please select a node type');
								return;
							}
							$('#field_location').find("option:selected")
									.remove(); //remove the selected option
							$('#field_location').val("-1"); //reset drop down

							if (confirm("Do you want to Add " + selectedText
									+ " to Location Hierarchy View ? ") == true) {
								if (nodeType.val() === 'children') {

									$('#tree-container1')
											.jstree()
											.create_node(
													nodeId,
													{
														"id" : selectedVal,
														"text" : selectedText,
													},
													"last",
													function() {
														$(".error-msg")
																.html(
																		selectedText
																				+ " Added To to Location Hierarchy View");
														updateNode();
													});
								} else {
									$('#tree-container1')
											.jstree()
											.create_node(
													nodeparent,
													{
														"id" : selectedVal,
														"text" : selectedText,
													},
													"last",
													function() {
														$(".error-msg")
																.html(
																		selectedText
																				+ " Added To to Location Hierarchy View");
														updateNode();
													});
								}

							} else {
								window.location.reload();
							}
						});





		// Change parentNode
		$('#btn-Change-parent')
				.on(
						'click',
						function() {
							var childres = [];
							var selectedVal = $('#field_location_perant').val();
							var selectedText = $('#field_location_perant').find(
									"option:selected").text();
							var node = selectednode;
							var nodeId = selectednode.id;
							var nodeparent = selectednode.parent;
							var nodetext = selectednode.text;
							var childres = selectednode.children;

							var nodeParentName = findParentName(selectednode.parent);
						
							if (selectedVal < 0) {
								alert('Please select a value for new node');
								return;
							}

							if (!node) {
								alert('Please select one node');
								return;
							}
							$('#field_location_perant').find("option:selected")
									.remove(); //remove the selected option
							$('#field_location_perant').val("-1"); //reset drop down
							

							if (confirm("Do you want to Change Parent " + nodeParentName
									+ " To " + selectedText+" ? ") == true) {
								$('#tree-container1').jstree(true).delete_node(
										[ selectednode.id ]);

									$('#tree-container1')
											.jstree()
											.create_node(
													selectedVal,
													{
														"id" : nodeId,
														"text" : nodetext,
													},
													"last",
													function() {
														$(".error-msg")
																.html(
																		nodetext
																				+ " Change Parent " + nodeParentName + " To "+ selectedText );
													});
									if (childres.length !== 0) {
									
										childres.forEach(function(entry) {
											locationdata.forEach(function(entry1) {
												
												if(entry == entry1.id ){
													$('#tree-container1')
													.jstree()
													.create_node(
															entry1.parent,
															{
																"id" : entry1.id,
																"text" : entry1.text,
															},
															"last",
															function() {
																
															});
													}
											  });
										  });
										 } 
									updateNodeDelection()
							} else {
								window.location.reload();
							}
						});

		//Update node
		function updateNode() {
			var jsonNodes = $('#tree-container1').jstree(true).get_json('#', {
				flat : true
			});
			var flatData = convertToFlat(jsonNodes);
			updateHierarchy(flatData);
		}

		function updateHierarchy(newData) {
			$.ajax({
				method : 'POST',
				url : rootUrl,
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(newData),
				success : function() {
					//window.location.reload();
				},
				error : function(xhr, error) {
					console.log("Error : " + error);
				}
			});
		}

		function updateNodeDelection() {
			var jsonNodes = $('#tree-container1').jstree(true).get_json('#', {
				flat : true
			});
			var flatData = convertToFlat(jsonNodes);
			updateHierarchydelet(flatData);
		}

		function updateHierarchydelet(newData) {
			$.ajax({
				method : 'POST',
				url : rootUrl,
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(newData),
				success : function() {
					window.location.reload();
				},
				error : function(xhr, error) {
					console.log("Error : " + error);
				}
			});
		}

		//convert from nested data to flat data
		function convertToFlat(jsonData) {
			var flatArr = new Array();
			//root node
			jsonData.forEach(function(entry) {
				if (entry.id) {
					if (entry.parent === "#") {
						var location = {
							id : null,
							parentId : null
						};
						location.id = entry.id;
						location.parentId = null;
						flatArr.push(location);
					} else {
						var location = {
							id : null,
							parentId : null
						};
						location.id = entry.id;
						location.parentId = entry.parent;
						flatArr.push(location);
					}

				}
			});
			return flatArr;
		}
	</script>
</body>
</html>
