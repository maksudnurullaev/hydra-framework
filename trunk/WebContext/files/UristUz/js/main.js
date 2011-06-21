/* Initial global setups */
if (UristUz == null) {
    var UristUz = { 
    	'version': '0.0.1a',
    	'MainContent': 'MainContent'
    	};
};

UristUz.showUserCabinet = function(){
	var content = '<div class="portfolioColumnTwo">';
	content += '<h2>Персональный Кабинет</h2>';
    content += ' <p class="nothingSpecial">';
    content += '  [[System|Login|form|NULL]]';
    content += ' </p>';
    content += ' <p class="nothingSpecial">[[DB|NonUserTemplate|Lawers.Invitation|span]]</p>';

    Globals.sendMessage({
        handler: 'General',
        action:  'getContent',
        content: content,
        dest:    UristUz.MainContent
    });	
};

UristUz.showMainPage = function(){
    Globals.sendMessage({
        handler: 'General',
        action:  'getContent',
        content: '[[DB|Template|Content.Home|span]]',
        dest:    UristUz.MainContent
    });	
};

UristUz.showContactsPage = function(){
    Globals.sendMessage({
        handler: 'General',
        action:  'getContent',
        content: '[[DB|Template|Contacts.Page|span]]',
        dest:    UristUz.MainContent
    });	
};

UristUz.showPublicOfferPage = function(){
    Globals.sendMessage({
        handler: 'General',
        action:  'getContent',
        content: '[[DB|Template|PublicOffer.Page|span]]',
        dest:    UristUz.MainContent
    });	
};