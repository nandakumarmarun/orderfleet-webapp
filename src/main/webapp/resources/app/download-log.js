if (!this.DownloadLog) {
	this.DownloadLog = {};
}

(function() {
	'use strict';

	$(document).ready(function() {
	
		$('#btnDownload').click(function() {
			if($("#txtDate").val()!='')
			{
				downloadLog($("#txtDate").val());
			}else{
				alert("Please Select Date");
			}
		});
	});

	var downloadLogContextPath = location.protocol + '//' + location.host;

	function downloadLog(date) {
		
		$.ajax({
			url : downloadLogContextPath + "/web/downloadLog",
			type : 'GET',
			data : {
				date : date
			},
			success : function(status) {
				
			}
		});
	}

})();