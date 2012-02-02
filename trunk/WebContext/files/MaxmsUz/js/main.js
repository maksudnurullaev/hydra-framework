/* Initial global setups */
if (MaxmsUz == null) {
    var MaxmsUz = { 
    	'version': '0.0.1a',
    	'MainContent': 'contentwrap',
    	'Content': 'content'    	
    	};
};

MaxmsUz.setMainContent = function(contentId){
	var content = '';
	content += '<div id="content">';
	content += ' [[DB|Template|TPage.' + contentId + '|span]]';
	content += '</div>';
	content += '<div id="sidebar">';
	content += ' [[DB|Template|TPage.' + contentId + '.SubMenu|span]]';
	content += '</div>';
	content += '<div style="clear: both;"></div>';
	
    Globals.sendMessage({
	    _handler: 'General'
	    , _action: 'getContent'
	    , _content: content
	    , dest: MaxmsUz.MainContent
	});	
};

MaxmsUz.setContent = function(contentId){
	var content = '[[DB|Template|' + contentId + '|span]]';
	
    Globals.sendMessage({
	    _handler: 'General'
	    , _action: 'getContent'
	    , _content: content
	    , dest: MaxmsUz.Content
	});	
};

MaxmsUz.sendClientMessage = function(){
	if(!$('textarea.message') || !$('textarea.message').value.trim()){
		alert("Нет теста сообщения!");
		return;
	}
	var text = $('textarea.message').value.trim();
    Globals.sendMessage({
        handler: 'ClientMessage'
        , _action: 'add'
        , dest: MaxmsUz.Content
        , _text: text
    });		
};

MaxmsUz.showCabinet = function(){
	var content = '[[DB|Template|TPage.Cabinet|span]]';
	
    Globals.sendMessage({
	    _handler: 'General'
	    , _action: 'getContent'
	    , _content: content
	    , dest: MaxmsUz.MainContent
	});	
};
