if (GlobalsNav == null){
	var GlobalsNav = {'version': '0.0.1', selected:'__none___'};
};

GlobalsNav.onClick = function(elm){
	
	
	/* Change selection */
	if($("title")){
		$("title").innerHTML = elm.innerHTML;
	}
			
	/* Move child elements to stack & clear context */
	Globals.clearContent(Globals.DivContentID, 'div');
	
	/* Get content */
	var requestDivId = elm.id + ".content";
	if(!$(requestDivId)){ /* From Servert */
		Globals.sendMessage({handler:'Message',
							 action:'get_html_content', 
			   				 key:requestDivId,
			   				 dest:Globals.DivContentID});
	}else { /* From Stack */
		$(Globals.DivContentID).appendChild($(requestDivId));
	}	
	
	/* Save selected menu ID */
	GlobalsNav.selected = elm.id;	
	
	return false;
};