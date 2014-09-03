package SchoolSearch.pages.department;

import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import SchoolSearch.services.dao.schooltest.model.SchooltestDepartment;
import SchoolSearch.services.monitor.MonitorService;
import SchoolSearch.services.services.organizationLevels.DepartmentService;

@Import(stylesheet = { "context:/res/css/pages/department/department.css" },//
library = { "context:/res/js/d3.v2.min.js",//
		"context:/res/js/visualization/visualization_CollapsableTree.js" })

public class DepartmentIndex {
	@Property
	JSONObject json;
	
	@SetupRender
	void setupRender() {
		monitor.visitPage("departmentIndex", null);
		
		Map<String, List<SchooltestDepartment>> existedDepartments = departmentService.getExistedDepartments();

		int schoolNumber = 0, departmentNumber = 0;
		for (String schoolName : existedDepartments.keySet()) {
			List<SchooltestDepartment> list = existedDepartments.get(schoolName);
			schoolNumber++;
			departmentNumber += list.size();
		}

		int height = departmentNumber * 21 + schoolNumber * 6;
		this.json = new JSONObject();
		this.json.put("height", height);
		this.json.put("json", formJson(existedDepartments));
	}

	@AfterRender
	void afterRender() {
		// json.put("getFunctionUrl", resources.createEventLink("get",
		// "%s").toURI());
		System.out.println(json.length());
		jsSupport.addInitializerCall("department_view", json);
	}

	public String getTitle() {
		return "院系概况";
	}

	private JSONObject formJson(Map<String, List<SchooltestDepartment>> existedDepartments) {
		JSONObject joo = new JSONObject();
		JSONArray jaa = new JSONArray();
		joo.put("name", "清华大学");
		for (String sname : existedDepartments.keySet()) {
			List<SchooltestDepartment> deps = existedDepartments.get(sname);
			JSONArray ja = new JSONArray();
			for (SchooltestDepartment dep : deps) {
				JSONObject joTmp = new JSONObject();
				joTmp.put("name", dep.getName());
				joTmp.put("iid", dep.getId());
				ja.put(joTmp);
			}
			JSONObject joChildren = new JSONObject();
			joChildren.put("name", sname);
			joChildren.put("children", ja);
			jaa.put(joChildren);
		}
		joo.put("children", jaa);
		return joo;
	}

	@Inject
	DepartmentService departmentService;

	@Inject
	private ComponentResources resources;

	@Inject
	private JavaScriptSupport jsSupport;
	
	@Inject
	private MonitorService monitor;
}
