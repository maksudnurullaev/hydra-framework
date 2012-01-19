/* Initial global setups */
if (Globals == null) {
    var Globals = { 
    		'version': '0.0.3a'
    		,'tempTextAreaID': 'tempTextAreaID.'
    		,'editBox': 'editBox'
    		,'editLinks': 'editLinks'    	
    		,'sessionID': 'unknown'	
    		,'tryToLoadCount': 0
			,'pageBusy': false
			,'loadingImage': '<img src="http://l.yimg.com/a/i/us/per/gr/gp/rel_interstitial_loading.gif">'
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
/* Mootools fade toogle */
Globals.toggle = function(elemID){
	if($(elemID)){
		$(elemID).fade('toggle');
	}
};
Globals.toogleBlock = function(elemID){
	if($(elemID) && $(elemID).getStyle) {
		if($(elemID).getStyle('display') == 'none'){
			$(elemID).setStyle('display', 'block');
		}else{
			$(elemID).setStyle('display', 'none');
		}
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
    	&& Globals.isLoadedStage13()
		&& Globals.isLoadedStage14();
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
Globals.isLoadedStage14 = function () {
    return typeof Globals.yuiPanelWait != "undefined";
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
/* decode & content from from server */
Globals.decodeContent = function(content){
    Globals.sendMessage({
        handler: 'General',
        action:  'getContent',
        content: ('[[' + content + ']]'),
        dest: 'content'
    });		
};
/* Send message to server*/
Globals.sendMessage = function (data) {
	if(Globals.chk(data.dest) && ($(data.dest).innerHTML != undefined)){
		$(data.dest).innerHTML = Globals.loadingImage ;
	}
    var message = {
        data: data,
        sessionID: Globals.sessionID,
        url: document.URL
    };
    MessageHandler.sendMessage(message, Globals.applyIncomingMessages);
};
/* Send message to server*/
Globals.sendMessage2 = function (data, file) {
	if(Globals.chk(data.dest) && ($(data.dest).innerHTML != undefined)){
		$(data.dest).innerHTML = Globals.loadingImage ;
	}
    var message = {
        data: data,
        sessionID: Globals.sessionID,
        url: document.URL
    };
    MessageHandler.sendMessage2(message, file, Globals.applyIncomingMessages);
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
		// check to reload page
		if(Globals.chk(message.reloadPage)){
			window.location.reload();
			return;
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
    			alert("ERROR: Session expired!");
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
     
        // check for page title
        if (Globals.chk(message.title)) {
        	if(document && document.title){
        		document.title = message.title;
        	}
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


