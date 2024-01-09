// Create a SalesTargetAchievedReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SalesTargetAchievedReport) {
  this.SalesTargetAchievedReport = {};
}

(function () {
  "use strict";

  var kmdistancefarePath =
    location.protocol + "//" + location.host + location.pathname;

  var dashboardEmployees = [];
  var otherEmployees = [];
  var allEmployeesMap = new Object();

  $(document).ready(function () {
    getEmployees();

    $("#txtFromMonth").MonthPicker({
      MonthFormat: "M, yy",
      ShowIcon: false,
    });
    $("#txtToMonth").MonthPicker({
      MonthFormat: "M, yy",
      ShowIcon: false,
    });

    $("input[type=month]").MonthPicker().css("backgroundColor", "lightyellow");

    $("#applyBtn").click(function () {
      loadSalesTargetAchievedReport();
    });
    $('#btnDownload').on('click', function() {
    			var tblAccountProfile = $("#tblAccountProfile tbody");
    			if (tblAccountProfile.children().length == 0) {
    				alert("no values available");
    				return;
    			}
    			if (tblAccountProfile[0].textContent == "No data available") {
    				alert("no values available");
    				return;
    			}

    			downloadXls();
    		});
  });


  function loadSalesTargetAchievedReport() {
    $("#tblSalesTargetAchievedReport").css("display", "none");
    $("#loadingData").css("display", "block");
    var employeePid = $("#dbEmployee").val();
    var fromDate = $("#txtFromMonth").MonthPicker("GetSelectedMonthYear");
    if (fromDate === null) {
      alert("please select from month");
      return;
    }
    $("#hLoadId").html("Loading...");
    var [fromMonth, fromYear] = fromDate.split("/");
    fromDate = new Date(fromYear, fromMonth - 1, 1);
    $.ajax({
      url: kmdistancefarePath + "/load",
      type: "GET",
      data: {
        fromDate: convertLocalDateToServer(fromDate),
        userPid: employeePid,
      },
      success: function (responsedata) {
        $("#loadingData").css("display", "none");
		$("#tbldistanceBody").html("")

        $.each(responsedata, function (index, response) {
          $("#tbldistanceBody").append(
            "<tr><td>" +
              response.employeeName +
              "</td><td>" +
              response.plannedDate +
              "</td><td>" +
              response.startingPoint +
              "</td><td>" +
              response.endPoint +
              "</td><td>" +
              response.totalDistance.toFixed(2)
              +"</td><td>" +
              response.slabName +
              "</td><td>" +
              response.slabRate +
              "</td></tr>"
          );
        });
      },
    });
  }
function downloadXls() {
		// When the stripped button is clicked, clone the existing source
		var clonedTable = $("#tblAccountProfile").clone();
		// Strip your empty characters from the cloned table (hidden didn't seem
		// to work since the cloned table isn't visible)
		clonedTable.find('[style*="display: none"]').remove();

		var excelName = "KilometerDistanceFare";

		clonedTable.table2excel({
			// exclude CSS class
			// exclude : ".odd .even",
			// name : "Dynamic Document Form",
			filename : excelName, // do not include extension
		// fileext : ".xls",
		// exclude_img : true,
		// exclude_links : true,
		// exclude_inputs : true
		});
	}
  function convertLocalDateToServer(date) {
    if (date) {
      return moment(date).format("YYYY-MM-DD");
    } else {
      return "";
    }
  }

  function onError(httpResponse, exception) {
    var i;
    switch (httpResponse.status) {
      // connection refused, server not reachable
      case 0:
        addErrorAlert("Server not reachable", "error.server.not.reachable");
        break;
      case 400:
        var errorHeader = httpResponse.getResponseHeader(
          "X-orderfleetwebApp-error"
        );
        var entityKey = httpResponse.getResponseHeader(
          "X-orderfleetwebApp-params"
        );
        if (errorHeader) {
          var entityName = entityKey;
          addErrorAlert(errorHeader, errorHeader, {
            entityName: entityName,
          });
        } else if (httpResponse.responseText) {
          var data = JSON.parse(httpResponse.responseText);
          if (data && data.fieldErrors) {
            for (i = 0; i < data.fieldErrors.length; i++) {
              var fieldError = data.fieldErrors[i];
              var convertedField = fieldError.field.replace(/\[\d*\]/g, "[]");
              var fieldName =
                convertedField.charAt(0).toUpperCase() +
                convertedField.slice(1);
              addErrorAlert(
                "Field " + fieldName + " cannot be empty",
                "error." + fieldError.message,
                {
                  fieldName: fieldName,
                }
              );
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
