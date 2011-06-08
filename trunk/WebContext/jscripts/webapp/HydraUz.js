/* Initial global setups */
if (HydraUz == null) {
    var HydraUz = { 
    	  'version': '0.0.1a'
    	, 'mainContent': 'content'
    	, 'mainContentTitle': 'title' 
    };
};

HydraUz.setMainMenuHeader = function(txt)
{
	$(HydraUz.mainContentTitle).innerHTML = txt;
};

HydraUz.mainMenu = function(header, content)
{
	HydraUz.setMainMenuHeader(header);
	$(HydraUz.mainContent).innerHTML = "...";
	content = '[[' + content + ']]';
		
	HydraUz.setContent(content, HydraUz.mainContent);
};

HydraUz.setContent = function(content, dest)
{
    Globals.sendMessage({
        handler: 'General'
        , action: 'getContent'
        , content: content
        , dest: dest
    });	
};

HydraUz.MorphVT = function(elemID){
	if($(elemID).getStyle('display') == 'none'){
		$(elemID).setStyle('display', 'block');
	}else{
		$(elemID).setStyle('display', 'none');
	}
};