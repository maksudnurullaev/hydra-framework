/* Initial global setups */
if (SKTourUz == null) {
    var SKTourUz = { 
        'version': '0.0.1a',
        'topNav': 'topNav',
        'MainContent': 'main_content'
        };
};

SKTourUz.setMainContent = function(aEl){    
    if(Globals.pageBusy){
        return;
    }

    jQuery('#topmenuul a').each(function(index, el){
        if(el.id == aEl.id){
            jQuery(el.parentNode).addClass('active');
        }else{
            jQuery(el.parentNode).removeClass('active');
        }
    });  
    
    var content = '[[';
    content += ('DB|Template|Main.' + aEl.id + '.Page|span');
    content += ']]';

    Globals.sendMessage({
        handler: 'General'
        , action:  'getContent'
        , content: content
        , dest: SKTourUz.MainContent
    });
};

SKTourUz.setContent = function(aEl, contentHolderId){    
    if(Globals.pageBusy){
        return;
    }

    var content = '[[';
    content += ('DB|Template|Content.' + aEl.id + '.Page|span');
    content += ']]';

    Globals.sendMessage({
        handler: 'General'
        , action:  'getContent'
        , content: content
        , dest: contentHolderId
    });
};