function getQueryString(formname) {
	var form = document.forms[formname];
	var qstr = "";
	function GetElemValue(name, value) {
		qstr += (qstr.length > 0 ? "&" : "")
		+ escape(name).replace(/\+/g, "%2B") + "="
		+ escape(value ? value : "").replace(/\+/g, "%2B");
		//+ escape(value ? value : "").replace(/\n/g, "%0D");
	}
	var elemArray = form.elements;
	for ( var i = 0; i < elemArray.length; i++) {
		var element = elemArray[i];
		var elemType = element.type.toUpperCase();
		var elemName = element.name;
		if (elemName) {
			if (elemType == "TEXT"
			|| elemType == "TEXTAREA"
			//|| elemType == "PASSWORD"
			//|| elemType == "BUTTON"
			//|| elemType == "RESET"
			//|| elemType == "SUBMIT"
			//|| elemType == "FILE"
			//|| elemType == "IMAGE"
			|| elemType == "HIDDEN")
				GetElemValue(elemName, element.value);
			else if (elemType == "CHECKBOX" && element.checked)
				GetElemValue(elemName,
				element.value ? element.value : "On");
			else if (elemType == "RADIO" && element.checked)
				GetElemValue(elemName, element.value);
			else if (elemType.indexOf("SELECT") != -1)
				for ( var j = 0; j < element.options.length; j++) {
					var option = element.options[j];
					if (option.selected)
						GetElemValue(elemName,
						option.value ? option.value : option.text);
				}
		}
	}
	return qstr;
}

function changeColor(elementChanged, elementToChange) {
	document.getElementById(elementToChange).value = 'AA'
		+ document.getElementById(elementChanged).value.substring(4,6)
		+ document.getElementById(elementChanged).value.substring(2,4)
		+ document.getElementById(elementChanged).value.substring(0,2);
}
