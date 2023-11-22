if (!this.TaskPlanToDO) {
  this.TaskPlanToDO = {};
}

(function () {
  "use strict";

  var taskToDoPath =
    location.protocol + "//" + location.host + location.pathname;

  $(document).ready(function () {
    $("#btnSubmit").click(function () {
		filter();
    });

    $("#btnDownload").on("click", function () {
      var tblDayPlan = $("#tBodyTaskToDo");
      if (tblDayPlan.children().length == 0) {
        alert("no values available");
        return;
      }
      var excelName = "Customer Exclude Dayplan";
      var instance = $("#tblTaskToDo").tableExport({
        formats: ["xlsx"],
        filename: excelName,
        exportButtons: false,
      });
      var exportData =
        instance.getExportData()["tblTaskToDo"]["xlsx"];
      instance.export2file(
        exportData.data,
        exportData.mimeType,
        exportData.filename,
        exportData.fileExtension
      );
    });

  });

  function filter() {
    $("#tBodyTaskToDo").html(
      "<tr><td colspan='9' align='center'>Please wait...</td></tr>"
    );
    $.ajax({
      url: taskToDoPath + "/load",
      type: "GET",
      data: {
        activityPid : $("#dbactivity").val(),
      },
      success: function (data) {
        $("#tBodyKilometerCalculation").html("");
        if (data.length == 0) {
          $("#tBodyTaskToDo").html(
            "<tr><td colspan='9' align='center'>No data available</td></tr>"
          );
          return;
        }

        $("#tBodyTaskToDo").html("");
        $.each(data, function (index, toDoTask) {
          $("#tBodyTaskToDo").append(
            "<tr><td>" +
              toDoTask.taskName +
              "</td><td>" +
              toDoTask.activityName +
              "</td></tr>"
          );
        });
      },
    });
  }

  function onSaveSuccess(result) {
    // reloading page to see the updated data
    window.location = kilometerCalculationPath;
  }

  function onDeleteSuccess(result) {
    // reloading page to see the updated data
    window.location = kilometerCalculationPath;
  }

  function addErrorAlert(message, key, data) {
    $(".alert > p").html(message);
    $(".alert").show();
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
