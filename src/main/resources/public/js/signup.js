var temp = [];

$(document).ready(function(){
	
	$.ajax({
		
		  type : "GET",
		  url : "/getUser",
		  dataType: "json",
		  complete : function(response){
			  
			  var data = response.responseJSON;
			  
			  for(var i=1;i<=Object.keys(data).length;i++){
				  
				  temp.push(data[i.toString()]);
				  
			  }
			  
		  },
		  error : function(){
			  alert("無法取得會員資料，請稍後再試!");
		  },
		  
	});
	
});
$("#signup").validate({
	  rules: {
	    UserName: {
	    	required :true,
	    	alreadyUsed :true
	    },
	    Password: "required",
	    PasswordTwice:{
	    	equalTo :"#Password"
	    }
	  }
});
jQuery.validator.addMethod("alreadyUsed", function(value, element) 
		{
		    value  = $.trim(value);
		    if(temp.indexOf(value)==-1)
		    {
		       return true;
		    }
		    else
		    {
		        return false;
		    }

		},"Username is already used. Sorry.");