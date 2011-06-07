/* Initial global setups */
if (Globals == null) {
    var Globals = { 
    		'version': '0.0.3a'
    		,'tempTextAreaID': 'tempTextAreaID.'
    		,'editBox': 'editBox'
    		,'editLinks': 'editLinks'    	
    		,'sessionID': 'unknown'	
    		,'tryToLoadCount': 0
    	};
};
/* Highlight them */
Globals.highlightFields = function(elementIDs){
	Object.each(elementIDs, function (id){
		if($(id)){
			$(id).setStyle('border','1px solid red');
			$(id).highlight('#ddf');
		}
	});
};
/* NO Highlight them */
Globals.noHighlightFields = function(elementIDs){
	Object.each(elementIDs, function (id){
		if($(id)){
			$(id).setStyle('border','1px solid #7F9DB9');
		}
	});
};
/* Blink element */
Globals.blinkIt = function(id){
	if(!$(id)) return ;
	
	var oldStyle = $(id).getStyle('border');
	
 	$$('span.edit').each(function(el){
 		el.setStyle('border','0px solid white');
	});
	if(!oldStyle.contains('3px')){ // quick check
 		$(id).setStyle('border','3px solid green');
 	}
};
/* Hide/Show Editor Links */
Globals.hideEditorLinksExcept = function(id){
 	$$('sup.editorlinks').each(function(el){
 		if(el.id != (id + '.editorlinks')){
    		el.fade('hide');
    	}
	});    	
};
Globals.hideEditBox = function(){
	$(Globals.editBox).innerHTML = "&nbsp;";
};
/* Edit online */
Globals.editIt = function(divId, handleName, actionName){		
	// hide editor links
	Globals.hideEditorLinksExcept(divId);
	// Get initial html body
    Globals.sendMessage({
       handler: handleName,
       action: actionName,
       key: divId,
       dest: Globals.editBox
    });
};
/* Upload online 
Globals.uploadIt = function(elemId, handleName, actionName){
	var localTempTextAreaId = elemId + ".textarea";
	if(!($(localTempTextAreaId)) || !($(elemId))) return ;
	
	// Get initial html body
    Globals.sendMessage({
        handler: handleName,
        action: actionName,
        value:  $(localTempTextAreaId).value,
        dest: divId
    });
};
*/
/* JS Loader */
Globals.loadJS = function (pathToJS, onLoadFn) {
    new Asset.javascript(pathToJS, {
        onload: onLoadFn
    });
};
/* Global initializator */
window.addEvent('domready', function () {
    Globals.initializeAll();
});
/* Initialize all stages of page loading */
Globals.initializeAll = function () {
    // Initialize load stages
    Globals.loadStages();
};
/* Defines is stages loaded */
Globals.isLoadedStage1 = function () {
    return Globals.isLoadedStage11()
    	&& Globals.isLoadedStage12()
    	&& Globals.isLoadedStage13();
};
Globals.isLoadedStage11 = function () {
    return typeof DWREngine != "undefined";
};
Globals.isLoadedStage12 = function () {
    return typeof DWRUtil != "undefined";
};
Globals.isLoadedStage13 = function () {
    return typeof MessageHandler != "undefined";
};
/* JS loading stages */
Globals.loadStages = function () {
    Globals.loadStage1();
};
Globals.loadStage1 = function () {
    if (!Globals.isLoadedStage1()) {
        Globals.loadStage11()
    };
};
Globals.loadStage11 = function () {
    if (!Globals.isLoadedStage11()) {
        Globals.loadJS("dwr/engine.js", Globals.loadStage12);
    }
};
Globals.loadStage12 = function () {
    if (!Globals.isLoadedStage12()) {
        Globals.loadJS("dwr/util.js", Globals.loadStage13);
    }
};
Globals.loadStage13 = function () {
    if (!Globals.isLoadedStage13()) {
        Globals.loadJS("dwr/interface/MessageHandler.js", Globals.loadStage2);
    }
};
Globals.loadStage2 = function () {
    // Check that all stages passed #1
    if (!Globals.isLoadedStage13()) {
        Globals.debug("ERROR: Could not load stage #1");
        return;
    };
	// load CSS files
	Globals.loadCSSFile(); 
};
// load CSS files - #1 initial page loading stage
Globals.loadCSSFile = function(){
    // Get initial html body
    Globals.sendMessage({
        handler: 'General',
        action: 'loadCSSFile'
    });   
};
// load JS files - #2 initial page loading stage
Globals.loadJSFile = function(){
    // Get initial html body
    Globals.sendMessage({
        handler: 'General',
        action: 'loadJSFile'
    });   
};
// load initial page
Globals.loadInitialPage = function(){
    // Get initial html body
    Globals.sendMessage({
        handler: 'General',
        action: 'getInitialBody',
        dest: 'body'
    });
};
/* Set html content by contents map */
Globals.setHtmlContents = function (contentsMap) {
    Object.each(contentsMap, function (htmlContent, elemId) {
        if ($(elemId) && $(elemId).innerHTML != undefined && htmlContent) {
        	if(htmlContent.search(/^close_me/i) >= 0){
        		$(elemId).fade('hide');
        	}else{
	            $(elemId).innerHTML = htmlContent;
	            $(elemId).fade('show');
            }
        };
    });
};
/* Test DOM object existance */
Globals.chk = function (obj) {
    return !!(obj || obj === 0);
};
/* Send message to server*/
Globals.sendMessage = function (data) {
    var message = {
        data: data,
        sessionID: Globals.sessionID,
        url: document.URL
    };
    MessageHandler.sendMessage(message, Globals.applyIncomingMessages);
};
Globals.confirmAndSendMessage = function (data) {
	if(!confirm("Confirm action!\nПодтвердите действие!"))return;
	Globals.sendMessage(data);
};
/* Receive message from server */
Globals.applyIncomingMessages = function (messages) {
    Array.each(messages, function (message, messageIndex) {
        // check for error
        if (Globals.chk(message.error)) {
            alert(message.error);
        };  
    	// check for existence of sessionID
    	if(!Globals.chk(message.sessionID)){
    		alert('Could not initialize Sesson ID!');
    		return;
    	}
    	// is it new session id!?
    	if(Globals.sessionID != message.sessionID){
    		if(Globals.tryToLoadCount > 3){
    			alert('Could not load pages, try to load count: ' + Globals.tryToLoadCount);
    			return;
    		};
    		// just for debug
    		if(Globals.tryToLoadCount > 1){
    			alert("DEBUG!!! Session (" + Globals.sessionID + ") expired, new session: " + message.sessionID);
    		};
    		Globals.sessionID = message.sessionID;
    		Globals.tryToLoadCount += 1;
			if(!Globals.chk(message.styleSheet)){
				// Step #1 - initial page's CSS loading stage
				Globals.loadCSSFile();
				return;
			};			
    	}
        // load CSS
        if (Globals.chk(message.styleSheet)) {
        	Asset.css(message.styleSheet);
        	Globals.loadJSFile();
        };
        // check JS files
        if (Globals.chk(message.JSFile)) {
           	Globals.loadJS(message.JSFile, Globals.loadInitialPage);
        };
     
        // check for html elements
        if (Globals.chk(message.htmlContents)) {
            Globals.setHtmlContents(message.htmlContents);
        };
        // no hightlight elements        
        if (Globals.chk(message.noHighlightFields)) {
            Globals.noHighlightFields(message.noHighlightFields);
        };
        // highlight elements
        if (Globals.chk(message.highlightFields)) {
            Globals.highlightFields(message.highlightFields);
        };
    });
};

