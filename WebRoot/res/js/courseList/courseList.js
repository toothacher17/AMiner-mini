function applyHiddenCourseDisplay(){
	jQuery("li").each(function(){
		var theList = jQuery(this).find(".p");
		var height = theList.height();
		var theShowButton = jQuery(this).find("#show");
		var theHideButton = jQuery(this).find("#hide");
		if(height>54){
			theList.css("max-height","54px");
			theList.css("overflow","hidden");
			theShowButton.hide();
			theHideButton.hide();
			theShowButton.show();
			theShowButton.click(function(){
				theList.css("max-height",height+20);
				theShowButton.hide();
				theHideButton.show();
			});
			theHideButton.click(function(){
				theHideButton.hide();
				theShowButton.show();
				theList.css("max-height","54px");
			});
		}else{
			theShowButton.hide();
			theHideButton.hide();
		}
	});
}