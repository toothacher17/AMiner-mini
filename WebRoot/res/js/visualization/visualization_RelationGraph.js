Tapestry.Initializer.relation_graph = function(params) {
	var r = 5;
	
	testD3();

	function testD3() {
		if (window['d3'] == undefined) {
			setTimeout(testD3, 100);
		} else {
			init();
		}
	}

	
	function init() {
		var width = 362, height = 362;

		var radii = width / 2;

		var color = d3.scale.category10();

		var diagonal = d3.svg.diagonal.radial().projection(function(d) {
			return [ d.y, d.x / 180 * Math.PI ];
		});

		var svg = d3.select("#svg_egonetwork")//
		.attr("width", radii * 2)//
		.attr("height", radii * 2)//
//		.on("click", networkBlankClick)//
		.append("g")//
		.attr("transform", "translate(" + radii + "," + radii + ")");

		var json = params;
		console.log(params);
		var personId = json.personId;

		var linkValueArray = new Array();
		var linkData = new Array();

		// generate Link Value list
		// prepare to group person
		for ( var i = 0; i < json.links.length; i++) {
			if (json.links[i].from == 0 || json.links[i].to == 0) {
				linkData.push(json.links[i]);
				var valueExist = false;
				for ( var j = 0; j < linkValueArray.length; j++) {
					if (linkValueArray[j] == json.links[i].value) {
						valueExist = true;
						break;
					}
				}
				if (!valueExist) {
					linkValueArray.push(Number(json.links[i].value));
				}
			}
		}
		linkValueArray.sort(function(x, y) {
			return x - y;
		});
		linkData.sort(function(x, y) {
			return -(x.value - y.value);
		});
		
		var typeColor = new Array();
		typeColor.push("#B3CDE3");
		typeColor.push("#FBB4AE");
		typeColor.push("#CCEBC5");
		// typeColor.push("#DECBE4");
		typeColor.push("#FED9A6");

		typeColor.push("#377EB8");
		typeColor.push("#E41A1C");
		typeColor.push("#4DAF4A");
		// typeColor.push("#984EA3");
		typeColor.push("#FF7F00");
		
		main(linkData, linkValueArray, radii, svg, typeColor);
	}

	// * filter start nodes;
	function getCurrentLinkData(linkData, linkValueArray) {
		var json=params;
		
		var showingThreadHold = 30;
		var currentSlideIndex = 0;
		if (linkData.length >= showingThreadHold) {
			for ( var i = 0; i < linkValueArray.length; i++) {
				if (linkData[showingThreadHold - 1].value == linkValueArray[i])
					currentSlideIndex = i;
			}
		}
		var currentLinkData = new Array();
		for ( var i = 0; i < json.links.length; i++) {
			if (json.links[i].to == 0 || json.links[i].from == 0) {
				if (json.links[i].value >= linkValueArray[currentSlideIndex])
					currentLinkData.push(json.links[i]);
			}
		}
		currentLinkData.sort(function(x, y) {
			var xvalue, yvalue;
			xvalue = x.typeCode * 100 + x.value;
			yvalue = y.typeCode * 100 + y.value;
			console.log(x.from + "\t" + x.to + "\t" + xvalue);
			return (xvalue - yvalue);
		});
		return currentLinkData;
	}
	// */

	

	function main(linkData, linkValueArray, radii, svg, typeColor) {
		var currentLinkData = getCurrentLinkData(linkData, linkValueArray);
		var json=params;
		
		var egonet = d3.layout.egonet().radius(radii);
		egonet.nodes(json.nodes).links(currentLinkData).layout();

		var nodes = egonet.nodes();
		console.log(nodes);
		
		var link = svg.selectAll("line.network_link")//
		.data(currentLinkData)//
		.enter()//
		.append("line")//
		.attr("class", "network_link")//
		.style("stroke-width", function(d) {
			return 2;
		})//
		.attr("x1", 0)//
		.attr("x2", function(d, i) {
			var dindex = d.from == 0 ? d.to : d.from;
			return nodes[dindex].y;
		})//
		.attr("y1", 0)//
		.attr("y2", 0)//
		.style("stroke", function(d) {
			var dindex = d.from == 0 ? d.to : d.from;
			var code = d.typeCode;
			code += 4;
			return typeColor[code];
		}).attr("node-index", function(d, i) {
			return d.from == 0 ? d.to : d.from;
		}).attr("transform", function(d, i) {
			dindex = d.from == 0 ? d.to : d.from;
			return "rotate(" + (nodes[dindex].x - 90) + ")translate(" + 0 + ")";
		}).on("mouseover", linkmouseover).on("click", linkmouseclick).on("mouseout", mouseout);

		var nodeData = new Array();
		for ( var i = 0; i < nodes.length; i++) {
			if (nodes[i].using)
				nodeData.push(nodes[i]);
		}

		var node = svg.selectAll("g.network_node").data(nodeData).enter().append("g").attr("class", "network_node").//
		style("fill", function(d) {
			var code = d.typeCode;
			// if(code==0)
			code += 4;
			return typeColor[code];
		}).attr("node-index", function(d, i) {
			return i;
		}).attr("transform", function(d) {
			return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")";
		}).on("mouseover", nodemouseover).on("click", nodemouseclick).on("mouseout", mouseout);

		node.append("circle").attr("r", r);

		node.append("text").attr("dy", ".31em").attr("text-anchor", function(d) {
			return d.x < 180 ? "start" : "end";
		}).attr("transform", function(d, i) {
			if (i != 0) {
				var trans = (r * 2);
				return d.x < 180 ? "translate(" + trans + ")" : "rotate(180)translate(-" + trans + ")";
			} else {
				return "rotate(90)translate(-" + (d.name.length * 2.1) + "," + (r * 2 + 3) + ")";
			}
		})//
		.style("font-weight:", "bold")//
		.style("cursor", "pointer")//
		.text(function(d) {
			return d.name;
		});

		node.append("title").text(function(d) {
			return d.name;
		});

		var fixed = false;
		this.lostFix = function() {
			fixed = false;
		};

		function linkmouseover(d, i) {
			var nodeindex = d.from == 0 ? d.to : d.from;
			mouseover(nodeindex, this);
		}
		function nodemouseover(d, i) {
			mouseover(i, this);
		}
		function linkmouseclick(d, i) {
			var nodeindex = d.from == 0 ? d.to : d.from;
			mouseclick(nodeindex, this);
		}
		function nodemouseclick(d, i) {
			mouseclick(i, this);
		}
		function mouseclick(index, thisobj) {
			if(index!=0)
				window.location.href='/person/'+nodes[index].id;
			else 
				return;
//			console.log(nodes[index]);
//			console.log(thisobj);
		}
		
		
		// var timeout4hiderdd = undefined;

		function mouseover(index, thisobj) {
			if (fixed)
				return;
			if (index == 0) {
				return;
			}
			// if(timeout4hiderdd != undefined) {
			// clearTimeout(timeout4hiderdd);
			// timeout4hiderdd = undefined;
			// }
			// rdd.switchToBoleViewVision(index, jQuery(thisobj).offset().top,
			// jQuery("#vis_network"));

			jQuery("g.network_node").each(function() {
				var nindex = jQuery(this).attr("node-index");
				var code = nodes[nindex].typeCode + 4;
				jQuery(this).css("fill", typeColor[code]);
			});
			jQuery("line.network_link").each(function() {
				var nindex = jQuery(this).attr("node-index");
				var code = nodes[nindex].typeCode + 4;
				jQuery(this).css("stroke", typeColor[code]);
			});

			var nodeobj = jQuery("g.network_node[node-index=" + index + "]");
			var linkobj = jQuery("line.network_link[node-index=" + index + "]");
			jQuery(nodeobj).css("fill", d3.rgb(jQuery(nodeobj).css("fill")).brighter());
			jQuery(linkobj).css("stroke", d3.rgb(jQuery(linkobj).css("stroke")).brighter());

		}
		function mouseout(d, i) {
			if (fixed)
				return;
			var index;
			if (d.name == undefined) {
				index = d.from == 0 ? d.to : d.from;
			} else {
				index = i;
			}

			var code = nodes[index].typeCode;
			code += 4;

			var nodeobj = jQuery("g.network_node[node-index=" + index + "]");
			var linkobj = jQuery("line.network_link[node-index=" + index + "]");
			jQuery(nodeobj).css("fill", typeColor[code]);
			jQuery(linkobj).css("stroke", typeColor[code]);

			// if(timeout4hiderdd == undefined)
			// timeout4hiderdd = setTimeout(mouseoutAction, 200);
		}
		// function mouseoutAction() {
		// rdd.hide();
		// }
	}

};

d3.layout.egonet = function() {
	var egonet = {}, size = [ 1, 1 ], nodes = [], links = [];
	egonet.layout = function() {

		var root = nodes[0];
		nodes[0].using = true;
		nodes[0].typeCode = 0;

		root.x = 0;
		root.y = 0;

		if (nodes.length == 1) {
			return;
		}

		var maxValue = 0.0;
		var minValue = 10000.0;
		for ( var i = 0; i < links.length; i++) {
			if (links[i].value > maxValue) {
				maxValue = links[i].value;
			}
			if (links[i].value < minValue) {
				minValue = links[i].value;
			}
			var lindex = (links[i].from == 0 ? links[i].to : links[i].from);
			nodes[lindex].typeCode = links[i].typeCode;
		}

		for ( var i = 1; i < nodes.length; i++) {
			nodes[i].using = false;
		}

		var delta = Math.PI * 2 / (links.length);
		for ( var i = 0; i < links.length; i++) {
			// var wei = 1 - links[i].value / (maxValue);
			// wei = Math.pow(wei, 2);
			var wei = maxValue != minValue ? (-0.5 * links[i].value + maxValue - minValue / 2) / (maxValue - minValue) : 1.0;
			var lindex = (links[i].from == 0 ? links[i].to : links[i].from);

			nodes[lindex].x = (i * delta) * 180 / Math.PI;
			nodes[lindex].y = 0.6 * (radius * wei);
			nodes[lindex].using = true;
		}
	};
	egonet.nodes = function(x) {
		if (!arguments.length)
			return nodes;
		nodes = x;
		return egonet;
	};
	egonet.links = function(x) {
		if (!arguments.length)
			return links;
		links = x;
		return egonet;
	};
	egonet.size = function(x) {
		if (!arguments.length)
			return size;
		size = x;
		return egonet;
	};

	egonet.radius = function(x) {
		if (!arguments.length)
			return radius;
		radius = x;
		return egonet;
	};
	return egonet;
};