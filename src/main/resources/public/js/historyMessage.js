function getHistory(){
	
	var dataLength = 0;
	
	$.ajax({
		
		  type : "POST",
		  url : "/getHistory",
		  dataType: "json",
		  async:false,
		  complete : function(response){
			  
			  var data = response.responseJSON;
			  
			  for(var i=1;i<=Object.keys(data).length;i++){
				  
				  var messageElement = document.createElement('li');
				  messageElement.classList.add('chat-message');
	
			      var avatarElement = getAvatarElement(data[i.toString()][1]);
			      messageElement.appendChild(avatarElement);
	
			      var usernameElement = getUsernameElement(data[i.toString()][1]);
			      messageElement.appendChild(usernameElement);
			        
			      if(data[i.toString()][2].search("file")!=-1){
			        	
			    	  var textElement = document.createElement('a');
			    	  var download = document.createAttribute("download");
			    	  var href = document.createAttribute("href");
			    	  href.value=data[i.toString()][2];
			    	  textElement.setAttributeNode(href);
			    	  textElement.setAttributeNode(download);

			    	  if(data[i.toString()][2].search("jpg")!=-1 || 
			    			  data[i.toString()][2].search("png")!=-1){
			        		
			    		  var image = document.createElement('img');
			    		  var src = document.createAttribute("src");
			    		  src.value=data[i.toString()][2];
			    		  image.setAttributeNode(src);
			    		  image.style['width'] = "400px";
			    		  image.style['height'] = "300px";
			    		  textElement.appendChild(image);
			    	    	
			    	  }else{
			        		
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
	
			      messageArea.appendChild(messageElement);
			      messageArea.scrollTop = messageArea.scrollHeight;
			      
			  }
			  
			  $("#messageArea li").slice(0,-5).hide();
			  dataLength = Object.keys(data).length;

		  },
		  error : function(){
			  alert("無法取得歷史紀錄，請稍後再試!");
		  },
		  
	});
	
	return dataLength;
	
}