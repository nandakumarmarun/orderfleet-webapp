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
<!-- dynam ic document script is in forms table at js_code column -->
	<!-- calculate total amount on dynamic document form multiple true -->
	<script type="text/javascript">
		function calculateTotalAmountAndShow(elem) {
			var totAmt = 0;
			$(elem).next("table.mulRec").find("tbody").find("tr").each(
					function() {
						var amt = $(this).find("td").eq(1).text();
						if (amt.length > 0 && !isNaN(amt)) {
							totAmt = totAmt + parseFloat(amt);
						}
					});
			if ($('#lblTotAmt').length > 0) {
				$('#lblTotAmt').html('Total : ' + totAmt.toFixed(2));
			} else {
				$(elem).next("table.mulRec").after(
						'<div align="middle"><b id="lblTotAmt">Total : '
								+ totAmt.toFixed(2) + '</b></div>');
			}
		}
	</script>
	<script type="text/javascript">
		//Dynamic document barbed wire
		function FieldError(field, rejectedValue, message) {
			this.field = field;
			this.rejectedValue = rejectedValue;
			this.message = message;
		}

		function initializeForm() {
			//find by name
			//Area
			var elCent = $('[name="Area (cents)"]');
			var elMeter = $('[name="Area (meters)"]');
			var elRate = $('[name="Rate/Running meter"]');
			var elTotal = $('[name="Total Amount"]');
			elCent
					.keyup(function() {
						var inMeterSqr = $(elCent).val() * 40.4686
						var inMeter = Math.sqrt(inMeterSqr);
						var fourSide = 4 * inMeter.toFixed(2);
						$(elMeter).val(fourSide.toFixed(2));
						$(elTotal)
								.val(
										($(elMeter).val() * $(elRate).val())
												.toFixed(2));
					});
			elRate
					.keyup(function() {
						$(elTotal)
								.val(
										($(elMeter).val() * $(elRate).val())
												.toFixed(2));
					});

			//Size Gage
			var elSizeGage = $('[name="Size Gage"]');
			var elSizeGageMm = $('[name="Size Gage (millimeter)"]');
			elSizeGage.change(function() {
				var convertedValue = gaugeToMillimeters($(elSizeGage).val());
				$(elSizeGageMm).val(convertedValue);
			});

			//Post Height
			var elPostFt = $('[name="Post Height (ft)"]');
			var elPostMtr = $('[name="Post Height (meters)"]');
			elPostFt.keyup(function() {
				$(elPostMtr).val($(elPostFt).val() * 0.3048);
			});
		}

		function gaugeToMillimeters(value) {
			var convertedValue = "";
			switch (value) {
			case "6 GI":
				convertedValue = "4.88";
				break;
			case "8 GI":
				convertedValue = "4";
				break;
			case "10 GI":
				convertedValue = "3";
				break;
			case "12 GI":
				convertedValue = "2.5";
				break;
			case "13 GI":
				convertedValue = "2.2";
				break;
			case "14 GI":
				convertedValue = "2";
				break;
			case "16 GI":
				convertedValue = "1.6";
				break;
			case "14 x 14":
				convertedValue = "2 x 2";
				break;
			case "12 x 12":
				convertedValue = "2.5 x 2.5";
				break;
			default:
				convertedValue = "0";
			}

			return convertedValue;
		}

		function validateForm(filledForms) {
			var fieldErrors = [];

			// because multiple forms
			//for (var i = 0; i < filledForms.length; i++) {

			var elements = filledForms[0].filledFormDetails;
			var formPid = filledForms[0].formPid;
			for (var j = 0; j < elements.length; j++) {
				var element = elements[j];
				//cannot be empty
				if (element.value == "" || element.value == 'none') {
					fieldErrors.push(new FieldError(element.formElementName,
							element.value, element.formElementName
									+ " cannot be empty"));
				}
				//numeric only
				if ([ 'Area (cents)', 'No. of horizontal lines',
						'No. of cross/diagonal lines', 'Post Height (ft)',
						'Distance between posts (meter)', 'Rate/Running meter' ]
						.indexOf(element.formElementName) > -1) {
					if (isNaN(element.value)) {
						fieldErrors.push(new FieldError(
								element.formElementName, element.value,
								element.formElementName + " must be a number"));
					}
				}
			}
			//}
			return fieldErrors;
		}
	</script>

	<!-- Form js_code validating fields -->
	<script type="text/javascript">
		function validateForm(filledForms) {
			var fieldErrors = [];
			var elements = filledForms[0].filledFormDetails;
			var formPid = filledForms[0].formPid;
			for (var j = 0; j < elements.length; j++) {
				var element = elements[j];
				//cannot be empty
				if (element.value == "" || element.value == 'none') {
					fieldErrors.push(new FieldError(element.formElementName,
							element.value, element.formElementName
									+ " cannot be empty"));
				}
				//numeric only
				if ([ 'Area (cents)', 'No. of horizontal lines',
						'No. of cross/diagonal lines', 'Post Height (ft)',
						'Distance between posts (meter)', 'Rate/Running meter' ]
						.indexOf(element.formElementName) > -1) {
					if (isNaN(element.value)) {
						fieldErrors.push(new FieldError(
								element.formElementName, element.value,
								element.formElementName + " must be a number"));
					}
				}
			}
			return fieldErrors;
		}
	</script>

	<!-- Form js_code script for validation -->
	<script type="text/javascript">
		function validateElement(elementName, elementValue, inElements) {
			var outElements = [];
			outElements[0] = {
				errorMsg : ''
			};
			switch (elementName) {
			case "Name":
				//required, min = 3, max = 30
				if (elementValue === '') {
					outElements[0].errorMsg = "Name is required."
				} else if (!(/^.{3,30}$/.test(elementValue))) {
					outElements[0].errorMsg = "Invalid length. Name Should be atleast 3 chars and atmost 30 chars long."
				}
				break;
			case "Email":
				//required, email format
				var emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
				if (elementValue === '') {
					outElements[0].errorMsg = "Email is required."
				} else if (!(emailRegex.test(elementValue))) {
					outElements[0].errorMsg = "Invalid Email.";
				}
				break;
			case "Phone No":
				//required,number, min = 6, max = 13
				if (elementValue === '') {
					outElements[0].errorMsg = "Phone No is required."
				} else if (!(/^\d*$/.test(elementValue))) {
					outElements[0].errorMsg = "Invalid Phone No. Must be a digit."
				} else if (!(/^.{6,13}$/.test(elementValue))) {
					outElements[0].errorMsg = "Invalid length. Phone No Should be atleast 6 digits and atmost 13 digits long."
				}
				break;
			case "Gender":
				//required
				if (elementValue === '') {
					outElements[0].errorMsg = "Gender is required."
				}
				break;
			case "Year of establishment":
				break;
			default:
			}
			return outElements;
		}

		function validateAllElements(inElements, elementName, elementValue) {
			var outElements = [];
			outElements[0] = {
				errorMsg : ''
			};
			inElements
					.forEach(function(entry) {
						var elementName = String(entry.name);
						var elementValue = String(entry.value);

						switch (elementName) {
						case "Name":
							//required, min = 3, max = 30
							if (elementValue === '') {
								outElements[0].errorMsg += "Name is required.\n"
							} else if (!(/^.{3,30}$/.test(elementValue))) {
								outElements[0].errorMsg += "Invalid length. Name Should be atleast 3 chars and atmost 30 chars long.\n"
							}
							break;
						case "Email":
							//required, email format
							var emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
							if (elementValue === '') {
								outElements[0].errorMsg += "Email is required.\n"
							} else if (!(emailRegex.test(elementValue))) {
								outElements[0].errorMsg += "Invalid Email.\n";
							}
							break;
						case "Phone No":
							//required,number, min = 6, max = 13
							if (elementValue === '') {
								outElements[0].errorMsg += "Phone No is required.\n"
							} else if (!(/^\d*$/.test(elementValue))) {
								outElements[0].errorMsg += "Invalid Phone No. Must be a digit.\n"
							} else if (!(/^.{6,13}$/.test(elementValue))) {
								outElements[0].errorMsg += "Invalid length. Phone No Should be atleast 6 digits and atmost 13 digits long.\n"
							}
							break;
						case "Gender":
							//required
							if (elementValue === '') {
								outElements[0].errorMsg += "Gender is required.\n"
							}
							break;
						default:
						}
					});
			return outElements;
		}
	</script>

	<script type="text/javascript">
		function validateMobileElements(inElements) {
			var reqElem = new Object();
			result = {
				outElements : [],
				errorMsg : ''
			};
			inElements.forEach(function(entry) {
				var elementName = String(entry.name);
				var elementValue = String(entry.value);
				if (elementName == "Mode of travel" || elementName == "Kilometer traveled") {
					reqElem[elementName] = elementValue;
				}
				if (elementName == "Mode of travel" || elementName == "Kilometer traveled") {
					reqElem[elementName] = elementValue;
				}
			});
			var travelExpense = 0;
			if(reqElem["Mode of travel"] == "Car"){
				travelExpense = 7.5 * reqElem["Kilometer traveled"];
			} else if(reqElem["Mode of travel"] == "Bike"){
				travelExpense = 3.5 * reqElem["Kilometer traveled"];
			}
			
			var elem = new Object();
			elem["name"] = "Travel expense";
			elem["value"] = travelExpense;
			result.outElements.push(elem);
			
			return result;
		}

		
		function validateMobileElements(inElements) {
			var reqElem = new Object();
			result = {
				outElements : [],
				errorMsg : ''
			};
			inElements.forEach(function(entry) {
				var elementName = String(entry.name);
				var elementValue = String(entry.value);
				if (elementName == "Mode of travel" || elementName == "Kilometer traveled") {
					reqElem[elementName] = elementValue;
				}
				if(elementName == "Food expense" || elementName == "Travel expense" || elementName == "Accommodation" ){
					reqElem[elementName] = elementValue;
				}
				if(elementName == "Amount 1" || elementName == "Amount 2" || elementName == "Amount 3" || elementName == "Amount 4" || elementName == "Amount 5" ){
					reqElem[elementName] = elementValue;
				}
			});
			var travelExpense = 0,foodExpense = 0,accomodation = 0;
			var amount1 = 0,amount2 = 0,amount3 = 0,amount4 = 0,amount5 = 0,totalAmount = 0;
			if(reqElem["Mode of travel"] == "Car"){
				travelExpense = 7.5 * reqElem["Kilometer traveled"];
			} else if(reqElem["Mode of travel"] == "Bike"){
				travelExpense = 3.5 * reqElem["Kilometer traveled"];
			}
			foodExpense = reqElem["Food expense"]==""?0:reqElem["Food expense"];
			accomodation = reqElem["Accommodation"]==""?0:reqElem["Accommodation"];
			amount1 = reqElem["Amount 1"]==""?0:reqElem["Amount 1"];
			amount2 = reqElem["Amount 2"]==""?0:reqElem["Amount 2"];
			amount3 = reqElem["Amount 3"]==""?0:reqElem["Amount 3"];
			amount4 = reqElem["Amount 4"]==""?0:reqElem["Amount 4"];
			amount5 = reqElem["Amount 5"]==""?0:reqElem["Amount 5"];
			
			totalAmount = parseFloat(foodExpense)+parseFloat(travelExpense)
			+parseFloat(accomodation)+parseFloat(amount1)+parseFloat(amount2)+parseFloat(amount3)
			+parseFloat(amount4)+parseFloat(amount5);

			var elem = new Object();
			elem["name"] = "Travel expense";
			elem["value"] = travelExpense;
			result.outElements.push(elem);
            var totalElem = new Object();
			totalElem ["name"] = "Total Amount";
			totalElem ["value"] = totalAmount;
			result.outElements.push(totalElem);
			
			return result;
		}
		
		
		function validateElement(elementName, elementValue, inElements) {
			var outElements = [];
			outElements[0] = {
				errorMsg : ''
			};
			if (isNaN(elementValue)) {
				outElements[0].errorMsg = elementName + " must be a number."
			}
			return outElements;
		}

		function validateAllElements(inElements, elementName, elementValue) {
			var outElements = [];
			outElements[0] = {
				errorMsg : ''
			};
			inElements.forEach(function(entry) {
				var elementName = String(entry.name);
				var elementValue = String(entry.value);
				//cnumeric only
				if (isNaN(elementValue)) {
					outElements[0].errorMsg += elementName
							+ " must be a number.\n"
				}
			});
			return outElements;
		}

		//bluescope
		function validateElement(elementName, elementValue, inElements) {
			var outElements = [];
			outElements[0] = {
				errorMsg : ''
			};
			if (isNaN(elementValue)) {
				outElements[0].errorMsg = elementName + " must be a number."
			}
			return outElements;
		}

		function validateAllElements(inElements, elementName, elementValue) {
			var outElements = [];
			outElements[0] = {
				errorMsg : ''
			};
			inElements.forEach(function(entry) {
				var elementName = String(entry.name);
				var elementValue = String(entry.value);
				//cnumeric only
				if (isNaN(elementValue)) {
					outElements[0].errorMsg += elementName
							+ " must be a number.\n"
				}
			});
			return outElements;
		}
		
		function validateMobileElements(inElements) {
			var abp = 0;
			var indent = 0;
			var compliance = 0;
			result = {
				outElements : [],
				errorMsg : ''
			};
			inElements.forEach(function(entry) {
				var elementName = String(entry.name);
				var elementValue = String(entry.value);
				if (elementName == "Expense Tracker") {
					if (elementValue.length > 0 && !isNaN(elementValue)) {
						abp = parseFloat(elementValue);
					}
				}
				if (elementName == "INDENT") {
					if (elementValue.length > 0 && !isNaN(elementValue)) {
						indent = parseFloat(elementValue);
					}
				}
				if (elementName == "%Compliance") {
					if (!isNaN(abp) && !isNaN(indent)) {
						elementValue = (indent / abp) * 100;
						compliance = elementValue.toFixed(2);
					}
				}
			});
			var elem = new Object();
			elem["name"] = "%Compliance";
			elem["value"] = 125;
			result.outElements.push(elem);
			return result;
		}
	</script>

	<!-- Varna quotations hide and show for mobile  -->
	<script type="text/javascript">
		/* Varna */
		function changeElementVisibility(elementName, elementValue, inElements,
				isChecked) {
			var outElements = [];
			switch (elementName) {
			case "Additional works":
				if (elementValue == "Yes") {
					for ( var i in inElements) {
						var elemObj = inElements[i];
						if (elemObj.questionName == "Work Type") {
							elemObj.visibility = true;
							outElements.push(elemObj);
						}
					}
				} else {
					for ( var i in inElements) {
						var elemObj = inElements[i];
						if (elemObj.questionName == "Work Type") {
							elemObj.visibility = false;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Polishing Type") {
							elemObj.visibility = false;
							outElements.push(elemObj);
						}
					}
				}
				break;
			case "Work Type":
				if (elementValue == "Polishing") {
					for ( var i in inElements) {
						var elemObj = inElements[i];
						if (elemObj.questionName == "Polishing Type") {
							if (isChecked) {
								elemObj.visibility = isChecked;
								outElements.push(elemObj);
							} else {
								elemObj.visibility = isChecked;
								outElements.push(elemObj);
								for ( var i in inElements) {
									var elemObj = inElements[i];
									if (elemObj.questionName == "Machine Polish Type") {
										elemObj.visibility = false;
										outElements.push(elemObj);
									}
									if (elemObj.questionName == "Manual Polish Type") {
										elemObj.visibility = false;
										outElements.push(elemObj);
									}
									if (elemObj.questionName == "Polishing Area") {
										elemObj.visibility = false;
										outElements.push(elemObj);
									}
									if (elemObj.questionName == "Preferred Area") {
										elemObj.visibility = false;
										outElements.push(elemObj);
									}
								}

							}
						}
					}
				}
				if (elementValue == "Etching") {
					for ( var i in inElements) {
						var elemObj = inElements[i];
						if (elemObj.questionName == "Etching Type") {
							elemObj.visibility = isChecked;
							outElements.push(elemObj);
						}
					}
				}
				if (elementValue == "Acid works") {
					for ( var i in inElements) {
						var elemObj = inElements[i];
						if (elemObj.questionName == "Acid Works Type") {
							elemObj.visibility = isChecked;
							outElements.push(elemObj);
						}
					}
				}
				if (elementValue == "Cut outs") {
					for ( var i in inElements) {
						var elemObj = inElements[i];
						if (elemObj.questionName == "Counter Cut") {
							elemObj.visibility = isChecked;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Patch cut out") {
							elemObj.visibility = isChecked;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "L cut out") {
							elemObj.visibility = isChecked;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Planilaque cut out") {
							elemObj.visibility = isChecked;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Exhaust fan (corner)") {
							elemObj.visibility = isChecked;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Exhaust fan (center)") {
							elemObj.visibility = isChecked;
							outElements.push(elemObj);
						}
						//holes
						if (elemObj.questionName == "Hole Diameter") {
							elemObj.visibility = isChecked;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "No of Holes") {
							elemObj.visibility = isChecked;
							outElements.push(elemObj);
						}
					}
				}
				break;
			case "Polishing Type":
				if (elementValue == "None") {
					for ( var i in inElements) {
						var elemObj = inElements[i];
						if (elemObj.questionName == "Machine Polish") {
							elemObj.visibility = false;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Manual Polish") {
							elemObj.visibility = false;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Polishing areas") {
							elemObj.visibility = false;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Preferred Area") {
							elemObj.visibility = false;
							outElements.push(elemObj);
						}
					}
				} else if (elementValue == "Machine Polish") {
					for ( var i in inElements) {
						var elemObj = inElements[i];
						if (elemObj.questionName == "Machine Polish") {
							elemObj.visibility = true;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Manual Polish") {
							elemObj.visibility = false;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Polishing areas") {
							elemObj.visibility = true;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Preferred Area") {
							elemObj.visibility = true;
							outElements.push(elemObj);
						}
					}
				} else if (elementValue == "Manual Polish") {
					for ( var i in inElements) {
						var elemObj = inElements[i];
						if (elemObj.questionName == "Manual Polish") {
							elemObj.visibility = true;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Machine Polish") {
							elemObj.visibility = false;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Polishing areas") {
							elemObj.visibility = true;
							outElements.push(elemObj);
						}
						if (elemObj.questionName == "Preferred Area") {
							elemObj.visibility = true;
							outElements.push(elemObj);
						}
					}
				}
				break;
			default:
			}
			return outElements;
		}
		function validateAllElements(inElements, elementName, elementValue) {
			var outElements = [];
			outElements[0] = {
				errorMsg : ''
			};
			return outElements;
		}
	</script>

	<!-- Varna quotations hide and show for Web  -->
	<script type="text/javascript">
		/* Varna */
		function initializeForm(isEditForm) {
			var elAddWorks = $("label:contains('Additional works')");
			var elWrkType = $("label:contains('Work Type')");
			var elPolType = $("label:contains('Polishing Type')");
			var elMachType = $("label:contains('Machine Polish')");
			var elManuType = $("label:contains('Manual Polish')");
			var elPolArea = $("label:contains('Polishing areas')");
			var elPrefArea = $("label:contains('Preferred Area')");
			var elEtchType = $("label:contains('Etching Type')");
			var elAcdWorkType = $("label:contains('Acid Works Type')");
			var elCntrCut = $("label:contains('Counter Cut')");
			var elPtchCutOut = $("label:contains('Patch cut out')");
			var elLCutOut = $("label:contains('L cut out')");
			var elPlnqueCutOut = $("label:contains('Planilaque cut out')");
			var elExhstFanCorner = $("label:contains('Exhaust fan (corner)')");
			var elExhstFanCenter = $("label:contains('Exhaust fan (center)')");
			var elHlsDiameter = $("label:contains('Hole Diameter')");
			var elNoHoles = $("label:contains('No of Holes')");

			elAddWorks.closest('div').find('input[type=radio]').change(
					function() {
						if (this.value == 'Yes') {
							hideAllElements();
							elWrkType.closest('div').show();
						} else if (this.value == 'No') {
							hideAllElements();
							clearAllElements();
						}
					});

			elWrkType.closest('div').find('input[type=checkbox]').change(
					function() {
						if ($(this).val() == "Polishing") {
							if ($(this).is(':checked')) {
								elPolType.closest('div').show();
							} else {
								elPolType.closest('div').hide();
								elPolType.closest('div').find(
										'input[type=radio]:checked')
										.removeAttr('checked');
								elMachType.closest('div').find(
										'input[type=radio]:checked')
										.removeAttr('checked');
								elManuType.closest('div').find(
										'input[type=radio]:checked')
										.removeAttr('checked');
								elPolArea.closest('div').find(
										'input[type=radio]:checked')
										.removeAttr('checked');
								elPrefArea.closest('div').find(
										'input[type=text]').val('');
							}
						}
						if ($(this).val() == "Etching") {
							if ($(this).is(':checked')) {
								elEtchType.closest('div').show();
							} else {
								elEtchType.closest('div').hide();
								elEtchType.closest('div').find('select')
										.first().val('none');
							}
						}
						if ($(this).val() == "Acid works") {
							if ($(this).is(':checked')) {
								elAcdWorkType.closest('div').show();
							} else {
								elAcdWorkType.closest('div').hide();
								elAcdWorkType.closest('div').find('select')
										.first().val('none');
							}
						}
						if ($(this).val() == "Cut outs") {
							if ($(this).is(':checked')) {
								showCutOutElements();
							} else {
								hideAndClearCutOutElements();
							}
						}
						if ($(this).val() == "Holes") {
							if ($(this).is(':checked')) {
								elHlsDiameter.closest('div').show();
								elNoHoles.closest('div').show();
							} else {
								elHlsDiameter.closest('div').hide();
								elNoHoles.closest('div').hide();
								elHlsDiameter.closest('div').find('input').val(
										'');
								elNoHoles.closest('div').find('input').val('');
							}

						}
					});

			elPolType.closest('div').find('input[type=radio]').change(
					function() {
						if ($(this).val() == "None") {
							elMachType.closest('div').hide();
							elManuType.closest('div').hide();
							elPolArea.closest('div').hide();
							elPrefArea.closest('div').hide();

							elMachType.closest('div').find(
									'input[type=radio]:checked').removeAttr(
									'checked');
							elManuType.closest('div').find(
									'input[type=radio]:checked').removeAttr(
									'checked');
							elPolArea.closest('div').find(
									'input[type=radio]:checked').removeAttr(
									'checked');
							elPrefArea.closest('div').find('input[type=text]')
									.val('');
						} else if ($(this).val() == "Machine Polish") {
							elMachType.closest('div').show();
							elManuType.closest('div').hide();
							elPolArea.closest('div').show();
							elPrefArea.closest('div').show();

							elMachType.closest('div').find(
									'input[type=radio]:checked').removeAttr(
									'checked');
							elManuType.closest('div').find(
									'input[type=radio]:checked').removeAttr(
									'checked');
						} else if ($(this).val() == "Manual Polish") {
							elMachType.closest('div').hide();
							elManuType.closest('div').show();
							elPolArea.closest('div').show();
							elPrefArea.closest('div').show();

							elMachType.closest('div').find(
									'input[type=radio]:checked').removeAttr(
									'checked');
							elManuType.closest('div').find(
									'input[type=radio]:checked').removeAttr(
									'checked');
						}
					});

			function hideAndClearCutOutElements() {
				//hide
				elCntrCut.closest('div').hide();
				elCntrCut.closest('div').hide();
				elPtchCutOut.closest('div').hide();
				elLCutOut.closest('div').hide();
				elPlnqueCutOut.closest('div').hide();
				elExhstFanCorner.closest('div').hide();
				elExhstFanCenter.closest('div').hide();

				//clear
				elCntrCut.closest('div').find('input').val('');
				elPtchCutOut.closest('div').find('input').val('');
				elLCutOut.closest('div').find('input').val('');
				elPlnqueCutOut.closest('div').find('input').val('');
				elExhstFanCorner.closest('div').find('input').val('');
				elExhstFanCenter.closest('div').find('input').val('');
			}

			function showCutOutElements() {
				elCntrCut.closest('div').show();
				elCntrCut.closest('div').show();
				elPtchCutOut.closest('div').show();
				elLCutOut.closest('div').show();
				elPlnqueCutOut.closest('div').show();
				elExhstFanCorner.closest('div').show();
				elExhstFanCenter.closest('div').show();
			}

			function hideAllElements() {
				elWrkType.closest('div').hide();
				elPolType.closest('div').hide();
				elMachType.closest('div').hide();
				elManuType.closest('div').hide();
				elPolArea.closest('div').hide();
				elPrefArea.closest('div').hide();
				elEtchType.closest('div').hide();
				elAcdWorkType.closest('div').hide();
				elCntrCut.closest('div').hide();
				elCntrCut.closest('div').hide();
				elPtchCutOut.closest('div').hide();
				elLCutOut.closest('div').hide();
				elPlnqueCutOut.closest('div').hide();
				elExhstFanCorner.closest('div').hide();
				elExhstFanCenter.closest('div').hide();
				elHlsDiameter.closest('div').hide();
				elNoHoles.closest('div').hide();
			}

			function clearAllElements() {
				elWrkType.closest('div').find('input[type=checkbox]:checked')
						.removeAttr('checked');
				elPolType.closest('div').find('input[type=radio]:checked')
						.removeAttr('checked');
				elEtchType.closest('div').find('select').first().val('none');
				elAcdWorkType.closest('div').find('select').first().val('none');

				elMachType.closest('div').find('input[type=radio]:checked')
						.removeAttr('checked');
				elManuType.closest('div').find('input[type=radio]:checked')
						.removeAttr('checked');
				elPolArea.closest('div').find('input[type=radio]:checked')
						.removeAttr('checked');
				elPrefArea.closest('div').find('input[type=text]').val('');

				hideAndClearCutOutElements();

				//holes
				elHlsDiameter.closest('div').hide();
				elNoHoles.closest('div').hide();
				elHlsDiameter.closest('div').find('input').val('');
				elNoHoles.closest('div').find('input').val('');
			}
		}

		function postFormCompleteEvent(params) {
			var elAddWorks = $("label:contains('Additional works')");
			var elWrkType = $("label:contains('Work Type')");
			var elPolType = $("label:contains('Polishing Type')");
			var elMachType = $("label:contains('Machine Polish')");
			var elManuType = $("label:contains('Manual Polish')");
			var elPolArea = $("label:contains('Polishing areas')");
			var elPrefArea = $("label:contains('Preferred Area')");
			var elEtchType = $("label:contains('Etching Type')");
			var elAcdWorkType = $("label:contains('Acid Works Type')");
			var elCntrCut = $("label:contains('Counter Cut')");
			var elPtchCutOut = $("label:contains('Patch cut out')");
			var elLCutOut = $("label:contains('L cut out')");
			var elPlnqueCutOut = $("label:contains('Planilaque cut out')");
			var elExhstFanCorner = $("label:contains('Exhaust fan (corner)')");
			var elExhstFanCenter = $("label:contains('Exhaust fan (center)')");
			var elHlsDiameter = $("label:contains('Hole Diameter')");
			var elNoHoles = $("label:contains('No of Holes')");

			var adtnlWorks = elAddWorks.closest('div').find(
					'input[type=radio]:checked').val();
			console.log("Additional Works = " + adtnlWorks);
			if (adtnlWorks == "Yes") {
				elWrkType.closest('div').show();
				elWrkType.closest('div').find('input[type=checkbox]:checked')
						.each(function() {
							if ($(this).val() == "Polishing") {
								elPolType.closest('div').show();
							}
							if ($(this).val() == "Etching") {
								elEtchType.closest('div').show();
							}
							if ($(this).val() == "Acid works") {
								elAcdWorkType.closest('div').show();
							}
							if ($(this).val() == "Cut outs") {
								elCntrCut.closest('div').show();
								elCntrCut.closest('div').show();
								elPtchCutOut.closest('div').show();
								elLCutOut.closest('div').show();
								elPlnqueCutOut.closest('div').show();
								elExhstFanCorner.closest('div').show();
								elExhstFanCenter.closest('div').show();
							}
							if ($(this).val() == "Holes") {
								elHlsDiameter.closest('div').show();
								elNoHoles.closest('div').show();
							}
						});
				var polishType = elPolType.closest('div').find(
						'input[type=radio]:checked').val();
				if (polishType == "Machine Polish") {
					elMachType.closest('div').show();
					elManuType.closest('div').hide();
					elPolArea.closest('div').show();
					elPrefArea.closest('div').show();
				}
				if (polishType == "Manual Polish") {
					elMachType.closest('div').hide();
					elManuType.closest('div').show();
					elPolArea.closest('div').show();
					elPrefArea.closest('div').show();
				}
			}
		}

		//function in sales order document
		function calculateTotal(inElements) {
			var totalRate,unitQty, discount, amount, tax, total, outElements;
			outElements = {};
			if (inElements.unit_quantity === null
					|| inElements.unit_quantity === ""
					|| inElements.unit_quantity === undefined
					|| inElements.unit_quantity === 0) {
				unitQty = 1;
			} else {
				unitQty = inElements.unit_quantity;
			}

			totalRate = (unitQty * inElements.quantity)
					* inElements.selling_rate;
			if (inElements.discount_percentage === undefined
					|| inElements.discount_percentage === null) {
				discount = 0;
			} else {
				discount = (totalRate * inElements.discount_percentage) / 100;
			}
			amount = totalRate - discount;
			if (inElements.tax_percentage === undefined
					|| inElements.tax_percentage === null) {
				tax = 0;
			} else {
				tax = (amount * inElements.tax_percentage) / 100;
			}
			total = amount + tax;
			outElements.total = total;
			return outElements;
		}
	</script>
</body>
</html>