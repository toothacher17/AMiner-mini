function applyHiddenCourseDisplay(){
	jQuery("li").each(function(){
		var theList = jQuery(this).find(".p");
		var height = theList.height();
		console.log("the height is" + height);
		var theShowButton = jQuery(this).find("#show");
		var theHideButton = jQuery(this).find("#hide");
		
		var theCourseList = jQuery(this).find(".sapn4");
		var theHeight = theCourseList.height();
		console.log("theHeight is" + theHeight);
		var theOutButton = jQuery(this).find("#out"); 
		var theInButton = jQuery(this).find("#in");
		
		if(height>120){
			theList.css("max-height","120px");
			theList.css("overflow","hidden");
			theShowButton.hide();
			theHideButton.hide();
			theShowButton.show();
			theShowButton.click(function(){
				theList.css("max-height",height);
				theShowButton.hide();
				theHideButton.show();
			});
			theHideButton.click(function(){
				theHideButton.hide();
				theShowButton.show();
				theList.css("max-height","120px");
			});
		}else{
			theShowButton.hide();
			theHideButton.hide();
		}
	
//		if(theHeight>100){
			theCourseList.css("max-height","0px");
			theCourseList.css("overflow","hidden");
			theOutButton.hide();
			theInButton.hide();
			theOutButton.show();
			theOutButton.click(function(){
				theCourseList.css("max-height",theHeight);
				theOutButton.hide();
				theInButton.show();
			});
			theInButton.click(function(){
				theInButton.hide();
				theOutButton.show();
				theCourseList.css("max-height","0px");
			});
//		}else{
//			theOutButton.hide();
//			theInButton.hide();
//		}
			
			
			
			
		
		
		
	});
}