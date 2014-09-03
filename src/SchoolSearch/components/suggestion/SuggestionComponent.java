package SchoolSearch.components.suggestion;

import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import SchoolSearch.pages.search.SearchIndex;
import SchoolSearch.services.services.suggestion.SuggestionService;

@Import (
	stylesheet = {"context:/res/css/home.css", "context:/res/css/pages/suggest.css"},	//
	library = {"context:/res/js/suggestion/suggestion.js"}
)

public class SuggestionComponent {
	@Parameter(allowNull = false)
	String suggestionType;
	
	@Property 
	String query;
	
//	@InjectPage
//	SearchIndex searchPage;
//	
	JSONObject json;

	@SetupRender
	Object setupRender() {
		json = new JSONObject();
		return true;
	}

	@AfterRender
	Object afterRender() {
		json.put("ajaxUrl", resources.createEventLink("ajaxSuggestion", "%s").toURI());
		jsSupport.addInitializerCall("suggestion", json);
		return true;
	}
	
	public JSONObject onAjaxSuggestion(String query) {
		query = query.replaceAll("__", " ");
		JSONObject result = new JSONObject();
		result = fomalJson(query);
		return result;
	}
	
	private JSONObject fomalJson(String query){
		JSONObject result = new JSONObject();
		List<String> suggestionResult = suggestionService.getSuggestion(query) ;
		JSONArray temp  = new JSONArray(suggestionResult.toArray()); 
		result.put("suggestionResultList",temp);
		result.put("data", query);
		return result;
		
	}
	
	public boolean showSuggestionType(){
		boolean result = false;
		if(suggestionType.equals("homePage")){
			result = true;
		}
		return result;
	}
	
	
	@Inject
	JavaScriptSupport jsSupport;
	
	@Inject
	SuggestionService suggestionService;
	
	@Inject
	ComponentResources resources;
}
