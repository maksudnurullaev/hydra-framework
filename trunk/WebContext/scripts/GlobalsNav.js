if (GlobalsNav == null){
	var GlobalsNav = {'version': '0.0.1', ID:'navlist', SelectedIDVal:'selected', SelectedMenuID:'__nothing__'};
};

GlobalsNav.getList = function(){
	return $$(GlobalsNav.ID,"li");
};

GlobalsNav.onClik = function(elem) {
	return GlobalsNav.onClickCommon(elem);
};


GlobalsNav.isNonActiveMenu = function(selectedMenuElement){
	// Check ID
	if(!$chk(selectedMenuElement.id)){
		alert("ERROR: Navigator does not have neccessary ID property!\n\n" + selectedMenuElement.innerHTML);
		return false;
	}	
	
	// Check "selected" state
	if(selectedMenuElement.id == GlobalsNav.SelectedMenuID){ return false; }
		
	return true;
};

GlobalsNav.clearSelectedItem = function(){
	$each(GlobalsNav.getList(), function(elem, elemIndex){
		elem.id = null;
	});	
};

GlobalsNav.onClickCommon = function(selectedMenuElement){
	
	/* No action if element already selected */
	if(!GlobalsNav.isNonActiveMenu(selectedMenuElement)){ return false; }
	
	/* Change selection */
	GlobalsNav.clearSelectedItem();	
	selectedMenuElement.parentNode.id = GlobalsNav.SelectedIDVal;
	
	/* Save selected menu ID */
	GlobalsNav.SelectedMenuID = selectedMenuElement.id;	
	
	/* Move child elements to stack & clear context */
	Globals.clearContent(Globals.DivContentID, 'div');
	
	/* Get content */
	var requestDivId = selectedMenuElement.id + ".content";
	if(!$(requestDivId)){ /* From Servert */
		Globals.sendMessage({handler:'Message',
							 what:'html.content', 
			   				 kind:requestDivId,
			   				 dest:Globals.DivContentID});
	}else { /* From Stack */
		$(Globals.DivContentID).appendChild($(requestDivId));
	}	
	
	GlobalsNav.SelectedMenuID = selectedMenuElement.id;
	
	return false;
};