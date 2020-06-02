$("form").submit(function(){
	$.cookie("username",$("#name").val());
});