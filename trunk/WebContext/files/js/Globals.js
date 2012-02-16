/* Initial global setups */
if (Globals == null) {
    var Globals = { 
            'version': '0.0.3a'
            ,'tempTextAreaID': 'tempTextAreaID.'
            ,'editBox': 'editBox'
            ,'editLinks': 'editLinks'        
            ,'tryToLoadCount': 0
            ,'pageBusy': false
            ,'loadingImage': '<img src="http://l.yimg.com/a/i/us/per/gr/gp/rel_interstitial_loading.gif">'
            ,'Y': null
        };
};
/* Highlight them */
Globals.highlightFields = function(elementIDs){
    Globals.Y.Array.each(elementIDs, function (id){
        Globals.setErrorClass(false, id);
    });
};
Globals.setErrorClass = function(val, id){
    var node = Globals.Y.one('#' + id);
    if(val){
        node.removeClass('error');
    } else {
        node.addClass('error');
    }
    return (val);
};
/* NO Highlight them */
Globals.noHighlightFields = function(elementIDs){
    Globals.Y.Array.each(elementIDs, function (id){
		var node = Globals.Y.one('#' + id);
		node.removeClass('error');
    });
};
/* toogle display */
Globals.toogleBlock = function(elemId){
    var node = Globals.Y.one('#' + elemId);
    if(!node) return;
    if(node.getStyle('display') == 'none'){
		Globals.showNode(node, true);
	} else {
		Globals.showNode(node, false);
	}
};
/* toogle block highlight */ 
Globals.toogleVisibility = function(elemId){
    var node = Globals.Y.one('#' + elemId);
    if(!node) return;
    if(node.getStyle('visibility') == 'visible'){
		node.setStyle('visibility', 'hidden');
	} else {
		node.setStyle('visibility', 'visible');
	}
};
/* clear edit area */
Globals.clearEditArea = function(){
    var node = Globals.Y.one('#' + Globals.editBox);
	if(node){
		node.setContent("&nbsp;");
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
// load initial page
Globals.loadInitialPage = function(){
    YUI().use('node', function (Y) {
        Globals.Y = Y;
        // Get initial html body
        Globals.sendMessage({
            handler: 'General'
            , action: 'getInitialBody'
            , dest: 'body'
        });
    });
};
Globals.showNode = function(node, isVisible){
	if(isVisible){
		node.setStyle('visibility', 'visible');
		node.setStyle('display', 'block');
	} else {
		node.setStyle('visibility', 'hidden');
		node.setStyle('display', 'none');
	}
};
/* Get proper destanation node */
Globals.getDestNode = function(elemId){
    var node = Globals.Y.one('#' + elemId + "_placeholder");
    if(node){
        return(node);
    }
    return(Globals.Y.one('#' + elemId));
};
/* Set html content by contents map */
Globals.setHtmlContents = function (contentsMap) {
    if(!contentsMap){
        return;
    }
    for(var elemId in contentsMap){
        var htmlContent = contentsMap[elemId];
        var node = Globals.getDestNode(elemId);
        if (node && node.setContent != "undefined" && htmlContent) {
            if(htmlContent.search(/^close_me/i) >= 0){
				Globals.showNode(node, false);
            }else{
                node.setContent(htmlContent);
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
/* set & restore wait element*/
Globals.setWaitElement = function (data){
    Globals.pageBusy = true;
	var node = Globals.Y.one('#' + 'wait_element');
	var destNode = Globals.getDestNode(data.dest);
    if(node && node.setContent){
        node.setContent(Globals.loadingImage);
    } else {
        if(destNode && destNode.setContent){
			destNode.setContent(Globals.loadingImage);
        }    
    }
};
Globals.restoreWaitElement = function(){
	var node = Globals.Y.one('#' + 'wait_element');
    if(node && node.setContent){
        node.setContent("&nbsp;");
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
    if(!confirm("Confirm action!\nПодтвердите действие!"))return;
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
        Globals.Y.one("#body_placeholder").setContent(loadingImage) ;
        window.location.reload();
        return;
    };
    // is it new session?
    if(Globals.chk(Globals.sessionID)){ 
        if(Globals.sessionID != message.sessionID){
            Globals.Y.one("#body_placeholder").setContent(Globals.loadingImage) ;
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



