/* Initial global setups */
if (MaxmsUz == null) {
    var MaxmsUz = { 
    	'version': '0.0.1a',
    	'MainContent': 'contentwrap',
    	'Content': 'content'    	
    	};
};

MaxmsUz.setMainContent = function(contentId){
	var content = '';
	content += '<div id="content">';
	content += ' [[DB|Template|' + contentId + '.MainPage|span]]';
	content += '</div>';
	content += '<div id="sidebar">';
	content += ' [[DB|Template|' + contentId + '.SubMenu|span]]';
	content += '</div>';
	content += '<div style="clear: both;"></div>';
	
    Globals.sendMessage({
	    handler: 'General'
	    , action: 'getContent'
	    , content: content
	    , dest: MaxmsUz.MainContent
	});	
};

MaxmsUz.showCabinet = function(){
	var content = '';
	content += '<div id="content">';
	content += ' [[System|Login|form|NULL]]';
	content += '</div>';
	content += '<div id="sidebar">';
	content += ' [[DB|Template|Cabinet.SubMenu|span]]';
	content += '</div>';
	content += '<div style="clear: both;"></div>';
	
    Globals.sendMessage({
	    handler: 'General'
	    , action: 'getContent'
	    , content: content
	    , dest: MaxmsUz.MainContent
	});	
};