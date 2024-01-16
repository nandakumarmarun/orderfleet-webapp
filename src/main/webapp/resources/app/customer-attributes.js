
 window.showCreateAttributeModal = function() {
     $('#myModal').modal('show');
 };
 if (!window.customerAttributes) {
    window.customerAttributes = {};
}

(function () {
var selectedQuestions = [];
    var customerAttributesContextPath = location.protocol + '//' + location.host + location.pathname;
console.log(customerAttributesContextPath);


  function saveAttribute() {


        var attributeName = $('#attributeName').val();
        var attributeType = $('#attributeType').val();
 if (!attributeName) {
        alert('Attribute name cannot be empty');
        return;
    }
     if (!attributeType) {
            alert('Please Select Attribute Type');
            return;
        }

        var customerAttributesDTO = {

            question: attributeName,
            type: attributeType

        };


        var url = customerAttributesContextPath + '/create-attribute';

        $.ajax({
            type: 'POST',
            url: url,
            data: JSON.stringify(customerAttributesDTO),
            contentType: 'application/json',
            success: function(data) {
                 alert('Attribute saved successfully!');
                console.log('Success:', data);
                refreshFunction();
                // Update the table or perform other actions as needed
            },
            error: function(jqXHR, textStatus, errorThrown) {
                // Handle the error
                onError(jqXHR, errorThrown);
            }
        });
    }

$(document).ready(function() {
    $('#saveButton').on('click', function() {
        saveAttribute();


         filterTable();
                    $("input[name='filter']").change(function() {
                        filterTable();
                    });
    });

});
$('#updateButton').on('click', function () {
    var companyPid = $('#dbCompany').val();
    var documentPid=$('#dbDocument').val();

    if (companyPid === '-1') {
        alert("Please select a Company for update");
        return;
    }

    var formData = new FormData();
    formData.append('companyPid', companyPid);
    formData.append('documentPid', documentPid);

    var selectedQuestions = [];
    var sortOrders = [];
    var conditionFailed = false;

    $.each($("input[name='customerAttributes']:checked"), function () {
        var selectedValue = $(this).val();
        var sortOrderValue = $(this).closest('tr').find('.sortOrderInput').val();
        selectedQuestions.push(selectedValue);

        var parsedSortOrder = parseInt(sortOrderValue);
        if (!isNaN(parsedSortOrder) && parsedSortOrder > 0 && !sortOrders.includes(parsedSortOrder)) {
            sortOrders.push(parsedSortOrder);
        } else if (isNaN(parsedSortOrder) || parsedSortOrder <= 0) {
            alert("Invalid sort order entered. Please enter a valid number greater than zero.");
            conditionFailed = true;
            return false; // break out of the loop
        } else {
            alert("Duplicate sort order found. Please enter a unique number.");
            conditionFailed = true;
            return false; // break out of the loop
        }
    });

    if (conditionFailed) {
        return; // exit the update function if any condition is failed
    }

    if (selectedQuestions.length === 0) {
        alert("Please select a Question for update");
        return;
    }

    var selectedQuestionsString = selectedQuestions.join(',');
    var sortOrdersString = sortOrders.join(',');

    formData.append('selectedQuestions', selectedQuestionsString);
    formData.append('sortOrder', sortOrdersString);

    $.ajax({
        url: customerAttributesContextPath + '/update',
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            console.log("success" + response);
            refreshFunction();
        },
        error: function (xhr, error) {
            console.error(error);
        }
    });
});

$('#selectAll').click(function () {
    var isChecked = this.checked;
    $('.selectRow').prop('checked', isChecked);
});

 window.customerAttributes.getDocument = function () {

     var selectedCompanyPid = $("#dbCompany").val();

     selectedCompanyPid  = String(selectedCompanyPid ); // Convert to string explicitly
console.log(selectedCompanyPid);


     $.ajax({
         type: "GET",
         url: customerAttributesContextPath + "/getDocumentByCompany",
         data: {
             companyPid: selectedCompanyPid

         },

         success: function (response) {
         console.log(response);
             var documentDropdown = $("#dbDocument");
documentDropdown.empty().append('<option value="all">All Documents</option>');

             var uniquePids = [];
             var uniqueDocuments = [];
             response.forEach(function (item) {
                 if (!uniquePids.includes(item.pid)) {
                     uniquePids.push(item.pid);
                     uniqueDocuments.push(item);
                 }
             });

             // Append filtered options to the dropdown
             uniqueDocuments.forEach(function (item) {
                 documentDropdown.append('<option value="' + item.pid + '">' + item.name + '</option>');
             });
             documentDropdown.trigger('change');
         },
         error: function (e) {
             console.log("Error fetching document: " + e);
         }
     });

 };
  $(document).ready(function () {
          window.customerAttributes.getDocument();
      });

function refreshFunction() {
location.reload();
}
$('#dbDocument').change(function () {
     var documentPid = $(this).val();
     var companyPid = $('#dbCompany').val();
if (documentPid === "all") {
        // Handle the "All Documents" case
        console.log("All Documents selected");
        // Add your logic here
    } else {
        // Handle other document selections
        console.log("Selected document: " + documentPid);
        // Add your logic here
    }
     // Clear checkboxes and error message
     $("#tBodyCustomerAttributes input:checkbox").prop('checked', false);
     $(".error-msg").html("");
     $("#tBodyCustomerAttributes input:text").val('');

     // Make an AJAX request to fetch the assigned attributes for the selected document
     $.ajax({
         type: 'GET',
         url: customerAttributesContextPath + '/get-attributes-by-document',
         data: {
             documentPid: documentPid,
             companyPid: companyPid
         },
         success: function (customerAttributes) {
             console.log(customerAttributes);

             if (customerAttributes) {
                 $.each(customerAttributes, function (index, attribute) {
                     $("#tBodyCustomerAttributes input:checkbox[value='" + attribute.attributePid + "']").prop("checked", true);
                     var inputBox = $("#tBodyCustomerAttributes input:text[id='" + attribute.attributePid + "']");
                     inputBox.val('').val(attribute.sortOrder);
                 });
             }

             filterTable();
         },

         error: function (jqXHR, textStatus, errorThrown) {
             // Handle the error
             console.error('Error:', errorThrown);
         }
     });
 });





// Function to filter table based on radio button selection
function filterTable() {
    var table = $('#tBodyCustomerAttributes');
    var filterBy = $("input[name='filter']:checked").val();

    table.find('tr').each(function(index, row) {
        var allCells = $(row).find('td');
        if (allCells.length > 0) {
            var found = false;
            var checked = $(row).find('input:checkbox').is(':checked');

            if (filterBy === 'all') {
                found = true;
            } else if (filterBy === 'selected') {
                found = checked;
            } else if (filterBy === 'unselected') {
                found = !checked;
            }

            if (found) {
                $(row).show();
            } else {
                $(row).hide();
            }
        }
    });
}


$("input[name='filter']").change(function() {
    filterTable();
});




 function loadCustomerAttributes(customerAttributes) {

     var $tBodyCustomerAttributes = $('#tBodyCustomerAttributes');
     $tBodyCustomerAttributes.empty();
console.log(customerAttributes);
     if (customerAttributes.length === 0) {
         $tBodyCustomerAttributes.html("<tr><td colspan='4' align='center'>No data available</td></tr>");
         return;
     }

     $.each(customerAttributes, function (index, customerAttributes) {

         $tBodyCustomerAttributes.append(

             "<tr>" +
             "<td><input type='checkbox' class='selectRow' name='customerAttributes' value='" + customerAttributes.attributePid + "' checked></td>" +

             "<td>" + customerAttributes.question + "</td>" +
             "<td>" + customerAttributes.type + "</td>" +
           "<td><input type='text' class='sortOrderInput' id='${attribute.attributePid}' pattern='[0-9]*' ></td>" +
             "</tr>"
         );

     });
     $("#selectAll").prop("checked", false);
 }

 function addErrorAlert(message, key, data) {
     $(".alert > p").html(message);
     $('.alert').show();
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
