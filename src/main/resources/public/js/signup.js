$(document).ready(function(){
	
	$.ajax({
		
		  type : "GET",
		  url : "/getUser",
		  dataType: "json",
		  complete : function(response){
			  
			  var data = response.responseJSON;
			  var temp = [];
			  
			  for(var i=1;i<=Object.keys(data).length;i++){
				  
				  temp.push(data[i.toString()]);
				  
			  }
			  
		  },
		  error : function(){
			  alert("無法取得會員資料，請稍後再試!");
		  },
		  
	});
	
});