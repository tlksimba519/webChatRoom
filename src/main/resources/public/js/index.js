/*
 * 表單送出後存下使用者名稱
 */
$("form").submit(function() {
	
	sessionStorage.setItem("username", $("#name").val());

});

/*
 * 登入表單欄位驗證
 */
$("#login").validate({
	
	rules : {
		
		UserName : "required",
		
		Password : "required",
		
	},
	
	messages : {
		
		UserName : "必填",
		
		Password : "必填",
		
	}
	
});