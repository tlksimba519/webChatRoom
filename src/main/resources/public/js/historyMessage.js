/**
 * 獲得歷史訊息
 * 回傳data格式為{ "1" : [時間, 傳訊者, 訊息(文字或檔案路徑)], "2" = [時間, 傳訊者, 訊息(文字或檔案路徑)], ...}
 */
function accessHistory(historyCount) {
	
	let form = new FormData();
	form.append("historyCount", historyCount);
	
	$.ajax({
		
		  type : "POST",
		  url : "/accessHistory",
		  dataType : "json",
		  cache : false,
		  processData : false,
		  contentType : false,
		  data : form,
		  complete : function(response) {
			  
			  var data = response.responseJSON;
			  
			  for(var i = 1;i <= Object.keys(data).length;i++) {
				  
				  // 判定是否為最後一筆資料，若是則關閉事件監聽器
				  if(data[i.toString()] == "end") {

					  return messageArea.removeEventListener('scroll', loadHistory, true);
					  
				  } else {
					  
					  var messageElement = document.createElement('li');
					  messageElement.classList.add('chat-message');
		
				      var avatarElement = getAvatarElement(data[i.toString()][1]);
				      messageElement.appendChild(avatarElement);
		
				      var usernameElement = getUsernameElement(data[i.toString()][1]);
				      messageElement.appendChild(usernameElement);
				      
				      // 檔案處理  
				      if(data[i.toString()][2].search("file") != -1) {
				        	
				    	  var textElement = document.createElement('a');
				    	  var download = document.createAttribute("download");
				    	  var href = document.createAttribute("href");
				    	  
				    	  href.value = data[i.toString()][2];
				    	  textElement.setAttributeNode(href);
				    	  textElement.setAttributeNode(download);
				    	  
				    	  // 圖片預覽處理
				    	  if(data[i.toString()][2].search("jpg") != -1 || 
				    			  data[i.toString()][2].search("png") != -1) {
				        		
				    		  var image = document.createElement('img');
				    		  var src = document.createAttribute("src");
				    		  
				    		  src.value = data[i.toString()][2];
				    		  image.setAttributeNode(src);
				    		  image.style['width'] = "400px";
				    		  image.style['height'] = "300px";
				    		  textElement.appendChild(image);
				    	    	
				    	  } else {
				        		
				    		  textElement.appendChild(document.createTextNode(data[i.toString()][2].substring(5)));
				        		
				    	  }
				        	
				        //純文字處理
				      } else {
				        	
				    	  var textElement = document.createElement('p');
				    	  var messageText = document.createTextNode(data[i.toString()][2]);
				    	  
				    	  textElement.appendChild(messageText);
				    	    
				      }
				        
				      messageElement.appendChild(document.createElement('br'));
				      messageElement.appendChild(textElement);
				      messageArea.prepend(messageElement);
				      messageArea.scrollTop = 150;

				  }
				  
			  }

		  },
		  error : function() {
			  
			  alert("無法取得歷史紀錄，請稍後再試!");
			  
		  },
		  
	});
	
}