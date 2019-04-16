//TODO:add a alert box to show server side errors

'use strict';

(function($){
  $(function() {
	  
	  var rootUrl = location.protocol + '//' + location.host + '/web/employee-hierarchical-data';
	  var data;
	  
	  //on-load fetch employee hierarchy
	  $.ajax({
          url:  rootUrl,
          method: 'GET',
          beforeSend: function(){
        	$("#spinner").show();  
          },
          success: function(responseData){
        	//console.log(JSON.stringify(responseData));
          	buildLocationTree(responseData);
          },
          complete: function(){
        	  $("#spinner").hide();
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
			  'nodeTitle': 'designationName',
			  'nodeContent': 'employeeName',
			  'nodeID': 'id',
			  'panzoom': true,
			  'createNode': function($node, data) {
		    	  $node.on('click', function(event) {
		    		  if (!$(event.target).is('.edge')) {
		    			  window.location = rootUrl + "/employee/" + data.employeePid;
		    		  }
		    	  });
		      }
		  });
	  }
	  
	  
  });
})(jQuery);