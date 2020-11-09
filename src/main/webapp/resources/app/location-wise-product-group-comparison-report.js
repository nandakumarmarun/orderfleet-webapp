// Create a LocationWiseProductGroupComparisonReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.LocationWiseProductGroupComparisonReport) {
    this.LocationWiseProductGroupComparisonReport = {};
}

(function () {
    'use strict';

    var locationWiseProductGroupComparisonReportContextPath = location.protocol + '//' +
        location.host + location.pathname;

    var dashboardEmployees = [];
    var otherEmployees = [];
    var allEmployeesMap = new Object();

    $(document).ready(function () {

        getEmployees();

        $('#txtFirstFromMonth').MonthPicker({
            MonthFormat: 'M, yy',
            ShowIcon: false
        });
        $('#txtFirstToMonth').MonthPicker({
            MonthFormat: 'M, yy',
            ShowIcon: false
        });
        $('#txtSecondFromMonth').MonthPicker({
            MonthFormat: 'M, yy',
            ShowIcon: false
        });
        $('#txtSecondToMonth').MonthPicker({
            MonthFormat: 'M, yy',
            ShowIcon: false
        });

        $('input[type=month]').MonthPicker().css('backgroundColor',
            'lightyellow');

        $('#applyBtn').click(function () {
            loadLocationWiseTargetAchievedReport();
        });

    });

    function loadLocationWiseTargetAchievedReport() {
        $('#tblLocationWiseTargetAchievedReport').css('display', 'none');
        $('#loadingData').css('display', 'block');
        var productGroupPid = $("#dbProduct").val();
        if (productGroupPid == "no") {
            alert("please select product group");
        }
        var firstFromDate = $('#txtFirstFromMonth').MonthPicker('GetSelectedMonthYear');
        var firstToDate = $('#txtFirstToMonth').MonthPicker('GetSelectedMonthYear');

        var secondFromDate = $('#txtSecondFromMonth').MonthPicker('GetSelectedMonthYear');
        var secondToDate = $('#txtSecondToMonth').MonthPicker('GetSelectedMonthYear');
        // validate data

        if (firstFromDate === null) {
            alert("please select from month");
            return;
        }
        if (firstToDate === null) {
            alert("please select to month");
            return;
        }

        if (secondFromDate === null) {
            alert("please select from month");
            return;
        }
        if (secondToDate === null) {
            alert("please select to month");
            return;
        }

        $('#hLoadId').html("Loading...");
        var [firstFromMonth, firstFromYear] = firstFromDate.split('/');
        var [firstToMonth, firstToYear] = firstToDate.split('/');
        var [secondFromMonth, secondFromYear] = secondFromDate.split('/');
        var [secondToMonth, secondToYear] = secondToDate.split('/');

        firstFromDate = new Date(firstFromYear, firstFromMonth - 1, 1);
        firstToDate = new Date(firstToYear, firstToMonth, 0);

        secondFromDate = new Date(secondFromYear, secondFromMonth - 1, 1);
        secondToDate = new Date(secondToYear, secondToMonth, 0);

        $("#th_target").html(firstFromYear);
        $("#th_achived").html(secondFromYear);

        $.ajax({
            url: locationWiseProductGroupComparisonReportContextPath + "/load-data",
            type: 'GET',
            data: {
                productGroupPid: productGroupPid,
                fromFirstDate: convertLocalDateToServer(firstFromDate),
                toFirstDate: convertLocalDateToServer(firstToDate),
                fromSecondDate: convertLocalDateToServer(secondFromDate),
                toSecondDate: convertLocalDateToServer(secondToDate)
            },
            success: function (response) {
                $('#loadingData').css('display', 'none');
                if (response) {
                    $("#tblLocationWiseTargetAchievedReport").show();
                    fillTargetData(response);
                } else {
                    console.log("Location target not assigned for this product group....");
                    var trRow = "<tr style='color:red; font-size: large;'><td><b>" +
                        "Location target not assigned for this product group" +
                        "</b></td></tr>";
                    $("#tblBody").html(trRow);
                    $("#tblLocationWiseTargetAchievedReport").show();
                }
            }
        });
    }

    function fillTargetData(targetData) {
        $("#tblLocationWiseTargetAchievedReport tbody").html("");

        console.log(targetData);

        var count1 = targetData.firstMonthList.length;
        var count2 = targetData.secondMonthList.length;
        $("#th_target").removeAttr("colspan");
        $("#th_target").attr("colspan", count1);
        $("#th_achived").removeAttr("colspan");
        $("#th_achived").attr("colspan", count2);

        var newRowContent = "";
        $.each(targetData.firstMonthList, function (index, month) {
            newRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>" + month + "</td>";
        });
        $.each(targetData.secondMonthList, function (index, month) {
            newRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>" + month + "</td>";
        });
        $("#txtRowHeader").html(newRowContent);

        var performanceRow = "";
        $.each(targetData.firstProductGroupLocationTargets, function (locationName, targets) {
            var achivedRow = "";
            var achivedRow2 = "";
            var achivedTotal = 0;
            var achivedTotal2 = 0;
            for (var i = 0, size = targets.length; i < size; i++) {
                var aVolume = targets[i].achievedVolume;
                achivedTotal += aVolume;
                var aVolume1 = (aVolume).toFixed(2);
                achivedRow += "<td>" + aVolume1 + "</td>";
            }
            var achivedTotal1 = (achivedTotal).toFixed(2);


            $.each(targetData.secondProductGroupLocationTargets, function (locationName2, targets2) {
                if (locationName === locationName2) {
                    for (var i = 0, size = targets2.length; i < size; i++) {
                        var aVolume = targets2[i].achievedVolume;
                        achivedTotal2 += aVolume;
                        var aVolume1 = (aVolume).toFixed(2);
                        achivedRow2 += "<td>" + aVolume1 + "</td>";
                    }
                    var achivedTotaltemp = (achivedTotal2).toFixed(2);
                    achivedRow2 += "<td>" + achivedTotaltemp + "</td>";
                }
            })

            // if(achivedTotal!=0){
            performanceRow += "<tr><td>" + locationName + "</td>";
            achivedRow += "<td>" + achivedTotal1 + "</td>";

            performanceRow += achivedRow += achivedRow2;

            var variance = achivedTotal2 - achivedTotal;
            var variancetemp = (variance).toFixed(2);

            if (variancetemp > 0) {
                performanceRow += "<td style='color: white;background-color: green;'>" + variancetemp + "</td>";
            } else if (variancetemp == 0) {
                performanceRow += "<td style='color: black;'>" + variancetemp + "</td>";
            } else {
                performanceRow += "<td style='color: white;background-color: red;'>" + variancetemp + "</td>";
            }

            var diffPercentage = 0;
            if (achivedTotal != 0) {
                diffPercentage = (variance / achivedTotal) * 100;
            }

            diffPercentage = Math.round(diffPercentage);
            // if(diffPercentage>=100){
            // 	diffPercentage=100;
            // }else if(diffPercentage<100 && diffPercentage>0){
            // 	diffPercentage=diffPercentage;
            // }else if(diffPercentage<0){
            // 	diffPercentage=0;
            // }
            if (diffPercentage > 0) {
                performanceRow += "<td style='color: white;background-color: green;'>" + diffPercentage + "%" + "</td>";
            } else if (diffPercentage == 0) {
                performanceRow += "<td style='color: black;'>" + diffPercentage + "%" + "</td>";
            } else {
                performanceRow += "<td style='color: white;background-color: red;'>" + diffPercentage + "%" + "</td>";
            }
            performanceRow += "</tr>"
            // }
        });
        if (performanceRow == "") {
            performanceRow = "<tr style='color:red; font-size: large;'><td><b>" +
                "Location target or acheived not found for this product group" +
                "</b></td></tr>";
        }
        $("#tblBody").html(performanceRow);
    }


    function convertLocalDateToServer(date) {
        if (date) {
            return moment(date).format('YYYY-MM-DD');
        } else {
            return "";
        }
    }


    function onError(httpResponse, exception) {
        var i;
        switch (httpResponse.status) {
            // connection refused, server not reachable
            case 0:
                addErrorAlert('Server not reachable', 'error.server.not.reachable');
                break;
            case 400:
                var errorHeader = httpResponse
                    .getResponseHeader('X-orderfleetwebApp-error');
                var entityKey = httpResponse
                    .getResponseHeader('X-orderfleetwebApp-params');
                if (errorHeader) {
                    var entityName = entityKey;
                    addErrorAlert(errorHeader, errorHeader, {
                        entityName: entityName
                    });
                } else if (httpResponse.responseText) {
                    var data = JSON.parse(httpResponse.responseText);
                    if (data && data.fieldErrors) {
                        for (i = 0; i < data.fieldErrors.length; i++) {
                            var fieldError = data.fieldErrors[i];
                            var convertedField = fieldError.field.replace(
                                /\[\d*\]/g, '[]');
                            var fieldName = convertedField.charAt(0).toUpperCase() +
                                convertedField.slice(1);
                            addErrorAlert(
                                'Field ' + fieldName + ' cannot be empty',
                                'error.' + fieldError.message, {
                                    fieldName: fieldName
                                });
                        }
                    } else if (data && data.message) {
                        addErrorAlert(data.message, data.message, data);
                    } else {
                        addErrorAlert(data);
                    }
                } else {
                    addErrorAlert(exception);
                }
                break;
            default:
                if (httpResponse.responseText) {
                    var data = JSON.parse(httpResponse.responseText);
                    if (data && data.description) {
                        addErrorAlert(data.description);
                    } else if (data && data.message) {
                        addErrorAlert(data.message);
                    } else {
                        addErrorAlert(data);
                    }
                } else {
                    addErrorAlert(exception);
                }
        }
    }
})();