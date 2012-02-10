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
        };
};
/* Highlight them */
Globals.highlightFields = function(elementIDs){
    Object.each(elementIDs, function (id){
        if($(id) && $(id).setStyle){
            $(id).setStyle('border','1px solid red');
        }
        if($(id) && $(id).style){
            $(id).style.border = '1px solid red';
        }
    });
};
/* NO Highlight them */
Globals.noHighlightFields = function(elementIDs){
    Object.each(elementIDs, function (id){
        if($(id) && $(id).setStyle){
            $(id).setStyle('border','1px solid #7F9DB9');
        }
        if($(id) && $(id).style){
            $(id).style.border = '1px solid #7F9DB9';
        }
    });
};
/* toogle display */
Globals.toogleBlock = function(elemID){
    if($(elemID) && $(elemID).getStyle) {
        if($(elemID).getStyle('display') == 'none'){
            $(elemID).setStyle('display', 'block');
        }else{
            $(elemID).setStyle('display', 'none');
        }
        return;
    }
    if($(elemID) && $(elemID).style) {
        if($(elemID).style.display == 'none' || (!$(elemID).style.display)){ // fixed for IE
            $(elemID).style.display = 'block';
        }else{
            $(elemID).style.display = 'none';
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
       handler: handleName
       , action: actionName
       , key: divId
       , dest: Globals.editBox
    });
};
// load initial page
Globals.loadInitialPage = function(){
    // Get initial html body
    Globals.sendMessage({
        handler: 'General'
        , action: 'getInitialBody'
        , dest: 'body'
    });
};
// show/hide element
Globals.setVisibility = function(el, isVisible){
    if(!Globals.chk(el)){
        return;
    }
    if(Globals.chk(el.style)){ // fix for IE
        el.style.visibility = (isVisible?'visible':'hidden');
        return;
    }
    if(Globals.chk(el.fade)){
        el.fade((isVisible?'show':'hide'));
        return;
    }
    alert('Display function does not work!');
};

/* Set html content by contents map */
Globals.setHtmlContents = function (contentsMap) {
    Object.each(contentsMap, function (htmlContent, elemId) {
        if ($(elemId) && $(elemId).innerHTML != undefined && htmlContent) {
            if(htmlContent.search(/^close_me/i) >= 0){
                Globals.setVisibility($(elemId), false);
            }else{
                $(elemId).innerHTML = htmlContent;
                Globals.setVisibility($(elemId), true);
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
        handler: 'General'
        , action:  'getContent'
        , content: ('[[' + content + ']]')
        , dest: 'content'
    });        
};
/* set & restore wait element*/
Globals.setWaitElement = function (data){
    Globals.pageBusy = true;
    if(Globals.chk($('wait_element')) && (typeof $('wait_element').innerHTML != "undefined")){
        $('wait_element').innerHTML = Globals.loadingImage ;
    } else {
        if(Globals.chk(data.dest) && (typeof $(data.dest).innerHTML != "undefined")){
            $(data.dest).innerHTML = Globals.loadingImage ;
        }    
    }
};
Globals.restoreWaitElement = function(){
    if(Globals.chk($('wait_element')) && (typeof $('wait_element').innerHTML != "undefined")){
        $('wait_element').innerHTML = "&nbsp;";
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
        $('body').innerHTML = Globals.loadingImage ;
        window.location.reload();
        return;
    };
    // is it new session?
    if(Globals.chk(Globals.sessionID)){ 
        if(Globals.sessionID != message.sessionID){
            $('body').innerHTML = Globals.loadingImage ;
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



