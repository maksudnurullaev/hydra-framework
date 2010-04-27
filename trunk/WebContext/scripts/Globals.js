if (Globals == null){
	var Globals = {'version': '0.0.1'};
};

/* Values */
Globals.DivContentID   = "content";
Globals.DivInvisibleID = "stack";

/* Debugger */
Globals.DebugMode = true;

Globals.debug = function(message){
	if(Globals.DebugMode) alert(message);
};

/* JS Loader */
Globals.loadJS = function(pathToJS, onLoadFn){
	new Asset.javascript(pathToJS, {onload: onLoadFn});
};

/* Global initializator */
window.addEvent('domready', function() {
    //alert("The DOM is ready.");
    Globals.initializeAll();
});


Globals.initializeAll = function() {

	// Initialize load stages
	Globals.loadStages();
};

/* Define stages status */
Globals.isLoadedStage1 = function(){
	return Globals.isLoadedStage11() 
			&& Globals.isLoadedStage12()
			&& Globals.isLoadedStage13();
};

Globals.isLoadedStage11 = function(){
	return typeof DWREngine != "undefined";	
};

Globals.isLoadedStage12 = function(){
	return typeof DWRUtil != "undefined";
};

Globals.isLoadedStage13 = function(){
	return typeof MessageHandler != "undefined";
};

Globals.isLoadedStage14 = function(){
	return typeof MessageHandler.applyIncomingMessages != "undefined";
};

/* Loading Stages */
Globals.loadStages = function(){
	Globals.loadStage1();
};

Globals.loadStage1 = function() {
	if (!Globals.isLoadedStage1()) {
		Globals.loadStage11()
	}
};

Globals.loadStage11 = function() {
	if (!Globals.isLoadedStage11()) {
		Globals.loadJS("dwr/engine.js", Globals.loadStage12);
	}
};

Globals.loadStage12 = function() {
	if (!Globals.isLoadedStage12()) {
		Globals.loadJS("dwr/util.js", Globals.loadStage13);
	}
};

Globals.loadStage13 = function() {
	if (!Globals.isLoadedStage13()) {
		Globals.loadJS("dwr/interface/MessageHandler.js", Globals.loadStage14);
	}
};

Globals.loadStage14 = function() {
	if (!Globals.isLoadedStage14()) {
		Globals.loadJS("scripts/MessageHandler.js", Globals.loadStage2);
	}
};

Globals.loadStage2 = function(){
	// Check Stage #1
	if(!Globals.isLoadedStage1()){
		Globals.debug("ERROR: Could not load stage #1");
		return;
	}
	
	alert("!!!Start STAGE 2!!!");

};

Globals.clearContent = function(destId, saveTag){
	if (arguments.length == 1){
		$(destId).innerHTML = "";
		return false;
	}
	
	$each($(destId).childNodes, function(elem, elemIndex){
		if($(elem) && elem.nodeName && elem.nodeName.test(saveTag,"i") && elem.id){
			$(Globals.DivInvisibleID).appendChild(elem);
		}
	});
	$(destId).innerHTML = "";
	
	return false;
};

Globals.disbaleMainNav = function(disable){
	$each($$('#navlist a'), function(elem){
		elem.set('disabled', disable);
	});
};

/* GLOBAL - send message */
Globals.sendMessage = function(data){
	var message = {data:data};

	Globals.disbaleMainNav(true);
	
	MessageHandler.sendMessage(message, Globals.applyIncomingMessages);
};

/* GLOBAL - receive message(s) */
Globals.applyIncomingMessages = function(messages){
	$each(messages, function(message, messageIndex){		
		if($chk(message.data)){
			if($chk(message.data.what)){
				if(message.data.what.test("error.message","i")){
					alert(($chk(message.data.value) ? message.data.value : "ERROR: undefined"));
				}else if(message.data.what.test("html.content","i")){
					if($chk(message.data.dest)){
						if($(message.data.dest)){
							$(message.data.dest).innerHTML = ($chk(message.data.value) ? message.data.value : "undefined");
						}else{
							alert("Could not find 'message.data.dest' element: " + message.data.dest);
						}
					}
				}else{
					alert("I dont know is 'what': " + message.data.what);
				}
			}else{
				alert("I dont know what to do with 'message.data': " + message.data);
			}
		}else{
			alert("I dont know what to do with 'message.data': " + message.data);
		}
	});
	Globals.disbaleMainNav(false);
};
