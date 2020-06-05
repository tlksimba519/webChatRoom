$("form").submit(function(){
	
	$.cookie("username",$("#name").val());

});

$("#login").validate({
	
	rules: {
		
		UserName: "required",
		
		Password: "required",
		
	},
	
	messages:{
		
		UserName: "必填",
		
		Password: "必填",
		
	}
	
});