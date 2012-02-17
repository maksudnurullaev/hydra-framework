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

SKTourUz._showImagesAlbum = function(sub_folders){
	window.open('/index.html?mode=zfile.uz', 'winname', 
	  directories=0,titlebar=0,toolbar=0,location=0,status=0,     
		menubar=0,scrollbars=no,resizable=no,
		  width=400,height=350);
};