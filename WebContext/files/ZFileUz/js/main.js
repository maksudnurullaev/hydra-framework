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
        handler: 'General'
        , action:  'getContent'
        , content: content
        , dest: ZFileUz.MainContent
    });        
};

ZFileUz.sendClientMessage = function(){
    if(!$('textarea.message') || !$('textarea.message').value.trim()){
        alert("Нет теста сообщения!");
        return;
    }
    if(!ZFileUz.testValue($('captcha_value').value, 'captcha_value')){
        return;
    }
    var text = $('textarea.message').value.trim();
    Globals.sendMessage({
        handler: 'ClientMessage'
        , action: 'add'
        , dest: ZFileUz.Content
        , text: text
        , captcha_value:$('captcha_value').value 
    });        
};

ZFileUz.testValue = function(val, name){
    if ($(name).setStyle) {
        $(name).setStyle('background', (val?'':'red'));
    } else if ($(name).style) {
    	$(name).style.background = (val?'':'red');    
    }
    return (val);
};

ZFileUz.try2SendFile = function(){
    var test1 = ZFileUz.testValue($('check_agreement').checked, 'li_check_agreement');
    var test2 = ZFileUz.testValue($('input_file').value, 'input_file');
    var test3 = ZFileUz.testValue($('captcha_value').value, 'captcha_value');
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
