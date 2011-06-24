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


// SET GLOBAL HOOKS 
HydraUz.preHook = function(){
	if($('html.body.vmiddle')){
		$('html.body.vmiddle').fade('hide');
	}
};
HydraUz.postHook = function(){
	if($('html.body.vmiddle')){
		$('html.body.vmiddle').fade('show');
	}
};
if(dwr.engine){
	dwr.engine.setPreHook(HydraUz.preHook);
	dwr.engine.setPostHook(HydraUz.postHook);
};
