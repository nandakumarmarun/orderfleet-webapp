// Create a User object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Slab) {
  this.Slab = {};
}

/**
 *
 */
(function () {
  var userContextPath =
    location.protocol + "//" + location.host + location.pathname;

  var createEditForm = $("#userForm");
  var deleteForm = $("#deleteForm");
  var slabPid = null;
  

  var kmSlabDTO = {
    id: null,
    pid: null,
    slabName: null,
    minKm: null,
    maxKm: null,
    slabRate: null,
    companyid: null,
  };

  // Specify the validation rules
  var validationRules = {
    minUser: {
      required: true,
      maxlength: 55,
    },
    maxUser: {
      required: true,
      maxlength: 55,
    },
    slabRate: {
      required: true,
      maxlength: 55,
    },
    companyPid: {
      valueNotEquals: "-1",
    },
  };

  // Specify the validation error messages
  var validationMessages = {
    minUser: {
      required: "This field is required.",
      maxlength: "This field cannot be longer than 55 characters.",
    },
    maxUser: {
      required: "This field is required.",
      maxlength: "This field cannot be longer than 55 characters.",
    },
    slabRate: {
      required: "This field is required.",
      maxlength: "This field cannot be longer than 55 characters.",
    },
  };

  $(document).ready(function () {
    Slab.filterUsers();

    $(".selectpicker").selectpicker();
    // add the rule here

    $.validator.addMethod(
      "valueNotEquals",
      function (value, element, arg) {
        return arg != value;
      },
      ""
    );

    createEditForm.validate({
      rules: validationRules,
      messages: validationMessages,
      submitHandler: function (form) {
        createUpdateUser(form);
      },
    });

    deleteForm.submit(function (e) {
      console.log("deleteing.....");
      // prevent Default functionality
      e.preventDefault();
      // pass the action-url of the form
      deleteUser(e.currentTarget.action);
    });

    $("#btnSearch").click(function () {
      console.log("searching .........");
      search();
    });

    $("#btnAssignUser").click(function () {
      console.log("Assign To Users...", slabPid);
      assignUsers(slabPid);
    });

    $("#EditUser").click(function () {
      console.log("Assign To Users...");
      UpdatedUser();
    });
  });

  function createUpdateUser(el) {
    kmSlabDTO.slabName = $("#field_slab_name").val();
    kmSlabDTO.minKm = $("#field_min_user").val();
    kmSlabDTO.maxKm = $("#field_max_user").val();
    kmSlabDTO.slabRate = $("#field_slab_rate").val();
    $.ajax({
      method: $(el).attr("method"),
      url: userContextPath + "/save",
      contentType: "application/json; charset=utf-8",
      data: JSON.stringify(kmSlabDTO),
      success: function (data) {
        console.log(data);
        onSaveSuccess(data);
      },
      error: function (xhr, error) {
        onError(xhr, error);
      },
    });
  }

  function UpdatedUser() {
    console.log(" Updated New User ");
    kmSlabDTO.slabName = $("#field_slab_name_update").val();
    kmSlabDTO.minKm = $("#field_min_user_update").val();
    kmSlabDTO.maxKm = $("#field_max_user_update").val();
    kmSlabDTO.slabRate = $("#field_slab_rate_update").val();
    $.ajax({
      method: "POST",
      url: userContextPath + "/update",
      contentType: "application/json; charset=utf-8",
      data: JSON.stringify(kmSlabDTO),
      success: function (data) {
        console.log(data);
        onSaveSuccess(data);
      },
      error: function (xhr, error) {
        onError(xhr, error);
      },
    });
  }

  function UpdateUser(pid) {
    $.ajax({
      method: "GET",
      url: userContextPath + "/edit",
      contentType: "application/json; charset=utf-8",
      data: {
        Slabpid: pid,
      },
      success: function (data) {
        $("#field_slab_name_update").val(data.slabName);
        $("#field_min_user_update").val(data.minKm);
        $("#field_max_user_update").val(data.maxKm);
        $("#field_slab_rate_update").val(data.slabRate);
      },
      error: function (xhr, error) {
        onError(xhr, error);
      },
    });
  }

  Slab.deleteUser = function (slabPid) {
    console.log(slabPid);
    $.ajax({
      url: userContextPath + "/delete/" + slabPid,
      method: "DELETE",
      success: function (status) {
        Slab.filterUsers();
      },
      error: function (xhr, error) {
        onError(xhr, error);
      },
    });
  };

  Slab.filterUsers = function () {
    $("#tBodyUser").html(
      "<tr><td colspan='10' align='center'>Please wait...</td></tr>"
    );
    $.ajax({
      url: userContextPath + "/salbs",
      method: "GET",
      success: function (users) {
        console.log(users);
        loadTableData(users);
      },
      error: function (xhr, error) {
        onError(xhr, error);
      },
    });
  };

  function loadTableData(users) {
    $("#tBodyUser").html("");
    if (users.length == 0) {
      $("#tBodyUser").html(
        "<tr><td colspan='10' align='center'>No data available</td></tr>"
      );
      return;
    }
    $.each(users, function (index, slab) {
      $("#tBodyUser").append(
        "<tr><td>" +
          slab.slabName +
          "</td><td>" +
          slab.minKm +
          "</td><td>" +
          slab.maxKm +
          "</td><td>" +
          slab.slabRate +
          "</td><td class='action'>" +
          "<button type='button' class='btn btn-primary btnuser' onclick='Slab.showModalPopup($(\"#userAssignModel\"),\"" +
          slab.pid +
          "\",2);'>Assign Users</button>" +
          "<button type='button' class='btn btn-primary btnuser' onclick='Slab.showModalPopup($(\"#EditModel\"),\"" +
          slab.pid +
          "\",1);'>Edit </button>" +
          "<button type='button' class='btn btn-red btndlt' onclick='Slab.deleteUser(\"" +
          slab.pid +
          "\");'>Delete</button>" +
          "</td></tr>"
      );
    });

    $(".collaptable").aCollapTable({
      startCollapsed: true,
      addColumn: false,
      plusButton: '<span><i class="entypo-down-open-mini"></i></span>',
      minusButton: '<span><i class="entypo-up-open-mini"></i></span>',
    });
  }

  function onSaveSuccess(result) {
    // reloading page to see the updated data
    window.location = userContextPath;
  }

  function onDeleteSuccess(result) {
    // reloading page to see the updated data
    window.location = userContextPath;
  }
  // $(".error-msg").html("");
  // var selectedusers = "";
  // $.each($("input[name='usercheckbox']:checked"), function () {
  //   selectedusers += $(this).val() + ",";
  // });

  // console.log(selectedusers);
  // if (selectedusers == "") {
  //   $(".error-msg").html("Please select users");
  //   return;
  // }

  function saveAssignedUsers(id) {
    $("#divUsers input:checkbox").attr("checked", false);
    $(".error-msg").html("");
    console.log(id);
    slabPid = id;
    $.ajax({
      url: userContextPath + "/km-slab-users",
      type: "GET",
      data: {
        kmSlabPid: id,
      },
      success: function (assignedUsers) {
        if (assignedUsers) {
          $.each(assignedUsers, function (index, user) {
            $("#divUsers input:checkbox[value=" + user.userPid + "]").prop(
              "checked",
              true
            );
          });
        }
      },
      error: function (xhr, error) {
        onError(xhr, error);
      },
    });
  }

  function assignUsers(slabPid) {
    console.log(slabPid);
    $(".error-msg").html("");
    var selectedusers = "";

    $.each($("input[name='userCheckBox']:checked"), function () {
      selectedusers += $(this).val() + ",";
    });

    $.ajax({
      url: userContextPath + "/km-slab-users",
      type: "POST",
      data: {
        kmSlabPid: slabPid,
        userPids: selectedusers,
      },
      success: function (assignedUsers) {
        onSaveSuccess(assignedUsers);
      },
      error: function (xhr, error) {
        onError(xhr, error);
      },
    });
  }

  Slab.showModalPopup = function (el, id, action) {
    resetForm();
    if (id) {
      switch (action) {
        case 0:
          showUser(id);
          break;
        case 1:
          UpdateUser(id);
          kmSlabDTO.pid=id;
          break;
        case 2:
          console.log(id);
          saveAssignedUsers(id);
          break;
      }
    }
    el.modal("show");
  };

  Slab.closeModalPopup = function (el) {
    el.modal("hide");
  };

  function resetForm() {
    $(".alert").hide();
    createEditForm.trigger("reset"); // clear form fields
    createEditForm.validate().resetForm(); // clear validation messages
    createEditForm.attr("method", "POST"); // set default method
    kmSlabDTO.pid = null; // reset user model;
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
