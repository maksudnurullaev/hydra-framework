/* Initial global setups */
if (SKTourUz == null) {
    var SKTourUz = { 
        'version': '0.0.1a',
        'topNav': 'topNav',
        'MainContent': 'main_content'
        };
};

SKTourUz.setMainContent = function(inEl){    
    if(Globals.pageBusy){
        return;
    }

    Globals.Y.all('#topmenuul a').each(function(el){
        if(el.generateID() == inEl.id){
             el.get('parentNode').addClass('active');
        }else{
            el.get('parentNode').removeClass('active');
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

