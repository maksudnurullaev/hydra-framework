/* Initial global setups */
if (BlondinkaUz == null) {
    var BlondinkaUz = { 
        'version': '0.0.1a' ,
		'MainContent': 'main_content'
        };
};

BlondinkaUz.setMainContent = function(aEl){    
    if(Globals.pageBusy){
        return;
    }
    
    var content = '[[';
    content += ('DB|Template|Main.' + aEl.id + '.Page|span');
    content += ']]';

    Globals.sendMessage({
        handler: 'General'
        , action:  'getContent'
        , content: content
        , dest: BlondinkaUz.MainContent
    });
};
