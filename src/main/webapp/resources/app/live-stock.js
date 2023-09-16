//.........................JS PAGE.............................

//Create a OpeningStock object only if one does not already exist. We create the
//methods in a closure to avoid creating global variables.

if (!this.OpeningStock) {
  this.OpeningStock = {};
}

(function () {
  "use strict";

  var openingStockContextPath =
    location.protocol + "//" + location.host + location.pathname;

  var openingStockModel = {
    pid: null,
    productProfilePid: null,
    batchNumber: null,
    stockLocationPid: null,
    quantity: null,
  };

  // Specify the validation rules
  var validationRules = {
    productProfilePid: {
      valueNotEquals: "-1",
    },
    batchNumber: {
      required: true,
      maxlength: 255,
    },
    stockLocationPid: {
      valueNotEquals: "-1",
    },
    quantity: {
      maxlength: 100,
    },
  };

  // Specify the validation error messages
  var validationMessages = {
    batchNumber: {
      required: "This field is required.",
      maxlength: "This field cannot be longer than 255 characters.",
    },

    quantity: {
      required: "This field is required.",
      maxlength: "This field cannot be longer than 100 characters.",
    },
  };

  $(document).ready(function () {
    // add the rule here
    $.validator.addMethod(
      "valueNotEquals",
      function (value, element, arg) {
        return arg != value;
      },
      ""
    );

    $("#btnSearch").click(function () {
      searchTable($("#search").val());
    });

    $("input:checkbox.allcheckbox").click(function () {
      $(this)
        .closest("table")
        .find('tbody tr td input[type="checkbox"]')
        .prop("checked", $(this).prop("checked"));
    });

    showOpeningStock();
  });

  $("#btnDownload").on("click", function () {
    var tblOpeningStocks = $("#tblOpeningStocks tbody");
    if (tblOpeningStocks.children().length == 0) {
      alert("no values available");
      return;
    }
    if (tblOpeningStocks[0].textContent == "No data available") {
      alert("no values available");
      return;
    }

    downloadXls();
    $(
      "#tblOpeningStocks th:last-child, #tblOpeningStocks td:last-child"
    ).show();
  });

  $("#btnload").on("click", function () {
    showOpeningStock();
  });

  function searchTable(inputVal) {
    var table = $("#tbodyOpeningStocks");
    table.find("tr").each(function (index, row) {
      var allCells = $(row).find("td");
      if (allCells.length > 0) {
        var found = false;
        allCells.each(function (index, td) {
          if (index != 7) {
            var regExp = new RegExp(inputVal, "i");
            if (regExp.test($(td).text())) {
              found = true;
              return false;
            }
          }
        });
        if (found == true) $(row).show();
        else $(row).hide();
      }
    });
  }

  function downloadXls() {
    window.location.href =
      openingStockContextPath + "/download-openingStock-xls";
  }

  function showOpeningStock() {
    $.ajax({
      url: openingStockContextPath + "/" + "load-live-stocks",
      method: "GET",
      success: function (data) {
        console.log(data);
        addTableBodyvalues(data);
      },
      error: function (xhr, error) {
        onError(xhr, error);
      },
    });
  }

  function addTableBodyvalues(livestocks) {
    $("#tbodyOpeningStocks").html("");

    if (livestocks.length == 0) {
      $("#tbodyOpeningStocks").html(
        "<tr><td colspan='13' align='center'>No data available</td></tr>"
      );
      return;
    }
	
    $.each(livestocks, function (index, livestock) {
      $("#tbodyOpeningStocks").append(
        "<tr><td>" +
          livestock.createdate +
          "</td><td>" +
          livestock.productId +
          "</td><td>" +
          livestock.productName +
          "</td><td>" +
          livestock.stockLocationName +
          "</td><td>" +
          livestock.openingQuantity +
          "</td><td>" +
          livestock.soldQuantity +
          "</td><td>" +
          livestock.avilableQuantity +
          "</td><td>" +
          livestock.damageQuantity +
          "</td><td>" +
          livestock.lastModifiedDate +
          "</td></tr>"
      );
    });
  }

  function onSaveSuccess(result) {
    // reloading page to see the updated data
    window.location = openingStockContextPath;
  }

  function onDeleteSuccess(result) {
    // reloading page to see the updated data
    window.location = openingStockContextPath;
  }

  OpeningStock.closeModalPopup = function (el) {
    el.modal("hide");
  };

  function resetForm() {
    $(".alert").hide();
    createEditForm.trigger("reset"); // clear form fields
    createEditForm.validate().resetForm(); // clear validation messages
    createEditForm.attr("method", "POST"); // set default method
    openingStockModel.pid = null; // reset openingStock model;
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

function toTimeZone(time, zone) {
  var format = "YYYY/MM/DD HH:mm:ss ZZ";
  return moment(time, format).tz(zone).format(format);
}

function convertUTCDateToDate(utcDate) {
  var date = new Date(utcDate);
  var dd = date.getDate();
  var mm = date.getMonth() + 1;
  var yyyy = date.getFullYear();
  if (dd < 10) {
    dd = "0" + dd;
  }
  if (mm < 10) {
    mm = "0" + mm;
  }
  return (utcDate = dd + "-" + mm + "-" + yyyy);
}
