/* Initial global setups */
if (HydraUz == null) {
    var HydraUz = { 
    	  'version': '0.0.1a'
    	, 'mainContent': 'content'
    	, 'mainContentTitle': 'title' 
    };
};

HydraUz.onMainMenu = function(el)
{
	var id = el.id;
	var contentKey = '[[DB|Text|Main.Menu.Content.' + id + '|locale]]';
	$(HydraUz.mainContentTitle).innerHTML = el.innerHTML;
	$(HydraUz.mainContent).fade('out');
	
    Globals.sendMessage({
        handler: 'General'
        , action: 'getContent'
        , key: contentKey
        , dest: HydraUz.mainContent
    });	
};