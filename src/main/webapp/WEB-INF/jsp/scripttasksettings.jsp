<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<title>SalesNrich</title>
</head>
<body id="benefits">
	<!-- Task Settings Script -->
	<script type="text/javascript">
		var dynamicDocumentTaskRequired = function(filledFormDetails) {
			var ArrayList = Java.type('java.util.ArrayList');
			var msgList = new ArrayList();
			for (var i = 0; i < filledFormDetails.length; i++) {
				var filledFormDetail = filledFormDetails[i];
				if (filledFormDetail.formElementName === 'Demo date') {
					var today = new Date();
					var convertedDate = new Date(filledFormDetail.value);
					print(convertedDate);
					if (convertedDate > today) {
						msgList.add(" ");
					}
				}
			}
			return msgList;
		}
		
		//for customer journey stage
		var customerJourneyStage = function(activitName, docName, currentStage,
				filledFormDetails) {
			var HashMap = Java.type('java.util.HashMap');
			var msgMap = new HashMap();
			var stageValue = '';
			if (activitName === 'Tele. Follow Ups'
					&& docName === 'Tele. Follow Up (Post-Demo)') {
				var oppValue = '';
				var rDemoValue = '';
				for (var i = 0; i < filledFormDetails.length; i++) {
					var filledFormDetail = filledFormDetails[i];
					if (filledFormDetail.formElementName.trim() === 'Revenue potential') {
						msgMap.put("Revenue", filledFormDetail.value);
					}
					if (filledFormDetail.formElementName.trim() === 'Remarks') {
						msgMap.put("Remarks", filledFormDetail.value);
					}
					if (filledFormDetail.formElementName.trim() === 'Opportunity') {
						oppValue = filledFormDetail.value;
					}
					if (filledFormDetail.formElementName.trim() === 'Are they ready for a demo') {
						rDemoValue = filledFormDetail.value;
					}
				}
				if(oppValue == 'Cold'){
					stageValue = 'Not interested (Lead)';
				} else if(oppValue == 'Warm') {
					if(rDemoValue == 'Yes') {
						stageValue = 'To be proposed (Prospect)';
					} else {
						stageValue = 'Follow up required (Lead)';
					}
				} else if(oppValue == 'Hot'){
					if(rDemoValue == 'Yes') {
						stageValue = 'To be proposed (Prospect)';
					} else {
						stageValue = 'Follow up required (Prospect)';
					}
				}
				msgMap.put("Stage", stageValue);
			} 
			return msgMap;
		}
		
		
	</script>
</body>
</html>