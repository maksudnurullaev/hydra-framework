/* Initial */
if (Buh1Uz == null) {
    var Buh1Uz = { 
        'version': '0.0.1a',
        'MainContent': 'main_content'
        };
};

Buh1Uz.setMainContent = function(encodedContent){    
    if(!Globals.pageBusy){
        var content = '[[' + encodedContent + ']]';

        Globals.sendMessage({
            handler: 'General'
            , action:  'getContent'
            , content: content
            , dest: Buh1Uz.MainContent
        });
    }
};

Globals.onStartPage = function () {
    $('body').layout({
        north: {
            size:					"auto"
        ,	spacing_open:			0
        ,	closable:				false
        ,	resizable:				false
        }
        , south: {
            size:					"auto"
        ,	spacing_open:			0
        ,	closable:				false
        ,	resizable:				false
        }        
    });
};
