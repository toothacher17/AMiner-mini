jQuery(function(){
	jQuery(".fulltext-display").each(function(){
		var $section = jQuery(this).parents(".infoComponentZone").find(".section");
		var $pDiv = $section.find("p");
		if($pDiv.height()>=150) {
			$section.css("max-height", "140px");
			jQuery(this).show();
		}
		
	});
});

function displayAllText(obj) {
	var $section = jQuery(obj).parents(".infoComponentZone").find(".section");
	$section.css("max-height","");
	jQuery(obj).hide();
}