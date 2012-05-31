/* Initial */
if (TestUz == null) {
    var TestUz = { 
        'version': '0.0.1a',
        'topNav': 'topNav',
        'MainContent': 'main_content'
        };
	TestUz.data = {
		'tyag': [ 'TGS 19.360 /4х2 /72W E-4', 'TGS 19.390 /4х2 /72W', 'TGS 19.400 /4х2 /72W', 'TGS 26.400 /6x4 /78W', 'TGS 26.400 /6x4 /78W/ ADR', 'CLA 18/280 Е-3' ]
		, 'tyag_chassis': ['TGS 33.360 /6x2 /76W/ Е-2', 'CLA 26.280 /6x4 /CS22']
		, 'polu': ['Тентовый полуприцеп с бортом', 'Бортовой полуприцеп', 'Полуприцеп Хлопковоз', 'Полуприцеп рефрижератор', 'Изотермический полуприцеп', 'Самосвальный полуприцеп (СТ 52)', 'Самосвальный полуприцеп (Hardox)'
			, 'Полуприцеп битумовоз'
			, 'Низкорамный полуприцеп тяжеловоз (45 т)'
			, 'Низкорамный полуприцеп тяжеловоз (60 т)'
			, 'Низкорамный полуприцеп тяжеловоз (70 т)'
			, 'Полуприцеп цементовоз'
			, 'Полуприцеп бензовоз'
			, 'Полуприцеп цистерна для кислоты'
			, 'Самосвальный полуприцеп (алюминевый) (для перевозки химикатов)'
			, 'Полуприцеп контейнеровоз'
			, 'Автовоз' ]
		, 'spec': [	'Автокран СLA 26.280 CS22 (16 т)'
			, 'Автокран СLA 26.280 CS22 (25 т)'
			, 'Каналопромывочная машина СLA 26.280 CS22'
			, 'Пожарная машина СLA 16.220 CS03'
			, 'КДМ СLA 16.220 CS03'
			, 'Мусоровоз СLA 16.220 CS03'
			, 'Автогудронатор СLA 16.220 CS03'
			, 'Мобильная автомастерская СLA 16.220 CS03'
			, 'Комплексная Мобильная лаборатория СLA 16.220 CS03'
			, 'Бортовые фургоны (тент) СLA 16.220 CS03'
			, 'Бортовые фургоны (тент), CLA 26-280 6x4 CS06'
			, 'Автотопливозаправщик, CLA 26-280 6x4 CS06'
			, 'Бетономешалка СLA 26.280 CS13'
			, 'Бортовые-Фургоны TGM/TGS (4x4, 6x6)'
			, 'Битумовоз TGM'
			, 'Изотермический фургон, CLA 26-280 6x4 CS06'
			, 'Самосвал TGS 33.360 Е-2 (16 м3)'
			, 'Самосвал СLA 26.280 Е-3 (13 м3'		
		]
	};
};

TestUz.try2FindIfDotted = function(){
	var seek_string = $.trim($('#search_field').val());
	if( seek_string && seek_string.indexOf('...') >= 0 ){
		seek_string = seek_string.replace('...', '');
		$('#search_field').val(seek_string);		
		TestUz.try2Find();
	}
};

TestUz.try2Find = function(){
	var seek_string = $.trim($('#search_field').val());
	if(seek_string){
		Globals.sendMessage(
			{handler:'UserFiles'
				, action: "searchTxtDbFile"
				, dest:   "seek_result"
				, folder: "files"
				, file_name: "MAN_DATA.txt"
				, seek_string: seek_string
			});
	} else {
		$('#seek_result').html('...');
	}
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
	TestUz.init_form1();
};

TestUz.init_form1  = function(){    	
	// Checkbox
	$(function() {
		$( "#check" ).button();
	});	
	
	$( "#check" ).click(function(){
		$("#check_label1 span").text(this.checked?"Вкл.":"Выкл.");
	});

	
	// Select auto type
	$( "#select0" ).change(function(){
		if($( "#select0" ).val() == 'select'){
			TestUz.init_form1.setup_select01(false);
		} else {
			TestUz.init_form1.setup_select01($( "#select0" ).val());
		}
		// Setup chassis
		TestUz.init_form1.setup_select02($( "#select0" ).val());
	});
};

TestUz.init_form1.setup_select02 = function(v){
	TestUz.set_first_select_option('#select02');
	if(v != 'select' && v != 'polu'){
		// set enable anyway
		if(Globals.chk($.mobile)){
			$( "#select02" ).selectmenu('enable');	
		}else{
			$("#select02").removeAttr('disabled');	
		}	
	}else{
		if(Globals.chk($.mobile)){
			$( "#select02" ).selectmenu('disable');	
		}else{
			$("#select02").attr('disabled', '');		
		}	
	}
};

TestUz.set_first_select_option = function(v){
	$( v ).val($( v +" option:first").val());
	if(Globals.chk($.mobile)){
		$( v ).selectmenu("refresh");
	};
};

TestUz.init_form1.setup_select01 = function(v){
	TestUz.set_first_select_option('#select01');
	// clear items except
	while($('#select01 option').size() > 1){
		$( "#select01 option:last").remove();
	}
	// check for disable
	if((!v) || (!TestUz.data[v])){
		if(Globals.chk($.mobile)){
			$( "#select01" ).selectmenu('disable');	
		}else{
			$("#select01").attr('disabled', '');		
		}
		return;
	} else if(TestUz.data[v]){
		$.each(TestUz.data[v], function(i, l){
			$('#select01').append('<option value="' + l + '">' + l +'</option>');
		});
	}
	// set enable anyway
	if(Globals.chk($.mobile)){
		$( "#select01" ).selectmenu('enable');
	}else{
		$("#select01").removeAttr('disabled');	
	}
};
