/* Initial */
if (TestUz == null) {
    var TestUz = { 
        'version': '0.0.1a',
        'topNav': 'topNav',
        'MainContent': 'main_content'
        };
};

TestUz.initStartPage = function(){    
	// Dialog
	$('#dialog').dialog({
		autoOpen: false,
		width: 600,
		buttons: {
			"Ok": function() {
				$(this).dialog("close");
			},
			"Cancel": function() {
				$(this).dialog("close");
			}
		}
	});

	// Dialog Link
	$('#dialog_link').click(function(){
		$('#dialog').dialog('open');
		return false;
	});
	
	// Tabs
	$(function() {
		$( "#tabs" ).tabs();
	});	
	// Radio
	$(function() {
		$( "#radio" ).buttonset();
	});	
	
	// Slider
	$(function() {
		$( "#slider" ).slider({
			value:5000,
			min: 0,
			max: 50000,
			step: 1000,
			slide: function( event, ui ) {
				$( "#amount" ).val( "$" + ui.value );
			}
		});
		$( "#amount" ).val( "$" + $( "#slider" ).slider( "value" ) );
	});
	
	// Checkbox
	$(function() {
		$( "#check" ).button();
	});	
	
	$( "#check" ).click(function(){
		$("#check_label1 span").text(this.checked?"Вкл.":"Выкл.");
	});
	
};
