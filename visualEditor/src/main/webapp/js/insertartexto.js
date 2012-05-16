function insertAtCursor(myFieldID, myValue) {
	var myField = document.getElementById(myFieldID);
    
	var textAreaScrollPosition = myField.scrollTop;
	
    if (document.selection) {
        myField.focus();
        sel = document.selection.createRange();
        sel.text = myValue;
    } else if (myField.selectionStart || myField.selectionStart == '0') {
        myField.focus();
        var startPos = myField.selectionStart;
        var endPos = myField.selectionEnd;
        myField.value = myField.value.substring(0, startPos) + myValue + myField.value.substring(endPos, myField.value.length);
        myField.setSelectionRange(endPos+myValue.length, endPos+myValue.length);
    } else {
        myField.value += myValue;
    }
	myField.scrollTop = textAreaScrollPosition;
}

function insertFuncion (myFieldID, myValue, ULID, aIDCompleto, aID) {
	ULID = aID + ":" + aIDCompleto + ":" + ULID;
	
	var ulParametros = document.getElementById(ULID);
	var lista = ulParametros.getElementsByTagName("li");
	
	var texto = myValue + "("
	
	var pongocoma = false;
	for(var i = 0; i < lista.length; i++) {
		if (pongocoma) {
			texto += ", ";
			pongocoma=true;
		}
		texto += lista[i].innerHTML;
	}
	texto += ");";
	
	insertAtCursor(myFieldID, texto);
}