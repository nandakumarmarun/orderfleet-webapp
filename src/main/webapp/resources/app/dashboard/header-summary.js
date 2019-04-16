function loadHeaderSummary() {
	let selDate = new Date($('#txtDate').val());
	console.log("loading header summary............");
	$.ajax({
		url : appContextPath + "/web/dashboard/summary",
		type : "GET",
		data : {
			date : formatDate(selDate, 'YYYY-MM-DD'),
		},
		success : function(headerSummary) {
			console.log(headerSummary)
			$("#divDaySummary").html(createSingleTile(headerSummary.daySummaryDatas,"day"));
			$("#divWeekSummary").html(createSingleTile(headerSummary.weekSummaryDatas,"week"));
			$("#divMonthSummary").html(createSingleTile(headerSummary.monthSummaryDatas,"month"));
			
			//loading here to properly set the height of the chart
			loadDashboardSummaryChart(selDate);
		},
		error : function(xhr, error) {
			console.log("<< Error in loading dashboard header summary >>");
		}
	});
}

function createSingleTile(summaryDatas, tileName, empPid) {
	if (summaryDatas.length == 0) {
		return "<div class='tile-content'>Please configure dashboard items</div>";
	}
	let actySecHtml = "";
	let docSecHtml = "";
	let prdctSecHtml = "";
	let targetSecHtml = "";
	
	$.each(summaryDatas, function(index, smryData){
		var id = tileName + smryData.dashboardItemPid;
		if ($("#dbDashboardType").val() == "ACTIVITY") {
			if (smryData.dashboardItemType == "ACTIVITY") {
				actySecHtml += makeActivitySectionHtml(smryData, id );
			} else if (smryData.dashboardItemType == "DOCUMENT") {
				docSecHtml += makeDocumentSectionHtml(smryData, id ,empPid);
			} else if (smryData.dashboardItemType == "PRODUCT") {
				prdctSecHtml += makeProductSectionHtml(smryData, id);
			} else if (smryData.dashboardItemType == "TARGET") {
				targetSecHtml += makeTargetSectionHtml(smryData, id);
			}
		} else {
			// attendance subgroup
			docSecHtml += makeAttendanceSubgroupSectionHtml(smryData, id);
		}
		
	});
	return makeTileHtml(actySecHtml, docSecHtml, prdctSecHtml, targetSecHtml, tileName, empPid);
}

function makeActivitySectionHtml(data, id ) {
	
	return '<div class="first"><p>' + data.label + '</p><p><b id="achieved'
			+ id + '">' + data.achieved + '</b></p><p><b>' + data.scheduled
			+ '</b></p></div>';
}

function makeDocumentSectionHtml(data, id ,empId) {
	let linkToPlanSummary = appContextPath + "/web/executive-task-executions?user-key-pid="+ empId+"&document-name="+data.label;
	let circleClass = "";
	if (data.numberCircle == true) {
		circleClass = "number-circle";
	}
	return '<div class="first"><p><a href="'+linkToPlanSummary+'" style="color:inherit">' 
			+ data.label + '</a></p><p><b id="count' + id + '" class="'+ circleClass +'">' 
			+ data.count + '</b></p><p><b id="amount' + id + '">' + Math.round(data.amount) + '</b></p></div>';
}

function makeProductSectionHtml(data, id) {
	return '<div class="first"><p>' + data.label + '</p><p><b  id="volume' + id
			+ '">' + data.volume + '</b></p><p><b id="productAmount' + id
			+ '">' + Math.round(data.amount) + '</b></p></div>';
}

function makeTargetSectionHtml(data, id) {
	let load = data.targetAverageVolume;
	let targt = 0;
	let achvd = 0;
	if (data.targetType == "VOLUME") {
		targt = data.targetVolume;
		achvd = data.targetAchievedVolume;
	} else {
		targt = Math.round(data.targetAmount);
		achvd = data.targetAchievedAmount;
	}
	return '<div class="first"><p id="tbLabel' + id + '">' + data.label + ' ('
			+ load + ')</p><p><b id="tbAchieved' + id + '">' + achvd
			+ '</b></p><p><b id="tbPlanned' + id + '">' + targt
			+ '</b></p></div>';
}

function makeAttendanceSubgroupSectionHtml(data, id) {
	return '<div class="first"><p>' + data.label + '</p><p><b></b></p><p><b id="attsubgrpCount' + id + '">' + data.count
			+ '</b></p></div>';
}

function makeTileHtml(actySecHtml, docSecHtml, prdctSecHtml, targetSecHtml, tileName, empPid) {
	let tileHtml = ''; 
	if (actySecHtml != "") {
		tileHtml = appendActivitySectionHtml(actySecHtml, tileName, empPid);
	}
	if (docSecHtml != "") {
		tileHtml += appendDocumentSectionHtml(docSecHtml);	
	}
	if (prdctSecHtml != "") {
		tileHtml += appendProductSectionHtml(prdctSecHtml);
	}
	if (targetSecHtml != "") {
		tileHtml += appendTargetSectionHtml(targetSecHtml);
	}
	return tileHtml;
}

function appendActivitySectionHtml(actySecHtml, tileName, empId) {
	let linkToPlanSummary = appContextPath + "/web/day-plans-execution-summary-user-wise?user="+ empId;
	let html = '<div class="tile-content tile-content_pad"><div class="table_"><div class="head">'
			+ '<div class="first"><p class="text-center">Activities</p></div>'
			+ '<div class="second"><div><p class="text-center">Achieved</p>';
	if (tileName == "day" || tileName == "week" || tileName == "month") {
		html += '<p class="text-center">Scheduled</p>';
	} else {
		html += '<p class="text-center"><a href="' + linkToPlanSummary + '" style="color:inherit">Scheduled</a></p>';
	}
	html += '</div></div></div>' + '<div class="clearfix"></div><div class="body">' + actySecHtml + '</div></div></div>';
	return html;
}

function appendDocumentSectionHtml(docSecHtml) {
	if ($("#dbDashboardType").val() == "ACTIVITY") {
		return '<div class="tile-content tile-content_pad"><div class="table_"><div class="head">'
				+ '<div class="first"><p class="text-center">Vouchers</p></div>'
				+ '<div class="second"><div><p class="text-center">Count</p>'
				+ '<p class="text-center">Amount</p></div></div></div>'
				+ '<div class="clearfix"></div><div class="body">'
				+ docSecHtml + '</div></div></div>';
	} else {
		// attendance subgroup
		return '<div class="tile-content padding_btm tile-content_pad"><div class="table_"><div class="head">'
				+ '<div class="first"><p class="text-center">Description</p></div>'
				+ '<div class="second"><div><p class="text-center"></p>'
				+ '<p class="text-center">Count</p></div></div></div>'
				+ '<div class="clearfix"></div><div class="body">'
				+ docSecHtml + '</div></div></div>';
	}
}

function appendProductSectionHtml(prdctSecHtml) {
	return '<div class="tile-content padding_btm tile-content_pad"><div class="table_"><div class="head">'
		+ '<div class="first"><p class="text-center">Products</p></div>'
		+ '<div class="second"><div><p class="text-center">Volume</p>'
		+ '<p class="text-center">Amount</p></div></div></div>'
		+ '<div class="clearfix"></div><div class="body">'
		+ prdctSecHtml + '</div></div></div>';
}

function appendTargetSectionHtml(targetSecHtml) {
	return '<div class="tile-content padding_btm tile-content_pad"><div class="table_"><div class="head">'
		+ '<div class="first"><p class="text-center">Target</p></div>'
		+ '<div class="second"><div><p class="text-center">Achieved</p>'
		+ '<p class="text-center">Planned</p></div></div></div>'
		+ '<div class="clearfix"></div><div class="body">'
		+ targetSecHtml + '</div></div></div>';
}



