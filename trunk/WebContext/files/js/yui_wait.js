YUI().use('panel', function (Y) {
	if(dwr.engine && typeof Globals.yuiPanelWait == "undefined")
	{
		var waitElement = '<img src="http://l.yimg.com/a/i/us/per/gr/gp/rel_interstitial_loading.gif">';
		Globals.yuiPanelWait = new Y.Panel({
			bodyContent: waitElement,
			modal  : true,
			centered : true
		});

		Globals.yuiPanelWait.render();
		Globals.yuiPanelWait.hide();
	}
	
	if(dwr.engine){
		dwr.engine.setPreHook(Globals.preHook);
		dwr.engine.setPostHook(Globals.postHook);
	};	
	
});
