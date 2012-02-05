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
	if(Globals.pageBusy) return;
	$$('#topmenuul a').each(function(el){
 		if(el.id == inEl.id){
    		el.setProperty('class', 'highlight');
    	}else{
    		el.setProperty('class', '');
		}
	});  
	
	var content = '[[';
	content += ('DB|Template|' + inEl.id + '.Page|span');
	content += ']]';

    Globals.sendMessage({
        _handler: 'General',
        _action:  'getContent',
        _content: content,
        dest: ZFileUz.MainContent
    });		
};

ZFileUz.sendClientMessage = function(){
	if(!$('textarea.message') || !$('textarea.message').value.trim()){
		alert("Нет теста сообщения!");
		return;
	}
	var text = $('textarea.message').value.trim();
    Globals.sendMessage({
        _handler: 'ClientMessage'
        , _action: 'add'
        , dest: ZFileUz.Content
        , text: text
    });		
};

ZFileUz.testValue = function(val, name){
	if(!val){
		$(name).setStyle('background','red');
		return false;
	}
	$(name).setStyle('background','');	
	return true;
};

ZFileUz.try2SendFile = function(){
	var test1 = ZFileUz.testValue($('checkAgreement').checked, 'licheckAgreement');
	var test2 = ZFileUz.testValue($('input_file').value, 'input_file');
	var test3 = ZFileUz.testValue($('CaptchaValue').value, 'CaptchaValue');
	if( test1 && test2 && test3 )
	{
		Globals.sendMessage2(
			{_handler:'UserFiles'
				, _action:'add'
				, dest:ZFileUz.Content
				, CaptchaValue:$('CaptchaValue').value 
				, Tag:$('tagId').value
				, Text:$('fileDescription').value 
				, Public:($('publicCheckbox').checked?'true':'false')
				, Name: dwr.util.getValue('input_file').value
			}
			, dwr.util.getValue('input_file'));
	}
};