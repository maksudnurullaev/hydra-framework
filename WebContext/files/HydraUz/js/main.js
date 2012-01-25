/* Initial global setups */
if (HydraUz == null) {
    var HydraUz = { 
    	  'version': '0.0.1a'
    	, 'mainContent': 'content'
    	, 'mainContentTitle': 'title' 
    };
};

HydraUz.mainMenu = function(header, content)
{
	HydraUz.setMainMenuHeader(header);
	$(HydraUz.mainContent).innerHTML = "...";
	content = '[[' + content + ']]';
		
	HydraUz.setContent(content, HydraUz.mainContent);
};

HydraUz.setMainMenuHeader = function(txt)
{
	$(HydraUz.mainContentTitle).innerHTML = txt;
};

HydraUz.setContent = function(content, dest){
    Globals.sendMessage({
        handler: 'General'
        , action: 'getContent'
        , content: content
        , dest: dest
    });	
};