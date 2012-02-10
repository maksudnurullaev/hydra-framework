/* Initial global setups */
if (SKTourUz == null) {
    var SKTourUz = { 
    	'version': '0.0.1a',
    	'topNav': 'topNav',
	'MainContent': 'main.content'
    	};
};

SKTourUz.setMainContent = function(inEl){    
    if(Globals.pageBusy){
        return;
    }
    $$('#topmenuul a').each(function(el){
         if(el.id == inEl.id){
            el.getParent().setProperty('class', 'active');
        }else{
            el.getParent().setProperty('class', '');
        }
    });  
    
    var content = '[[';
    content += ('DB|Template|Main.' + inEl.id + '.Page|span');
    content += ']]';

    Globals.sendMessage({
        handler: 'General'
        , action:  'getContent'
        , content: content
        , dest: SKTourUz.MainContent
    });        
};

