//TODO:add a alert box to show server side errors

'use strict';

(function($){
  $(function() {
	  
	  var rootUrl = location.protocol + '//' + location.host + '/web/location-hierarchies';
	  var data;

	  //on-load fetch location hierarchy
	  $.ajax({
          url:  rootUrl,
          method: 'GET',
          success: function(responseData){
        	console.log("Location hierarchy locaded....");
        	$('#divRootLocation').hide();
        	if(responseData.length > 0){   
        		buildLocationTree(responseData);
        	} else {
        		//show button to add root location
        		$('#divRootLocation').show();
        	}
          },
          error: function(xhr, error){
          	console.log("error : " + error);
          }
      });
	  
	  function buildLocationTree(responseData){
		  var flatToNested = new FlatToNested({id : 'id',parent: 'parentId'});
		  data = flatToNested.convert(responseData);
		  $('#chart-container').orgchart({
			  'data' : data,
			  'nodeTitle': 'locationName',
			  //'nodeContent': 'locationName',
			  'nodeID': 'locationId',
			  'panzoom': true
		  });
	  }
	  
	  //start : edit org-chart ********************************************* 
	  
	  //edit or view radio button
	  $('input[name="chart-state"]').on('click', function() {
		  $('#edit-panel, .orgchart').toggleClass('view-state');
		  if ($(this).val() === 'edit') {
			  $('#divEdit').show(); //show the div
			  $('#chart-container').html(''); //clear the old chart
			  $('#chart-container').orgchart({ //recreate chart
				  'data' : data,
				  'nodeContent': 'locationName',
			      'nodeID': 'locationId',
			      'panzoom': true,
			      'draggable': true,
			      'createNode': function($node, data) {
			    	  $node.on('click', function(event) {
			    		  if (!$(event.target).is('.edge')) {
			    			  $('#selected-node').val(data.locationName).data('node', $node);
			    		  }
			    	  });
			      }
			  }).on('click', '.orgchart', function(event) {
				  if (!$(event.target).closest('.node').length) {
					  $('#selected-node').val('');
				  }
			  });
		  } else {
			  $('#btn-reset').trigger('click');
		  }
	  });
	  
	  //Add new node button
	  $('#btn-add-nodes').on('click', function() {
		  var nodeVals = [];
		  var selectedVal = $('#field_location').val();
		  var selectedText = $('#field_location').find("option:selected").text();
		  var $node = $('#selected-node').data('node');
		  var nodeType = $('input[name="node-type"]:checked');
		  
		  if(selectedVal < 0){
			  alert('Please select a value for new node');
			  return;
		  }
		  
		  if (!$node) {
			  alert('Please select one node in orgchart');
			  return;
		  }
		  
		  if (!nodeType.length) {
			  alert('Please select a node type');
			  return;
		  }
		  
		  var location = {
				  id: selectedVal,
				  locationName: selectedText,
				  relationship: null
		  };
		  
		  nodeVals.push(location);
		  $('#field_location').find("option:selected").remove(); //remove the selected option
		  $('#field_location').val("-1"); //reset drop down
		  
		  
		  if (nodeType.val() === 'siblings') {
			  location.relationship = '110';
			  $('#chart-container').orgchart('addSiblings', $node,{'siblings': nodeVals.map(function(location) { return location; })
			  });
		  } else {
			  var hasChild = $node.parent().attr('colspan') > 0 ? true : false;
			  if (!hasChild) {
				  location.relationship = nodeVals.length > 1 ? '110' : '100';
				  $('#chart-container').orgchart('addChildren', $node, {
					  'children': nodeVals.map(function(location) {
						  return location;
					  })
				  }, $.extend({}, $('#chart-container').data('orgchart').options, { depth: 0 }));
			  } else {
				  location.relationship = '110';
				  $('#chart-container').orgchart('addSiblings', $node.closest('tr').siblings('.nodes').find('.node:first'),{ 'siblings': nodeVals.map(function(location) { return location; })
			      });
			  }
		  }
	  });
	  
	  //delete node
	  $('#btn-delete-nodes').on('click', function() {
		  var $node = $('#selected-node').data('node');
		  if (!$node) {
			  alert('Please select one node in orgchart');
			  return;
		  }
		  $('#chart-container').orgchart('removeNodes', $node);
	      $('#selected-node').data('node', null);
	  });
	  
	  //reset button
	  $('#btn-reset').on('click', function() {
		  $('#chart-container').html(''); //clear the old chart
		  $('#chart-container').orgchart({ //recreate original chart
			  'data' : data,
			  'nodeContent': 'locationName',
	      	  'nodeID': 'locationId',
	      	  'panzoom': true
		  });
		  $('.orgchart').trigger('click');
		  $('#new-nodelist').find('input:first').val('').parent().siblings().remove();
		  $('#node-type-panel').find('input').prop('checked', false);
		  $('#rd-view').prop('checked', true); //reset edit-view
		  //reset dropdown and its li
		  $('#field_location').val("-1");
		  $("#new-nodelist").html('');
		  $('#divEdit').hide(); //hide the div
	  });
	  
	  $('#btn-update').on('click', function() {
		  var hierarchy = $('#chart-container').orgchart('getHierarchy');
		  var flatData = convertToFlat(hierarchy);
		  updateHierarchy(flatData);
	  });
	  
	  //end : edit org-chart *********************************************** 
	  
	  function updateHierarchy(newData){
		  $.ajax({
			  method: 'POST',
			  url: rootUrl,
			  contentType: "application/json; charset=utf-8",
			  data: JSON.stringify(newData),
			  success:  function(){
				  window.location.reload(); 
			  },
			  error: function(xhr, error){
				  console.log("Error : " + error);
			  }
		  });
	  }
	  
	  //convert from nested data to flat data
	  function convertToFlat(jsonData) {
		  var location = {
				  id: null,
				  parentId: null
		  };
		  var flatArr = new Array();
		  
		  //root node
		  if(jsonData.id) {
			  location.id = jsonData.id;
			  location.parentId = null;
			  flatArr.push(location);
			  if(jsonData.children){
				  recursiveFunction(jsonData.id, jsonData.children);
			  }
		  }
		  
		  function recursiveFunction(parentId, data) {
			  data.forEach(function(entry) {
				  location = {
						  id: null,
						  parentId: null
				  }
				  location.id = entry.id;
				  location.parentId = parentId;
				  flatArr.push(location);
			  });
			  
			  for (var i = 0; i < data.length; i++) {
				  if( data[i].children ){
					  recursiveFunction(data[i].id, data[i].children);
				  }
			  }
		  }
		  return flatArr;
	  }
	  
  });
})(jQuery);