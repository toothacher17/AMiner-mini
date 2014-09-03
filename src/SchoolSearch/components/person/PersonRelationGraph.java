package SchoolSearch.components.person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import SchoolSearch.services.dao.person.model.Person;
import SchoolSearch.services.dao.person.model.PersonRelation;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonrelation;
import SchoolSearch.services.services.person.PersonRelationService;
import SchoolSearch.services.services.person.PersonService;

@Import(library = { "context:/res/js/d3.v2.min.js",//
		"context:/res/js/visualization/visualization_RelationGraph.js" }//
)
public class PersonRelationGraph {
	@Parameter(allowNull = false)
	Integer personId;

	@Property
	Integer _personId;

	JSONObject json;
	
	void setupRender() {
		this._personId = personId;

		List<SchooltestPersonrelation> personRelations = personrelationService.getExistedPersonRelationByPersonId(_personId);
		System.out.println(personRelations.size());
//				getPersonRelationByPersonId(_personId);
		json = formJson(personRelations);
		json.put("personId", _personId);
	}

	void afterRender() {
		jsSupport.addInitializerCall("relation_graph", json);
	}

	private JSONObject formJson(List<SchooltestPersonrelation> personRelations) {
		final Map<Integer,Integer> pid2IndexMap = new HashMap<Integer, Integer>();
		pid2IndexMap.put(_personId, 0);
		JSONObject json = new JSONObject(); 
		JSONArray linkArray = new JSONArray();
		for(SchooltestPersonrelation pr : personRelations) {
			if(!pid2IndexMap.containsKey(pr.getPersonid1()))
				pid2IndexMap.put(pr.getPersonid1(), pid2IndexMap.size());
			if(!pid2IndexMap.containsKey(pr.getPersonid2()))
				pid2IndexMap.put(pr.getPersonid2(), pid2IndexMap.size());
				
			int frompIndex = pid2IndexMap.get(pr.getPersonid1());
			int topindex = pid2IndexMap.get(pr.getPersonid2());
			JSONObject link = new JSONObject();
			link.put("from", frompIndex);
			link.put("to", topindex);
			
			
			//TODO: other type
//			int typeCode = 0;
//			if(pr.gettype.equals("student"))
//				typeCode = 1;
//			else if(pr.type.equals("tutor"))
//				typeCode = 3;
//			else
//				typeCode = 2;
			
			int typeCode = 2;
			
			link.put("typeCode", typeCode);
			
			link.put("value", pr.getCount());
			linkArray.put(link);
		}
		List<Integer> pidList = new ArrayList<Integer>(pid2IndexMap.keySet());
//		ComparatorUtil.MapComparator<Integer, Integer> pid2IndexMapComparator = new MapComparator<Integer, Integer>(pid2IndexMap);
		
		List<SchooltestPerson> personList = personService.getPersonList(pidList);
		Collections.sort(personList, new Comparator<SchooltestPerson>() {
			@Override
			public int compare(SchooltestPerson o1, SchooltestPerson o2) {
				return pid2IndexMap.get(o1.getId()).compareTo(pid2IndexMap.get(o2.getId())); 
			}
		});
		JSONArray nodeArray = new JSONArray();
		for(SchooltestPerson p : personList) {
//			System.out.println(p.id + "\t" + pid2IndexMap.get(p.id));
			JSONObject node = new JSONObject();
			node.put("id", p.getId());
			node.put("index", pid2IndexMap.get(p.getId()));
			node.put("name", p.getName());
			
			nodeArray.put(node);
		}
		json.put("links", linkArray);
		json.put("nodes", nodeArray);
		return json;
	}

	@Inject
	PersonRelationService personrelationService;

	@Inject
	PersonService personService;

	@Inject
	JavaScriptSupport jsSupport;
}
