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
        handler: 'General',
        action:  'getContent',
        content: content,
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
        handler: 'ClientMessage'
        , action: 'add'
        , dest: ZFileUz.Content
        , text: text
    });		
};

ZFileUz.acceptAgreement = function(el){
	$('sending').disabled = (!el.checked);
};

ZFileUz.try2SendFile = function(){
	if(!$('input_file').value){
		$('input_file').setStyle('background','red');
		$('input_file').highlight('#ddf');
		return;
	}
	if(!$('captchaResult').value){
		$('captchaResult').setStyle('background','red');
		$('captchaResult').highlight('#ddf');
		return;
	}
	Globals.sendMessage2({handler:'UserFiles',action:'add', dest:ZFileUz.Content, tag:$('tagId').value}, dwr.util.getValue('input_file'));
	if($(ZFileUz.Content) && $(ZFileUz.Content).innerHTML){
		$(ZFileUz.Content).innerHTML = "Ждем ответа от сервера...";
	} 
};

// SET GLOBAL HOOKS 
ZFileUz.preHook = function(){
	if($('waiting')){
		$('waiting').fade('show');
	}
	if($('topmenu')){
		$('topmenu').fade('hide');
	}
	if($('contentcontainer')){
		$('contentcontainer').fade('hide');
	}
	
};
ZFileUz.postHook = function(){
	if($('waiting')){
		$('waiting').fade('hide');
	}
	if($('topmenu')){
		$('topmenu').fade('show');
	}
	if($('contentcontainer')){
		$('contentcontainer').fade('show');
	}
};
if(dwr.engine){
	dwr.engine.setPreHook(ZFileUz.preHook);
	dwr.engine.setPostHook(ZFileUz.postHook);
};