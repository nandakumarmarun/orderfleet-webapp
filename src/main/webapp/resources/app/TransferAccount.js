if (!window.TransferAccount) {
    window.TransferAccount = {};
}

(function () {
    'use strict';
    function showLoadingSymbol() {
        document.getElementById('loadingSymbol').style.display = 'block';
    }

    function hideLoadingSymbol() {
        document.getElementById('loadingSymbol').style.display = 'none';
    }

    // Declare selectedAccountProfiles at a higher scope
    var selectedAccountProfiles = [];

    var transferAccountContextPath = location.protocol + '//' + location.host + location.pathname;

    window.TransferAccount.onChangeUser = function () {
        var userPid = $('#dbUser').val();

        if (userPid === '-1') {
            alert("Please select a user.");
            return;
        }

        $.ajax({
            url: transferAccountContextPath + "/userChange/" + userPid,
            method: 'GET',
            success: function (data) {
                loadTransferAccountTables(data);
            },
            error: function (xhr, error) {
                onError(xhr, error);
            }
        });
    };
 window.TransferAccount.getLocation = function () {
     var selectedUserPid = $("#dbUser").val();
     selectedUserPid = String(selectedUserPid); // Convert to string explicitly

     $.ajax({
         type: "GET",
         url: transferAccountContextPath + "/getLocationByUser",
         data: {
             userPid: selectedUserPid
         },
         success: function (response) {
             var locationDropdown = $("#dbLocation");
             locationDropdown.empty();
             locationDropdown.append('<option value="-1">Select Location</option>');

             // Filter out duplicate pids
             var uniquePids = [];
             var uniqueLocations = [];
             response.forEach(function (item) {
                 if (!uniquePids.includes(item.pid)) {
                     uniquePids.push(item.pid);
                     uniqueLocations.push(item);
                 }
             });

             // Append filtered options to the dropdown
             uniqueLocations.forEach(function (item) {
                 locationDropdown.append('<option value="' + item.pid + '">' + item.name + '</option>');
             });
         },
         error: function (e) {
             console.log("Error fetching locations: " + e);
         }
     });
 };

    window.TransferAccount.onChangeLocation = function () {
        var locationPid = $('#dbLocation').val();
        var userPid = $('#dbUser').val();
        console.log(locationPid);

        if (locationPid === "-1" || userPid === "-1") {
            alert("Please select both a location and a user.");
            return;
        }

        $.ajax({
            url: transferAccountContextPath + "/getLocationAccountProfilesSortedByUserAndLocation" + "/" + locationPid + "/" + userPid,
            type: "GET",
            success: function (data) {
                if (!data || data.length === 0) {
                    $('#tBodyAccountProfile').html("<tr><td colspan='6' align='center'>No data available</td></tr>");
                } else {
                    loadTransferAccountTables(data);
                }
            },
            error: function (xhr, error) {
                onError(xhr, error);
            }
        });
    };

$('#transferButton').on('click', function () {

    var newLocation = $('#locationDropdown').val();
    var currentLocationPid = $('#dbLocation').val();


    // Create a FormData object
    var formData = new FormData();

    // Add currentLocationPid and newLocation as form fields
    formData.append('currentLocationPid', currentLocationPid);
    formData.append('newLocation', newLocation);

    // Clear the selectedAccountProfiles array
    selectedAccountProfiles = [];
    console.log(newLocation);
    console.log(currentLocationPid);
    console.log( selectedAccountProfiles);


    $.each($("input[name='data']:checked"), function () {
        var selectedValue = $(this).val();
        selectedAccountProfiles.push(selectedValue);
    });

    console.log(selectedAccountProfiles);

    // Convert the selectedAccountProfiles array to a comma-separated string
    var selectedProfilesString = selectedAccountProfiles.join(',');



    // Add the selectedAccountProfiles as a form field
    formData.append('selectedAccountProfiles', selectedProfilesString);
    if(newLocation==currentLocationPid){
        alert("Both Location Are same select another location");
        return;
        }
    if(newLocation==='-1'){
             alert("Please select A location to transfer");
             return;
            }
    if(selectedAccountProfiles.length===0){
        alert("Please select A data to transfer");
        return;
        }
showLoadingSymbol();
    // Create an AJAX request
    $.ajax({
        url: transferAccountContextPath + '/transferAccountProfiles',
        method: 'POST',
        data: formData, // Use FormData
        processData: false, // Prevent jQuery from processing the data
        contentType: false, // Prevent jQuery from setting a content type
        success: function (response) {
            console.log("success" + response);
            hideLoadingSymbol();
             alert("Transfer completed successfully!");
            refreshFunction();


        },
        error: function (xhr, error) {
            console.error(error);
            hideLoadingSymbol();
        }
    });
});


    $('#selectAll').click(function () {
        var isChecked = this.checked;
        $('.selectRow').prop('checked', isChecked);
    });

    function loadTransferAccountTables(accountProfiles) {
        var $tBodyAccountProfile = $('#tBodyAccountProfile');
        $tBodyAccountProfile.empty();

        if (accountProfiles.length === 0) {
            $tBodyAccountProfile.html("<tr><td colspan='6' align='center'>No data available</td></tr>");
            return;
        }

        $.each(accountProfiles, function (index, accountProfile) {
            $tBodyAccountProfile.append(
                "<tr>" +
                "<td><input type='checkbox' class='selectRow' name='data' value='" + accountProfile.accountProfilePid + "'></td>" +
                "<td>" + accountProfile.accountProfileName + "</td>" +
                "<td>" + accountProfile.accountProfileAddress + "</td>" +
                "<td>" + accountProfile.locationName + "</td>" +
                "<td>" + accountProfile.username + "</td>" +
                "</tr>"
            );
        });
        $("#selectAll").prop("checked", false);
    }

    function addErrorAlert(message, key, data) {
        $(".alert > p").html(message);
        $('.alert').show();
    }
function refreshFunction() {
window.TransferAccount.onChangeLocation();
}
    function onError(httpResponse, exception) {
        var i;
        switch (httpResponse.status) {
            case 0:
                addErrorAlert('Server not reachable', 'error.server.not.reachable');
                break;
            case 400:
                var errorHeader = httpResponse.getResponseHeader('X-orderfleetwebApp-error');
                var entityKey = httpResponse.getResponseHeader('X-orderfleetwebApp-params');
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
                            var convertedField = fieldError.field.replace(/\[\d*\]/g, '[]');
                            var fieldName = convertedField.charAt(0).toUpperCase() + convertedField.slice(1);
                            addErrorAlert('Field ' + fieldName + ' cannot be empty', 'error.' + fieldError.message, {
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
