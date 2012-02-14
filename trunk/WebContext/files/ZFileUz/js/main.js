/* Initial global setups */
if ( ZFileUz == null ) {
    var ZFileUz = { 
        'version': '0.0.1a',
        'MainContent': 'contentcontainer',
        'Content': 'content',
        'currentTopMenuId': 'Home'
        };
};

ZFileUz.setMainContent = function(inEl){    
    if(Globals.pageBusy){
        return;
    }
    Globals.Y.all('#topmenuul a').each(function(el){
         if(el.generateID() == inEl.id){
            el.addClass('highlight');
        }else{
            el.removeClass('highlight');
        }
    });  
    
    var content = '[[';
    content += ('DB|Template|' + inEl.id + '.Page|span');
    content += ']]';

    Globals.sendMessage({
        handler: 'General'
        , action:  'getContent'
        , content: content
        , dest: ZFileUz.MainContent
    });        
};

ZFileUz.sendClientMessage = function(){
	var node = Globals.Y.one('#textarea_message');
	var captcha_value = Globals.Y.one('#captcha_value');
    var test1 = Globals.setErrorClass(node.get('value'), 'textarea_message');
    var test2 = Globals.setErrorClass(captcha_value.get('value'), 'captcha_value');
	if(test1 && test2){
		Globals.sendMessage({
			handler: 'ClientMessage'
			, action: 'add'
			, dest: ZFileUz.Content
			, text: node.get('value')
			, captcha_value: captcha_value.get('value')
		});        
	}
};

ZFileUz.try2SendFile = function(){
    var test1 = Globals.setErrorClass($('check_agreement').checked, 'li_check_agreement');
    var test2 = Globals.setErrorClass($('input_file').value, 'input_file');
    var test3 = Globals.setErrorClass($('captcha_value').value, 'captcha_value');
    if( test1 && test2 && test3 )
    {
        Globals.sendMessage(
            {handler:'UserFiles'
                , action:'add'
                , dest:ZFileUz.Content
                , folder:'files'
                , captcha_value:$('captcha_value').value 
                , Tag:$('tagId').value
                , Text:$('fileDescription').value 
                , Public:($('publicCheckbox').checked?'true':'false')
                , Name: dwr.util.getValue('input_file').value
            }
            , dwr.util.getValue('input_file'));
    }
};
