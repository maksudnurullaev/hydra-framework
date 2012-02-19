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

    jQuery('#topmenuul a').each(function(index, el){
        if(el.id == inEl.id){
            jQuery(el.parentNode).addClass('active');
        }else{
            jQuery(el.parentNode).removeClass('active');
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

SKTourUz.showImagesAlbum = function(sub_folders){
	if(!Globals.Y.isLoadedShowImagePanel){
		YUI().use('panel', function (Y) {
			Globals.Y = Y;
			Globals.Y.isLoadedShowImagePanel = true;
			SKTourUz._showImagesAlbum(sub_folders);
			return;
		});
	} else {
		SKTourUz._showImagesAlbum(sub_folders);
	}
};

SKTourUz.showImagesAlbum = function(sub_folders, dest){
    var content = '[[';
    content += ('Application|Images|' + sub_folders + '|lightbox_col4');
    content += ']]';
	
    Globals.sendMessage({
        handler: 'General'
        , action:  'getContent'
        , content: content
        , dest: dest
    });
};
