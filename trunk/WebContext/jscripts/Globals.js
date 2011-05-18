/* Initial global setups */
if (Globals == null) {
    var Globals = { 
    		'version': '0.0.3a'
    		,'tempTextAreaID': 'tempTextAreaID.'
    		,'workspaceEditDiv': 'workspace.edit'
    		,'workspaceEditDivBackup': 'workspace.edit.backup'
    		,'editLinksDiv': 'editLinks'    		
    	};
};
/* Blink element */
Globals.blinkIt = function(id){
	if(!$(id)) return ;
	
	var oldStyle = $(id).getStyle('border');
	
 	$$('div.edit').each(function(el){
 		el.setStyle('border','0px solid white');
	});
	if(!oldStyle.contains('3px')){ // It's enough
 		$(id).setStyle('border','3px solid green');
 		$(id).highlight('#000');
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
Globals.showEditorLinks = function(){
	$(Globals.getEditDivId()).setStyle('display','none');
 	$$('sup.editorlinks').each(function(el){
   		el.fade('show');
	});
};
Globals.getEditDivId = function(){
	if ($(Globals.workspaceEditDiv)) {
		return Globals.workspaceEditDiv;
	} 	
	return Globals.workspaceEditDivBackup;
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
        dest: Globals.getEditDivId()
    });
};
/* Upload online */
Globals.uploadIt = function(divId, handleName, actionName){
	var localTempTextAreaId = divId + ".textarea";
	//if($(localTempTextAreaId) && $(divId) && !Globals.chk($(divId).innerHTML)) return ;
	if(!($(localTempTextAreaId)) || !($(divId))) return ;
	
	// Get initial html body
    Globals.sendMessage({
        handler: handleName,
        action: actionName,
        value:  $(localTempTextAreaId).value,
        dest: divId
    });
};
/* Values */
Globals.DivContentID = "content";
Globals.DivInvisibleID = "stack";
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
    // Get initial html body
    Globals.sendMessage({
        handler: 'General',
        action: 'deployInitialFiles',
        url: document.URL,
    });    
	//Globals.setHtmlBody();
};
Globals.setHtmlBody = function(){
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
        
            $(elemId).innerHTML = htmlContent;
            if(
            	elemId.contains(Globals.workspaceEditDiv) ||
            	elemId.contains(Globals.workspaceEditDivBackup) ||
            	elemId.contains(Globals.editLinksDiv)
            ){
		            $(elemId).setStyle('display','block');
            };
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
        data: data
    };
    MessageHandler.sendMessage(message, Globals.applyIncomingMessages);
};
/* Receive message from server */
Globals.applyIncomingMessages = function (messages) {
    Array.each(messages, function (message, messageIndex) {
        // check for error
        if (Globals.chk(message.error)) {
            alert(message.error);
            return;
        };
        // check for style sheets
        if (Globals.chk(message.styleSheets)) {
            message.styleSheets.each(function (cssPath) {
                Asset.css(cssPath, null);
            });
        };
        // check for htmls
        if (Globals.chk(message.htmlContents)) {
            Globals.setHtmlContents(message.htmlContents);
        };
        // check for jscripts
        if (Globals.chk(message.jscript)) {
            Object.each(message.jscript, function (callback, file){
            	Globals.loadJS(file, eval(callback));
            });
        };        
    });
};
