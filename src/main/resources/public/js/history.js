var historyList = document.querySelector('#list');
$(document).ready(function(){
	
	$.ajax({
		
		  type : "POST",
		  url : "/getHistory",
		  dataType: "json",
		  async:false,
		  complete : function(response){
			  
			  var data = response.responseJSON;
			  var temp = "";
			  for(var i=1;i<=Object.keys(data).length;i++){
				  
				  temp = "";
				  var msg = document.createElement('tr');
				  
				  for(var j=0;j<3;j++){
					  
					  var text = document.createElement('td');
					  temp = data[i.toString()][j];
					  text.appendChild(document.createTextNode(temp));
					  msg.appendChild(text);
	
				  }
				  
				  historyList.appendChild(msg);
				  
			  }
			  
		  },
		  error : function(){
			  alert("無法取得歷史紀錄，請稍後再試!");
		  },
		  
	});
	
});