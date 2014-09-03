Tapestry.Initializer.suggestion = function(params) {
	var url = params.ajaxUrl.replace(/\$0025s/g, "%s");

	var timer = undefined;

	// var suggestColumnPattern = "<li><span>%s</span></li>";
	var suggestColumnPattern = "<tr class=\"ml\" index=\"%d\"><td>%s</td></tr>";

	var suggestLength = -1;
	var isSuggestionDisplayed = false;
	var currentIndex = -1;

	var lastSuggestQuery = "";
	
	jQuery(".fn_searchInput").on("keyup", function(event) {
//		console.log(event.keyCode);
		if (event.keyCode == 38) {
			if (currentIndex == -1) {
				currentIndex = suggestLength - 1;
			} else if (currentIndex > 0)
				currentIndex--;
			jQuery("#suggest_builder tr").removeClass("active");
			var $this = jQuery("#suggest_builder tr[index='" + currentIndex + "']");
			$this.addClass("active");
			jQuery(".fn_searchInput").val($this.text());
			// 判断不为最靠上一个,则将当前的上一个进行高亮
			return false;
		} else if (event.keyCode == 40) {
			// 判断不为最靠下一个,则将当前的下一个进行高亮
			if (currentIndex < suggestLength - 1)
				currentIndex++;
			jQuery("#suggest_builder tr").removeClass("active");
			var $this = jQuery("#suggest_builder tr[index='" + currentIndex + "']");
			$this.addClass("active");
			jQuery(".fn_searchInput").val($this.text());
			return false;
		} else if (event.keyCode == 13 || event.which == 13) {
			if(currentIndex == -1) {
//				jQuery(".fn_search_form").submit();
				return true;
			}
			jQuery(".fn_searchInput").val(jQuery("#suggest_builder tr[index='" + (currentIndex) + "']").text());
//			jQuery(".fn_search_form").submit();
			jQuery("#suggestion").hide();
			isSuggestionDisplayed = false;
			currentIndex = -1;
			event.preventDefault();
			return false;
		} else if (event.keyCode == 27) {
			jQuery("#suggestion").hide();
			isSuggestionDisplayed = false;
			currentIndex = -1;
			event.preventDefault();
			return false;
		} else {
			currentIndex = -1;
			var text = jQuery(this).val();
			if ("" == text) {
				if (undefined != timer) {
					clearTimeout(timer);
					timer = undefined;
				}
				jQuery("#suggestion").hide();
				isSuggestionDisplayed = false;
			} else {
				jQuery("#suggestion").css("display","table");
				isSuggestionDisplayed = true;
				if (undefined == timer) {
					displaySuggestion();
					// jQuery("#suggestion").html(event.keyCode + "<br/>" +
					// event.shiftKey);
					timer = setTimeout(displaySuggestion, 333);
				}
			}
		}

	});

	function displaySuggestion() {
		if (undefined != timer) {
			clearTimeout(timer);
			timer = undefined;
		} else {
			timer = setTimeout(displaySuggestion, 333);
		}
		var text = jQuery(".fn_searchInput").val();
		text = text.replace(/ /g, "__");
		if(lastSuggestQuery == text) {
			return;
		}
		lastSuggestQuery = text;
		if(text == "")
			return;
		jQuery.post(jQuery.sprintf(url, encodeURIComponentForTapestry(text)), function(json) {
			jQuery("#suggest_builder").html("");
			suggestLength = json.suggestionResultList.length;
			if(json.suggestionResultList.length != 0) {
				var suggestInnerHtml = "";
				for ( var i = 0; i < json.suggestionResultList.length; i++) {
					suggestInnerHtml += jQuery.sprintf(suggestColumnPattern, i, json.suggestionResultList[i]) + "\n";
				}
				jQuery("#suggest_builder").html(suggestInnerHtml);
				selectSuggestion();
			} else {
				jQuery("#suggestion").hide();
				isSuggestionDisplayed = false;
				currentIndex = -1;
			}
		}, "json");
	}
	function selectSuggestion() {
		// have already shown the suggestionList,select a certain one suggestion
		jQuery("#suggest_builder tr").bind("mouseover", function() {
			jQuery("#suggest_builder tr").removeClass("active");
			jQuery(this).addClass("active");
			currentIndex = jQuery(this).attr("index");
		});
		jQuery("#suggest_builder tr").bind("click", function() {
			jQuery(".fn_searchInput").val(jQuery(this).text());
			jQuery(".fn_search_form").submit();
			jQuery("#suggestion").hide();
			isSuggestionDisplayed = false;
			currentIndex = -1;
		});
	}
};


var VALID_T5_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890-_.:".split( "" ); 
function encodeURIComponentForTapestry(uri) { 
    if ( uri == null || uri.length == 0 ) { 
    return "$B"; 
    } 

    var result = ""; 
    for(var i = 0; i < uri.length; i++ ) { 
    var c = uri.charAt( i ); 
        if( VALID_T5_CHARS.indexOf( c ) > -1 ) { 
        result += c; 
        } 
        else { 
        result += "$" + uri.charCodeAt( i ).toString(16); 
        } 
    } 
    return result; 
} 
