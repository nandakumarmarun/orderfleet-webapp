if (!this.User) {
  this.User = {};
}

/**
 *
 */
(function () {
  var userContextPath =
    location.protocol + "//" + location.host + location.pathname;
      var normalPath =
        location.protocol + "//" + location.host;

  $(document).ready(function () {
    $(".selectpicker").selectpicker();
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

    $("#btnSearch").click(function () {
      searchTable($("#search").val());
    });

    $("#btnActivate").on("click", function () {
      activateAssigned();
    });

    $("input:checkbox.allcheckbox").click(function () {
      $(this)
        .closest("table")
        .find('tbody tr td input[type="checkbox"]:visible')
        .prop("checked", $(this).prop("checked"));
    });
  });

  function searchTable(inputVal) {
    var table = $("#tBodyUser");
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

  User.ActivatedOnclick = function (status) {
    var userPid = [];
    var status = status;
    $.each($("input[name='user']:checked"), function () {
      userPid.push($(this).val());
    });

    if (userPid == "") {
      alert("Please Select Any User!");
    }

    $.ajax({
      url: normalPath + "/web/mod-c/enable-feature",
      method: "GET",
      contentType: "application/json",
      data: {
        userPid: userPid.join(','),
        status: true,
      },
      success: function (data) {
        console.log("dsklfdkfsdhfsdjhfekjfh")
        window.location = userContextPath;
      },
      error: function (xhr, error) {
        onError(xhr, error);
      },
    });
  };

    User.DeActivatedOnclick = function (status) {
    console.log(status);
      var userPid = [];
      var status = status;
      $.each($("input[name='user']:checked"), function () {
        userPid.push($(this).val());
      });

      if (userPid == "") {
        alert("Please Select Any User!");
      }

      $.ajax({
        url: normalPath + "/web/mod-c/disable-feature",
        method: "GET",
        contentType: "application/json",
        data: {
          userPid: userPid.join(','),
          status: false,
        },
        success: function (data) {
          window.location = userContextPath;
        },
        error: function (xhr, error) {
          onError(xhr, error);
        },
      });
    };

  User.loadUser = function (companyPid) {
    var companypid = $("#dbCompany").val();
    console.log(companypid);
    $("#dbDocument").val('<option value="no">Select Document</option>');
    if (companyPid == "no") {
      return;
    }
    $("#dbDocument").val('<option value="no">Documents loading...</option>');
    $("#tBodyUser").html("");
    $.ajax({
      url: normalPath + "/web/mod-c/user-list/"+ companypid,
      method: "GET",
      contentType: "application/json; charset=utf-8",
      success: function (data) {
        loadTableData(data);
      },
      error: function (xhr, error) {
        onError(xhr, error);
      },
    });
  };

  function loadTableData(data) {
    $.each(data, function (index, user) {
      $("#tBodyUser").append(
        "<tr><td>" +
          user.login +
          "</td><td>" +
          spanActivated(user.enableModcFeature, user.pid) +
          "</td><td><input type='checkbox' name='user' class='check-one' value='" +
          user.pid +
          "' />" +
          "</td></tr>"
      );
    });
  }

  function spanActivated(activated, userPid) {
    var spanActivated = "";
    var user = "'" + userPid + "'";
    if (activated) {
      spanActivated =
        '<span class="label label-success" onclick="User.setActive(' +
        user +
        ", " +
        !activated +
        ')" style="cursor: pointer;">Enable</span>';
    } else {
      spanActivated =
        '<span class="label label-danger" onclick="User.setActive(' +
        user +
        "," +
        !activated +
        ')" style="cursor: pointer;">Disable</span>';
    }
    return spanActivated;
  }

  function onSaveSuccess(result) {
    // reloading page to see the updated data
    window.location = userContextPath;
  }

  function onDeleteSuccess(result) {
    // reloading page to see the updated data
    window.location = userContextPath;
  }

  User.closeModalPopup = function (el) {
    el.modal("hide");
  };

  function resetForm() {
    $(".alert").hide();
    createEditForm.trigger("reset"); // clear form fields
    createEditForm.validate().resetForm(); // clear validation messages
    createEditForm.attr("method", "POST"); // set default method
    userModel.pid = null; // reset user model;
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

  function convertDateTimeFromServer(date) {
    if (date) {
      return moment(date).format("MMMM Do YYYY, h:mm:ss a");
    } else {
      return null;
    }
  }
})();