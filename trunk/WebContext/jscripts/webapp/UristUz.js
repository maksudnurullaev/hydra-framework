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
	content += '[[DB|Template|MainContentLeft.' + id + '|null]]';
	content += ' </div>';
	content += '</div>';
	content += '<div id="sideBar" class="sideBar">';
	content += ' <div class="sideBarItem">';
	content += '[[DB|Template|MainSideBar.' + id + '|null]]';
	content += ' </div>';
	content += '</div><br class="clear" />';
	
	return content;
};