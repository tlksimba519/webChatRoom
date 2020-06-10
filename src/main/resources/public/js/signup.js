/*
 * 註冊表單欄位驗證
 */
$("#signup").validate({

	rules : {

		UserName : {

			required : true,

		},

		Password : "required",

		PasswordTwice : {

			equalTo : "#Password"

		}

	},

	messages : {

		UserName : {

			required : "必填"

		},

		Password : "必填",

		PasswordTwice : {
			
			required : "必填",
			equalTo : "密碼不一致",

		},

	},

});