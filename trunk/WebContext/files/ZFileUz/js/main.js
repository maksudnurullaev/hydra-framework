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
    jQuery('#topmenuul a').each(function(index, el){
         if(el.id == inEl.id){
            jQuery(el).addClass('highlight');
        }else{
            jQuery(el).removeClass('highlight');
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
	var node = jQuery('#textarea_message');
	var captcha_value = jQuery('#captcha_value');
    var test1 = Globals.setErrorClass(node.prop('value'), 'textarea_message');
    var test2 = Globals.setErrorClass(captcha_value.prop('value'), 'captcha_value');
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
    var test1 = Globals.setErrorClass(jQuery('#check_agreement').prop('checked'), 'li_check_agreement');
    var test2 = Globals.setErrorClass(jQuery('#input_file').prop('value'), 'input_file');
    var test3 = Globals.setErrorClass(jQuery('#captcha_value').prop('value'), 'captcha_value');
    if( test1 && test2 && test3 )
    {
        Globals.sendMessage(
            {handler:'UserFiles'
                , action:'add'
                , dest:ZFileUz.Content
                , folder:'files'
                , captcha_value:jQuery('#captcha_value').prop('value') 
                , Tag:jQuery('#tagId').prop('value')
                , Text:jQuery('#fileDescription').prop('value') 
                , Public:(jQuery('#publicCheckbox').prop('checked')?'true':'false')
                , Name: jQuery('#input_file').prop('value')
            }
            , jQuery('#input_file')[0]);
    }
};
