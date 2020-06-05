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

	},

	messages:{

		UserName:{

			required : "必填"

		},

		Password: "必填",

		PasswordTwice:{
			
			required : "必填",
			equalTo : "密碼不一致",

		},

	},

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

		},"此名稱已被使用");