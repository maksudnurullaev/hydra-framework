/* hk global setups */
if (Globals == null) {
    var Globals = { 
            'version': '0.0.3a'
            ,'tempTextAreaID': 'tempTextAreaID.'
            ,'editBox': 'editBox'
            ,'editLinks': 'editLinks'        
            ,'tryToLoadCount': 0
            ,'pageBusy': false
            ,'loadingImage': '<img src="http://l.yimg.com/a/i/us/per/gr/gp/rel_interstitial_loading.gif">'
            ,'mobile': false
			,'mobile_init': false
        };
};
/* Highlight them */
Globals.highlightFields = function(elementIDs){
    jQuery.each(elementIDs, function (index, id){
        Globals.setErrorClass(false, id);
    });
};
Globals.setErrorClass = function(val, id){
    var node = jQuery('#' + id);
    if(val){
        node.removeClass('error');
    } else {
        node.addClass('error');
    }
    return (val);
};
/* NO Highlight them */
Globals.noHighlightFields = function(elementIDs){
    jQuery.each(elementIDs, function (index, id){
		var node = jQuery('#' + id);
		node.removeClass('error');
    });
};
/* toogle display */
Globals.toogleBlock = function(elemId){
    var node = jQuery('#' + elemId);
    if(!node) return;
    if(node.css('display') == 'none'){
		Globals.showNode(node, true);
	} else {
		Globals.showNode(node, false);
	}
};
/* toogle block highlight */ 
Globals.toogleVisibility = function(elemId){
    var node = jQuery('#' + elemId);
    if(!node) return;
    if(node.css('visibility') == 'visible'){
		node.css('visibility', 'hidden');
	} else {
		node.css('visibility', 'visible');
	}
};
/* clear edit area */
Globals.clearEditArea = function(){
    var node = jQuery('#' + Globals.editBox);
	if(node){
		node.html("&nbsp;");
		Globals.showNode(node, false);
	}
};
/* Edit online */
Globals.editIt = function(divId, handleName, actionName){        
    // Get initial html body
    Globals.sendMessage({
       handler: handleName
       , action: actionName
       , key: divId
       , dest: Globals.editBox
    });
};
Globals.showNode = function(node, isVisible){
	if(isVisible && (node.css('visibility') != 'visible' || node.css('display')) != 'block'){
		node.css('visibility', 'visible');
		node.css('display', 'block');
		return;
	} 
	if(!isVisible && (node.css('visibility') != 'hidden'  || node.css('display')) != 'none') {
		node.css('visibility', 'hidden');
		node.css('display', 'none');
	}
};

Globals.loadInitialPage = function($) {
	Globals.sendMessage({
		handler: 'General'
		, action: 'getInitialBody'
		, dest: 'body'
	});
};
/* Set html content by contents map */
Globals.setHtmlContents = function (contentsMap) {
    if(!contentsMap){
        return;
    }	
    for(var elemId in contentsMap){
        var htmlContent = contentsMap[elemId];
        var node = Globals.getPlaceholderOrNode(elemId);
        if (node.length && htmlContent) {
            if(htmlContent.search(/^close_me/i) >= 0){
				Globals.showNode(node, false);
            }else{
                node.html(htmlContent);
				Globals.showNode(node, true);
            }
        };        
    }
};
/* Test DOM object existance */
Globals.chk = function (obj) {
    return !!(obj || obj === 0);
};
/* decode & content from from server */
Globals.decodeContent = function(content){
    Globals.sendMessage({
        handler: 'General'
        , action:  'getContent'
        , content: ('[[' + content + ']]')
        , dest: 'content'
    });        
};
/* get element placeholder or real element by id */
Globals.getPlaceholderOrNode = function(elemId){
	var node = jQuery('#' + elemId + '_placeholder');
	if(!node.length){
		node = jQuery('#' + elemId);
	}
	return(node);
};
/* set & restore wait element*/
Globals.setWaitElement = function (data){
    Globals.pageBusy = true;
	var node = Globals.getPlaceholderOrNode('wait_element');
    if(node.length){
        node.html(Globals.loadingImage);
    } else {
		node = Globals.getPlaceholderOrNode(data.dest);
		if(node.length){
			node.html(Globals.loadingImage);
        }    
    }
};
Globals.restoreWaitElement = function(){
	var node = jQuery('#' + 'wait_element');
    if(node.length){
        node.html("&nbsp;");
     }
     Globals.pageBusy = false;
};

/* Send message to server*/
Globals.sendMessage = function (data, file) {
    Globals.setWaitElement(data);
    var message = {
        data: data,
        url: document.URL
    };
    MessageHandler.sendMessage(message, file, Globals.applyIncomingMessages);
};
Globals.confirmAndSendMessage = function (data) {
    if(!confirm("Are you sure?"))return;
    Globals.sendMessage(data);
};
/* Receive message from server */
Globals.applyIncomingMessages = function (messages) {
    if(Globals.chk(messages.length)){
        for(var i = 0 ; i < messages.length ; i++){
            Globals.porcessMessage(messages[i]);
        }
    } else {
        Globals.porcessMessage(messages);
    }
    Globals.restoreWaitElement();
};
Globals.porcessMessage = function (message) {
    // check for error
    if (Globals.chk(message.error)) {
        alert(message.error);
    };  
    // check to reload page
    if(Globals.chk(message.reloadPage)){
        window.location.reload();
        return;
    };
    // is it new session?
    if(Globals.chk(Globals.sessionID)){ 
        if(Globals.sessionID != message.sessionID){
			var body = Globals.getPlaceholderOrNode('body');
			body.html(Globals.loadingImage);
            window.location.reload();
        }
    } else {
        Globals.sessionID = message.sessionID ;
    }
    
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
};
Globals.htmlEscape = function (str) {
    return String(str)
            .replace(/&/g, '&amp;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
};




