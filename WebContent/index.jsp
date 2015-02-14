<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="assets/css/bootstrap-responsive.css" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1" />
<meta name="keywords" content="Google maps, jQuery" />
<meta name="description" content="Performing Map" />

<title>Art Cultural Map</title>

<link rel="stylesheet" href="css/style.css" />
<link rel="stylesheet", href="css/bootstrap/2.3.2/bootstrap.css" />
<link rel="stylesheet", href="css/bootstrap/2.3.2/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/ui-lightness/jquery-ui.css" />
   	<style type="text/css">
		.map {
			width:100%; 
			height:842px;
		}
		.map img { 
			max-width:none; 
			height:auto; 
			border:0; 
			-ms-interpolation-mode:bicubic;
		}
		.label1{
			font-size:16px; background:#FF6666; color:white; padding:.25em
		}
		.label2{
			font-size:16px; background:#FF6666; color:white; padding:.25em
		}
		.noscrollbar {
			line-height:1.35;
			overflow:hidden;
			white-space:nowrap;
		}
		.nodisplay { display: none; }
		.display { display: block; }
		#list1 { float:left; width:90%; background:#FF6666; list-style:none; padding:0; } 
		#list1 li { padding:10px; color:#CCCCCC; } 
		#list1 li:hover { background:#555; color:#fff; cursor:pointer; cursor:hand; }
		#mwt_mwt_slider_scroll1 {
			top: 0;
			left:-360px;
			width:360px;	
			position:fixed;
			z-index:9999;
		}
		#mwt_slider_content1 {
			background:#FF6666;
			text-align:right;
			padding-top:20px;
			overflow:auto;
		}
		#mwt_fb_tab1 {
			position:absolute;
			top:200px;
			right:-24px;
			width:24px;
			background:#FF6666;
			color:#ffffff;
			font-family:Arial, Helvetica, sans-serif;	
			text-align:center;
			padding:9px 0;
		 
			-moz-border-radius-topright:10px;
			-moz-border-radius-bottomright:10px;
			-webkit-border-top-right-radius:10px;
			-webkit-border-bottom-right-radius:10px;	
		}
		#mwt_fb_tab1 span {
			display:block;
			height:12px;
			padding:1px 0;
			line-height:12px;
			text-transform:uppercase;
			font-size:12px;
		}
		#list2 { float:left; width:90%; background:#0099CC; list-style:none; padding:0; } 
		#list2 li { padding:10px; color:#CCCCCC; } 
		#list2 li:hover { background:#555; color:#fff; cursor:pointer; cursor:hand; }
		#mwt_mwt_slider_scroll2 {
			top: 0;
			left:-360px;
			width:360px;	
			position:fixed;
			z-index:9999;
		}
		#mwt_slider_content2 {
			background:#0099CC;
			text-align:right;
			padding-top:20px;
			overflow:auto;
		}
		#mwt_fb_tab2 {
			position:absolute;
			top:330px;
			right:-24px;
			width:24px;
			background:#0099CC;
			color:#ffffff;
			font-family:Arial, Helvetica, sans-serif;	
			text-align:center;
			padding:9px 0;
		 
			-moz-border-radius-topright:10px;
			-moz-border-radius-bottomright:10px;
			-webkit-border-top-right-radius:10px;
			-webkit-border-bottom-right-radius:10px;	
		}
		#mwt_fb_tab2 span {
			display:block;
			height:12px;
			padding:1px 0;
			line-height:12px;
			text-transform:uppercase;
			font-size:12px;
		}
		.ui-autocomplete {
			max-height: 600px;
			overflow-y: auto;
			/* prevent horizontal scrollbar */
			overflow-x: hidden;
		}
		* html .ui-autocomplete {
			height: 600px;
		}
		#alist li { padding:10px; color:#000000; } 
		#alist li:hover { background:#555; color:#fff;}
		a:hover { background:#555; color:#fff;} 
    </style>

<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC3TI_aOEP-JWo7bEI4NzLaU8U_BofxJpk&sensor=true" type="text/javascript"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.js" type="text/javascript"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.js" type="text/javascript"></script>
<script src="script/jqueryuimap/markerclusterer.js" type="text/javascript"></script>
<script src="script/jqueryuimap/jquery.ui.map.js" type="text/javascript"></script>
<script src="script/jqueryuimap/jquery.ui.map.extensions.js" type="text/javascript"></script>
<script src="script/jqueryuimap/jquery.ui.map.services.js" type="text/javascript"></script>
<script src="script/jqueryuimap/jquery.ui.map.overlays.js" type="text/javascript"></script>
<script type="text/javascript" src="script/tinybox.js"></script>

<script type="text/javascript">
	$(document).ready(function(){
		
		var warning = {
			show: function(msg) {
				$("#messages>b").html(msg);
				$("#messages").show();
			},
			hidden: function() {
				$("#messages").fadeOut(600);
				delete this.timeoutID;
			},
			hiddencheck: function() {
				if(!this.check()){
					this.hidden();
				}
			},
			showperiod: function(msg) {
				this.cancel();
				var self = this;
				self.show(msg);
				this.timeoutID = window.setTimeout(function(msg) {
					self.hidden();
				}, 3000);
			},
			cancel: function() {
				if(typeof this.timeoutID == "number") {
					window.clearTimeout(this.timeoutID);
					delete this.timeoutID;
				}
			},
			check: function() {
				if(this.timeoutID != undefined){
					return true;
				}else{
					return false;
				}
			},
			setup: function() {
				$("#messages>b").html("");
				$("#messages").hide();
				this.cancel();
			}
		};
		warning.setup();
		
		var startLatLon = new google.maps.LatLng(24.135, 120.67);
		if (navigator.geolocation) {
			warning.show("系統定位中...");
			navigator.geolocation.getCurrentPosition(
				function(position, status){
					startLatLon = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
					//window.console.log("geolocation successful : " + startLatLon);
					_map.panTo(startLatLon);
					_map.setZoom(15);
					warning.showperiod("定位完成");
				},
				function(error){
					var errorTypes={
						0:"不明原因錯誤",
						1:"使用者拒絕提供位置資訊",
						2:"無法取得您的位置資訊",
						3:"位置查詢逾時"
					};
					warning.showperiod(errorTypes[error.code]);
					//window.console.log("geolocation error : " + errorTypes[error.code]);
				}
			);
		} else {
			warning.showperiod("您的瀏覽器不支援Geo Location !");
		}
		

		$('#map1').gmap({
			'center':startLatLon,
			'zoom':15,
			'disableDefaultUI':false
		}).bind('init', function(evt, map) {
			//window.console.log("map init : " + evt);
		});
		
		
		
		function loadbyKeyword(keyword){
			var args = {'keyword':keyword};
			freshMarkers(args);
		}
		
		function loadData(bound, start, end){
			//window.console.log("loadData() : " + bound);
			var ne = bound.getNorthEast();
			var sw = bound.getSouthWest();
			var args = {'minLat':sw.lat(), 'maxLat':ne.lat(), 'minLon':sw.lng(), 'maxLon':ne.lng(), 'startDate':start, 'endDate':end};
			freshMarkers(args);
		}
		
		
		var _map = $('#map1').gmap('get', 'map');
		var _markerCluster = new MarkerClusterer(_map);
		var _markers=[];
		var _markersArray=[];
		var _refresh = false;
		var _isAutoFit = false;
		
		function freshMarkers(args){
			_refresh = false;
			$.getJSON('ajax/getjson.do', args, function(json) { 
				$.each(json.result, function(i, v) {
					
					var showTable = "<table class='table table-striped'>"+
						"<thead><tr><th>場次時間</th><th>場所</th><th>地址</th><th>票價</th></tr></thead>"+
						"<tbody>";
					$.each(this.showinfo, function() {
						showTable +="<tr><td>"+this.time+"</td><td>"+this.locationName+"</td><td>"+this.location+"</td><td>"+this.price+"</td></tr>";
					});
					showTable +="</tbody>"+
						"</table>";
					
					var content = "<div id='infoWindow' style='width:550px; height:600px'>"+
						"<h4>"+ v.title + "</h4>"+
						"<p style='color:red;'>"+v.startDate+" ~ "+v.endDate +"&nbsp;&nbsp;"+ v.locationName+"&nbsp;[ "+v.location+" ]</p>"+
						"<div style='word-break: break-all;word-wrap: break-word;'><p>"+ replaceAll('  ', '<p>', v.descriptionFilterHtml) +"</p></div>"+
						showTable+
						"<span class='label label-warning'>折扣活動</span>&nbsp;"+ replaceAll(' ', '<p>', v.discountInfo) +"<br/>" + 
						"<span class='label label-success'>資料來源</span>&nbsp;"+(v.sourceWebPromote?"<a href='"+ v.sourceWebPromote +"'>"+ v.sourceWebName +"</a><br/>": v.sourceWebName +"<br/>") + 
						"<span class='label label-success'>相關單位</span>&nbsp;"+ v.masterUnit + v.subUnit + v.supportUnit +"</br>"+ 
						"<span class='label label-success'>表演團體</span>&nbsp;"+ v.showUnit +"</br>"+
						"<span class='label label-info'>資料最後更新日</span>&nbsp;"+ v.editModifyDate +"<br/>"+
						"<span class='label label-info'>藝文網址</span>&nbsp;<a href='"+ (v.webSales ? v.webSales : v.sourceWebPromote) +"' target='_blank'>"+ (v.webSales ? v.webSales : v.sourceWebPromote) +"</a><br/>" + 
						(v.webSales || v.sourceWebPromote ?"<div><iframe width='100%' height='520px' src='"+(v.webSales ? v.webSales : v.sourceWebPromote)+"' frameborder='0'></iframe></div>" : "")+
						"<p></p>"+
						"<a id='"+ v.UID +"' class='label1' href='#'>More..</a><br/>" +
						"</div>";
				
					if(!_markers[v.UID]){//It is not duplicated to show
						_markers[v.UID] = true;
						_refresh = true;
						//marker generation
						$('#map1').gmap('addMarker', {
							'position':new google.maps.LatLng(v.latitude, v.longitude),
							'id':i,
							'title':v.title+" ["+v.locationName+"]",
							//'icon' : new google.maps.MarkerImage(v.photo_file_url, new google.maps.Size(20, 34), new google.maps.Point(0, 0), new google.maps.Point(300, 300)),
							'content':content, 
							'tags':'art',
							'bounds':false,
							'animation':google.maps.Animation.DROP, 
							'time':v.startDate+" ~ "+v.endDate,
							'location':v.locationName,
							'uid':v.UID
						}).mouseover(function() {
							//window.console.log("mouse over : " + $(this).attr("id"));
						}).mouseout(function() {
							//window.console.log("mouse out : " + $(this).attr("id"));
						}).click(function() {// marker clicked
							var markerObj = this;
							
							// open info window
							$('#map1').gmap('openInfoWindow', {
								'content':$(this)[0].content,
								'maxWidth':550,
							}, this, function(){
								
								$("a.label1").click(function(e) {
									//window.console.log("click me : " + $(this).attr("id"));
									openDialogbyMarker(markerObj);
								});

							});
							
						}).each(function(i, marker) {
							var markObj = $(this);
							//window.console.log("marker each : " +$(this).get(0).getPosition());
							_markersArray.push(markObj.get(0));
							
							// ingest activities list
							// by name
							$("<li />").html("<h4>"+ highlighting($(this).attr("location"), $("#query").val()) +"</h4>"+ highlighting($(this).attr("title"), $("#query").val()) +"<br/>"+ $(this).attr("time") ) 
								.click(function(){
									event.preventDefault();
									markerClick(markObj.get(0));
									//TINY.box.hide();
					  			}).appendTo("#list1");
							// by date
							$("<li />").html("<h4>"+$(this).attr("time") +"</h4>"+ highlighting($(this).attr("title"), $("#query").val()) +"<br/>"+ highlighting($(this).attr("location"), $("#query").val()) ) 
								.click(function(){
									event.preventDefault();
									markerClick(markObj.get(0));
									//TINY.box.hide();
					  			}).appendTo("#list2");
						});
					}
					
				});
				
	
				if(_markerCluster && _refresh){
					_markerCluster.clearMarkers();
					_markerCluster = new MarkerClusterer(_map, _markersArray,{
						'maxZoom':15,
						'gridSize':40
					});
					
					google.maps.event.addListener(_markerCluster, 'clusterclick', function(cc) {
						//window.console.log("Number of managed markers in cluster: " + cc.getSize());
						var m = cc.getMarkers();
						$("#alist").empty();
						$.each(m, function(i, v) {
							//window.console.log(m[i]);
							$("<li />").html(m[i]["time"]+ " <a href='#' onclick=markerClickIdx('"+m[i]['uid']+"');>" + m[i]["title"] + "</a>")
					  			.appendTo("#alist");
						});
						TINY.box.show({html:$("#alistdiv").html(), animate:true, opacity:20});
					});
					
				}
				sortList();
				
				if(_isAutoFit){
					_isAutoFit = false;
					if(_refresh){
			    		var bounds = new google.maps.LatLngBounds();
			    		for(var i=0;i<_markersArray.length;i++) {
			    			bounds.extend(_markersArray[i].getPosition());
			    		}
			    		_map.fitBounds(bounds);
					}else{
						warning.showperiod("藝文活動已過期或找不到藝文");
					}
				}
				
			})
			.fail(function(jqxhr, textStatus, error ) {
				//window.console.log(textStatus + ", " + error);
				if(_isAutoFit){
					warning.showperiod("即將為您導向到 ["+$("#query").val()+"]");
					panLatLngByAddr($("#query").val());
					_isAutoFit = false;
				}
			});
		}
		
		
		ClusterIcon.prototype.triggerClusterClick = function() {
			var markerClusterer = this.cluster_.getMarkerClusterer();
			// Trigger the clusterclick event.
			google.maps.event.trigger(markerClusterer, 'clusterclick', this.cluster_);
			
			if (markerClusterer.isZoomOnClick()) {
				this.map_.fitBounds(this.cluster_.getBounds()); // Zoom into the cluster.
				//this.map_.setZoom(markerClusterer.getMaxZoom()+1); // modified zoom in function
			}
		};
		
		
		
		// Map 'idle' event handler
		$(_map).addEventListener('idle', function() {
			//window.console.log("idle : " + this.getBounds());
			var bound = this.getBounds();
			var timer;
	        clearTimeout(timer);
	        timer = setTimeout(function() {
	        	loadData(bound, $("#dp1").val(), $("#dp2").val());
	        }, 500);
		});
		
		// zoom change event
		$(_map).addEventListener('zoom_changed', function() {
			//window.console.log("zoom_changed : " + map.getZoom());
			if(_map.getZoom() <= 11){
				warning.show("您瀏覽的地圖範圍過大，請放大地圖載入藝文活動");
			}else{
				warning.hiddencheck();
			}
		});
		
		//trun off both of the left side windows when drag end
		$(_map).addEventListener('dragend', function() {
			//window.console.log("dragend : " + this);
			var timer;
	        clearTimeout(timer);
	        timer = setTimeout(function() {
	        	$("#mwt_mwt_slider_scroll1").animate( { left:'-'+w1+'px' }, 600 ,'swing');
	        	$("#mwt_mwt_slider_scroll2").animate( { left:'-'+w2+'px' }, 600 ,'swing');
	        }, 500);
		});
		
		
		//error close button
		$("#closemsg").button().click(function( event ) {
			event.preventDefault();
			warning.hidden();
		});
		
		
		//Search keyword autocomplete
		var _cache = {};
		$("#query").autocomplete({
			source:function(request, response){
				var term = request.term;
		        if(term in _cache){
					response(_cache[term].result);
					return;
		        }
				$.ajax({
					url : "ajax/getaddress.do",
					dataType : "json",
					data : {
						query : encodeURIComponent($("#query").val())
					},
					success : function(data){
						_cache[term] = data;
						response(data.result);
						//response($.map(data.result, function(item){
						//	return {value:item};
						//}));
					}
				});
			},
			minLength:1
		}).data("ui-autocomplete")._renderItem = function(ul, item) {
			var type, cateType;
			if(item.category=="3"){
				type = "badge badge-success";
			}else if(item.category=="1"){
				type = "badge badge-info";
			}else{
				type = "badge";
			}
			cateType = "<span class='"+type+"'>"+ item.categoryName + "</span>";
			return $("<li>").append("<a>" + item.label + "<br/><small>"+ cateType +"</small></a>").appendTo(ul);
		};
		
		
		//search1 button click
		$("#search1").button().click(function( event ) {
			event.preventDefault();
			
			
			if($("#query").val()==""){
				warning.showperiod("請輸入搜尋地點");
				return;
			}
			
			warning.show("搜尋中…");
			var timer;
	        clearTimeout(timer);
	        timer = setTimeout(function() {
	        	clear();
	        	loadbyKeyword(encodeURIComponent($("#query").val()));
	        	_isAutoFit = true;
	        }, 500);
		});

		
		
		//DatePicker
		$("#dp1").datepicker({
			changeYear: true,
			changeMonth: true, 
		});
		$("#dp1").datepicker("option", "dateFormat","yy/mm/dd");
		$("#dp2").datepicker({
			changeYear: true,
		    changeMonth: true,
		});
		$("#dp2").datepicker("option", "dateFormat","yy/mm/dd");
		
		//go button click
		$("#go").button().click(function( event ) {
			event.preventDefault();
			
			/*
			if($("#dp1").val()==""){
				warning.showperiod("開始日必輸入");
				return;
			}
			*/
			if(Date.parse($("#dp1").val()).valueOf() > Date.parse($("#dp2").val()).valueOf()){
				warning.showperiod("開始日必小於結束日");
				return;
			}
			
			var bound = _map.getBounds();
			var timer;
	        clearTimeout(timer);
	        timer = setTimeout(function() {
	        	clear();
	        	loadData(bound, $("#dp1").val(), $("#dp2").val());
	        }, 500);
	        if($("#dp1").val()==""){
	        	openDialog("[藝文日期]設定已取消 !");
	        }else{
	        	openDialog("[藝文日期]已設定 !");
	        }
	        warning.hidden();
		});
		
		
		
		//open dialog
		var name = $( "#name" );
		var email = $( "#email" );
		var password = $( "#password" );
		var allFields = $([]).add(name).add(email).add(password);
		var tips = $( ".validateTips" );
		
		function openDialogbyMarker(marker) {
			//_map.panTo($(marker).get(0).getPosition()); //move position to center
			//_map.setZoom(18);
			
			//window.console.log("openDialog() marker --> " + $(marker) + "  " + $(marker).attr('id') + " "+ $(marker).get(0).getPosition());
			$('#dialogWin').dialog({
				'modal':true,
				'width':900,
				'title':$(marker).attr('title'), 
				'buttons':{
					'Close':function() {
						
						$( "#users tbody" ).append( "<tr>" +
							"<td>" + name.val() + "</td>" +
							"<td>" + email.val() + "</td>" +
							"<td>" + password.val() + "</td>" +
							"</tr>");
						
						$(this).dialog('close');
					
					}
				}
			});
			
		}
		
		
		
		//left slider1
		var w1 = $("#mwt_slider_content1").width();
		$('#mwt_slider_content1').css('height', ($(window).height() - 20) + 'px' );
		$("#mwt_fb_tab1").mouseover(function(){
			if ($("#mwt_mwt_slider_scroll1").css('left') == '-'+w1+'px'){
				$("#mwt_mwt_slider_scroll1").animate({ left:'0px' }, 600 ,'swing');
			}
		});
		$("#mwt_slider_content1").mouseleave(function(){
			$("#mwt_mwt_slider_scroll1").animate( { left:'-'+w1+'px' }, 600 ,'swing');
		});
		//left slider2
		var w2 = $("#mwt_slider_content2").width();
		$('#mwt_slider_content2').css('height', ($(window).height() - 20) + 'px' );
		$("#mwt_fb_tab2").mouseover(function(){
			if ($("#mwt_mwt_slider_scroll2").css('left') == '-'+w2+'px'){
				$("#mwt_mwt_slider_scroll2").animate({ left:'0px' }, 600 ,'swing');
			}
		});
		$("#mwt_slider_content2").mouseleave(function(){
			$("#mwt_mwt_slider_scroll2").animate( { left:'-'+w2+'px' }, 600 ,'swing');
		});
		
		
		//alert
		function openDialog(content){
			$("#dialog1").html("<p>"+content+"</p>");
			$("#dialog1").dialog({
				height: 120,
				modal: true
			});
		}
		
		function sortList(){
			var items = $("#list1>li").get();
			items.sort(function(a,b){
				var keyA = $(a).text();
				var keyB = $(b).text();
				if (keyA < keyB) return -1;
				if (keyA > keyB) return 1;
				return 0;
			});
			var ul = $("#list1");
			$.each(items, function(i, li){
				ul.append(li);
			});
			var items = $("#list2>li").get();
			items.sort(function(a,b){
				var keyA = $(a).text();
				var keyB = $(b).text();
				if (keyA < keyB) return -1;
				if (keyA > keyB) return 1;
				return 0;
			});
			var ul = $("#list2");
			$.each(items, function(i, li){
				ul.append(li);
			});
		}
		
		function clear(){
			if(_markerCluster){
				_markerCluster.clearMarkers();
			}
			for (var i = 0; i < _markersArray.length; i++ ) {
				_markersArray[i].setMap(null);
			}
			_markersArray=[];
			$("#list1").empty();
			$("#list2").empty();
			_markers=[];
		}
		
		// address to geo location
		function panLatLngByAddr($address) {
		    var geocoder = new google.maps.Geocoder();  //定義一個Geocoder物件
		    geocoder.geocode({ 
		    	address: $address },    //設定地址的字串
		        function(results, status) {    //callback function
		            if (status == google.maps.GeocoderStatus.OK) {    //判斷狀態
		              	_map.panTo(results[0].geometry.location); //取得座標
		              	_map.setZoom(17);
		            } else {
		                window.console.log("panLatLngByAddr() Error");
		            }
		      	}
		    );
		}
		
		function replaceAll(find, replace, str) {
			return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
		}
		
		function escapeRegExp(string) {
		    return string.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
		}
		
		function highlighting(src, key){
			if(src && key){
				return replaceAll(key, "<span class='badge badge-warning'><strong>"+key+"</strong></span>", src);
			}else{
				return src;
			}
		}
		
		function markerClick(markObj){
			_map.panTo(markObj.getPosition());
			_map.setZoom(16);
			google.maps.event.trigger(markObj, 'click');
		}
		
		window.markerClickIdx = function(uid){
			$.each(_markersArray, function(i, v) {
				if(_markersArray[i]['uid'] == uid){
					//window.console.log("markerClickIdx match --> "+_markersArray[i]['title']);
					google.maps.event.trigger(_markersArray[i], "click");
				}
			});
			
		};

		
	});
</script>
</head>
<body>
	<noscript>No Javascript support</noscript>
	<div class="navbar navbar-static-top">
		<div class="page-header">
			<h1>&nbsp;藝文活動地圖 <small>Art Cultural Map</small></h1>
		</div>
	</div>
	<div class="navbar navbar-fixed-top">
		<div class="pagination pagination-centered">
			<form action="index.jsp" class="navbar-form pull-right">
				<input type="text" id="query" class="input-medium search-query span4" placeholder="輸入地點：台中市中山堂">
				<button id="search1" class="btn">搜尋</button>
				&nbsp;
				<input type="text" id="dp1" class="input-small span2" placeholder="藝文開始日期">
				<input type="text" id="dp2" class="input-small span2" placeholder="藝文結束日期">
				<button id="go" class="btn btn-info">設定</button>
				&nbsp;
			</form>
			<br/>
			<br/>
			<p></p>
			<div id="messages" class="alert alert-error">
				<button id="closemsg" type="button" class="close" data-dismiss="alert">&times;</button>
				<b></b>
			</div>
		</div>
	</div>
	<div id="map1" class="map"></div>
	<div id="mwt_mwt_slider_scroll1">
		 <div id="mwt_fb_tab1">
			<span>藝</span>
			<span>文</span>
			<span>活</span>
			<span>動</span>
			<span>場</span>
			<span>地</span>
			<span>排</span>
			<span>序</span>
		</div>
		<div id="mwt_slider_content1">
			<ul id="list1"></ul>
		</div>
	</div>
	<div id="mwt_mwt_slider_scroll2">
		 <div id="mwt_fb_tab2">
			<span>藝</span>
			<span>文</span>
			<span>活</span>
			<span>動</span>
			<span>日</span>
			<span>期</span>
			<span>排</span>
			<span>序</span>
		</div>
		<div id="mwt_slider_content2">
			<ul id="list2"></ul>
		</div>
	</div>
	<div id="alistdiv" class='nodisplay'>
		<ul id="alist"></ul>
	</div>
	<div id="dialogWin" class='nodisplay'>
		<form>
  			 <fieldset>
				<label for="name">Name</label>
				<input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all">
				<label for="email">Email</label>
				<input type="text" name="email" id="email" value="" class="text ui-widget-content ui-corner-all">
				<label for="password">Password</label>
				<input type="password" name="password" id="password" value="" class="text ui-widget-content ui-corner-all">
			</fieldset>
  		</form>
	</div>
	<div id="dialog1" title="訊息"></div>
	
</body>
</html>