Tapestry.Initializer.visualization_pub_line_chart = function(params) {
	testD3();
	
	function testD3() {
		if(window['d3'] == undefined) {
			setTimeout(testD3,100);
		} else {
			main();
		}
	}
	
	function main() {
		var margin = {
			top : 20,
			right : 20,
			bottom : 30,
			left : 30
		}, width = 600 - margin.left - margin.right, height = 150 - margin.top - margin.bottom;

		var lcParseDate = d3.time.format("%Y%m%d").parse;
//		var parseDate = d3.time.format("%Y").parse;

		var x = d3.time.scale().range([ 0, width ]);

		var y = d3.scale.linear().range([ height, 0 ]);

		var color = d3.scale.category10();

		var xAxis = d3.svg.axis().scale(x).orient("bottom");

		var yAxis = d3.svg.axis().scale(y).orient("left");

		var line = d3.svg.line().interpolate("basis").x(function(d) {
			return x(d.date);
		}).y(function(d) {
			return Math.round(y(d.paperNumber));
		});

		var svg = d3.select("#vis_pub_line_chart").append("svg").style("font-family","Simsun")//
				.attr("width", width + margin.left + margin.right)//
				.attr("height", height + margin.top + margin.bottom)//
				.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

//		d3.tsv("data.tsv", function(error, data) {
		var data = params.json;
			color.domain(data.terms);
			
			
//			var all_state = 1; //-1 all off, 0 complex, 1 all on
//			var showing_topic = {};
			var disableColor = "rgb(119, 119, 119)";
			jQuery(".vis_pub_term").each(function(){
//				showing_topic[jQuery.trim(jQuery(this).text())] = true;
				jQuery(this).css("background-color",color(jQuery.trim(jQuery(this).text())));
				jQuery(this).css("cursor","pointer");
				jQuery(this).click(function(){
					var thistext = jQuery(this).text();
					if(thistext == "All") {
						var all_shown = true;
						jQuery(".vis_pub_term").each(function(){
							if(jQuery(this).css("background-color") == disableColor)
								all_shown = false;
						});
						if(all_shown) {
							jQuery(".vis_pub_term").css("background-color", disableColor);
							jQuery(".plcLine").hide();
						} else {
							jQuery(".vis_pub_term").each(function(){
								jQuery(this).css("background-color",color(jQuery.trim(jQuery(this).text())));
							});
							jQuery(".plcLine").show();
						}
					} else {
//						var currentColor = jQuery(".vis_pub_term").css("background-color");
						if(jQuery(this).css("background-color") == disableColor) {
							jQuery(this).css("background-color",color(jQuery.trim(thistext)));
							jQuery(".plcLine." + thistext.replace(/[ \(\)\/]/g, "_")).show();
						} else {
							jQuery(this).css("background-color",disableColor);
							jQuery(".plcLine." + thistext.replace(/[ \(\)\/]/g, "_")).hide();
						}
					}
					
				});
			
			data.yearCount.forEach(function(d) {
				d.date = lcParseDate(d.year + "0101");
//				d.date = d.year;
			});

			var cities = color.domain().map(function(name) {
				var i = 0;
				for(i=0; i<data.terms.length; i++) {
					if(data.terms[i] == name)
						break;
				};
				return {
					name : name,
					values : data.yearCount.map(function(d) {
						return {
							date : d.date,
							paperNumber : +(d.count[i]+0.01*i)
						};
					})
				};
			});

			x.domain(d3.extent(data.yearCount, function(d) {
				return d.date;
			}));

			y.domain([ d3.min(cities, function(c) {
				return d3.min(c.values, function(v) {
					return v.paperNumber;
				});
			}), d3.max(cities, function(c) {
				return d3.max(c.values, function(v) {
					return v.paperNumber;
				});
			}) ]);

			svg.append("g")//
				.attr("class", "x axis")//
				.attr("transform", "translate(0," + height + ")")//
				.call(xAxis);

			svg.append("g").attr("class", "y axis")//
				.call(yAxis).append("text")//
				.attr("transform", "rotate(-90)")//
				.attr("y", 6)//
				.attr("dy", ".71em")//
				.style("text-anchor", "end")//
				.text("");
			
			var city = svg.selectAll(".city")//
				.data(cities)//
				.enter()//
				.append("g")//
				.attr("class", "city");

			city.append("path").attr("class", function(d) {
				if(d.name!="All")
					return "plcLine" + " " + d.name.replace(/[ \(\)\/]/g, "_");
			}).attr("d", function(d) {
				if(d.name!="All")
					return line(d.values);
			}).style("stroke", function(d) {
				if(d.name!="All")
					return color(d.name);
			});
			
	});
	}

};
