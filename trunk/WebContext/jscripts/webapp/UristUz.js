/* Initial global setups */
if (UristUz == null) {
    var UristUz = { 
    	'version': '0.0.1a',
    	'topNav': 'topNav'
    	};
};

UristUz.mainMenu = function(aElem){
	// switch menu
	$$('#topNav a').each(function (elem){
		if(aElem.id == elem.id){
			elem.addClass('topNavAct');
		}else{
			if(elem.hasClass('topNavAct')){
				elem.removeClass('topNavAct');
			}
		}
	});	
	// update content
    Globals.sendMessage({
        handler: 'General',
        action:  'getContent',
        content: UristUz.mainContent(aElem.id),
        dest:    'content'
    });
    
};

UristUz.mainContent = function(id){
	var content = 
			   '<div class="content_1" id="content_1">';
	content += ' <div class="content_1_left" id="content_1_left">';
	content += '[[DB|Template|Main.' + id + '.Page|null]]';
	content += ' </div>';
	content += '</div>';
	content += '<div id="sideBar" class="sideBar">';
	content += ' <div class="sideBarItem">';
	content += '[[DB|Template|Main.' + id + '.Page.Menu|null]]';
	content += ' </div>';
	content += '</div><br class="clear" />';
	
	return content;
};

// SET GLOBAL HOOKS 
UristUz.preHook = function(){
	if($('branding')){
		$('branding').fade('hide');
	}
	if($('content')){
		$('content').fade('hide');
	}
};

UristUz.postHook = function(){
	if($('branding')){
		$('branding').fade('show');
	}
	if($('content')){
		$('content').fade('show');
	}	
};

if(dwr.engine){
	dwr.engine.setPreHook(UristUz.preHook);
	dwr.engine.setPostHook(UristUz.postHook);
};