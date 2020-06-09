/**
 * html標籤表
 */
var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var nameInput = document.querySelector('#name');
var fileInput = document.querySelector('#file');

var stompClient = null;
var username = null;
var file = null;
var fileName = null;
var fileType = null;

/**
 * 頭像顏色表
 */
var colors = [ '#2196F3', '#32c787', '#00bcd4','#4dbb00', '#ff5652', '#ffc107',
        '#ff85af', '#ff9800', '#39bbb0', '#b0c503' ];

/**
 * 連線function
 * 描述 : 確認當前使用者名稱是否存在，若有則顯示主畫面
 */
function connect(event) {

    if (username!=null) {

    	//顯示聊天室
        chatPage.classList.remove('hidden');
        
        //建立sockJS to STOMP
        var socket = new SockJS('/chatroom');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
        
    }

    event.preventDefault();
    
}

/**
 * 連線建立後callback function
 * 描述 : 連線建立後訂閱channel，並廣播使用者上線通知
 */
function onConnected() {
    //訂閱/topic/public
    stompClient.subscribe('/topic/public', onMessageReceived); //訂閱成功後 callback onMessageReceived()。

    //發送加入訊息至/app/join，也就是送到Controller.addUser()
    stompClient.send("/app/join", {}, JSON.stringify({
    	
        sender : username,
        type : 'JOIN'
        	
    }))
    //關閉 "connecting.."
    connectingElement.classList.add('hidden');
    //播放登入音效
    $("#loginSuccess")[0].play();
    
}

/*
 * 連線建立失敗 callback function
 */
function onError(error) {
	
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
    
}

/**
 * 發送訊息 function
 * 描述 : 
 */
function sendMessage(event) {
	
    var messageContent = messageInput.value.trim();
    
    if (stompClient) {
    	
    	//傳檔案
	    if(fileName&&!messageContent){
	    	
	    	var chatMessage = {
	    			
	                sender : username,
	                fileName : fileName,
	                filePath : "file/"+username+"_"+fileName,
	                fileType : fileType,
	                type : 'FILE'
	                	
	        };
	    	
	    //空字串阻擋
	    }else if(!messageContent){
	    	
	    	event.preventDefault();
	    	
	    //傳訊息
	    }else {
	    	
	    	var chatMessage = {
	    			
	                sender : username,
	                content : messageInput.value,
	                type : 'CHAT'
	                	
	        };
	    	
	    }
	        // 發送訊息至/app/chat，也就是送到Controller.sendMessage()
	        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
	        //清空輸入欄
	        messageInput.value = '';
	        fileInput.value = '';
	        
    }
    
    event.preventDefault();
    
}

/**
 * 收到訊息後處理 function
 */
function onMessageReceived(payload) {
	
    var message = JSON.parse(payload.body);
    //使用<li>tag加入messageArea
    var messageElement = document.createElement('li');
    
    //分類為事件訊息及聊天訊息
    if (message.type === 'JOIN') {
    	
        messageElement.classList.add('event-message');
        message.content = message.sender + ' 上線囉';
        
    } else if (message.type === 'LEAVE') {
    	
        messageElement.classList.add('event-message');
        message.content = message.sender + ' 下線囉';
        
    //訊息排序為頭像->使用者名稱->訊息    
    } else {
    	
        messageElement.classList.add('chat-message');

        var avatarElement = getAvatarElement(message.sender);
        messageElement.appendChild(avatarElement);

        var usernameElement = getUsernameElement(message.sender);
        messageElement.appendChild(usernameElement);
        
    }
    
    //檔案處理，如果為圖片會做預覽處理
    if(message.type === 'FILE'){
    	
    	var textElement = document.createElement('a');
    	var download = document.createAttribute("download");
    	var href = document.createAttribute("href");
    	href.value=message.filePath;
    	textElement.setAttributeNode(href);
    	textElement.setAttributeNode(download);
    	
    	
    	if(message.fileType.search("image")!=-1){
    		
	    	var image = document.createElement('img');
	    	var src = document.createAttribute("src");
	    	src.value=message.filePath;
	    	image.setAttributeNode(src);
	    	image.style['width'] = "400px";
	    	image.style['height'] = "300px";
	    	textElement.appendChild(image);
	    	
    	}else{
    		
    		textElement.appendChild(document.createTextNode(message.fileName));
    		
    	}
    	
    	
    //純文字處理	
    } else {
    	
	    var textElement = document.createElement('p');
	    var messageText = document.createTextNode(message.content);
	    textElement.appendChild(messageText);
	    
    }
    
    messageElement.appendChild(document.createElement('br'));
    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
    //關閉 "上傳中.."
    $("#sending").addClass("hidden");
    //播放訊息通知聲
    $("#messageReceived")[0].play();
    
}

/**
 * 取得頭像
 */
function getAvatarElement(sender) {
	
    var avatarElement = document.createElement('i');
    var avatarText = document.createTextNode(sender[0]);
    avatarElement.appendChild(avatarText);
    avatarElement.style['background-color'] = getAvatarColor(sender);
    return avatarElement;
    
}

/**
 * 取得頭像顏色
 * 描述 : 用hash隨機選色
 */
function getAvatarColor(sender) {
	
    var hash = 0;
    for (var i = 0; i < sender.length; i++) {
        hash = 31 * hash + sender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
    
}

/**
 * 設定使用者名稱
 */
function getUsernameElement(sender) {
	
    var usernameElement = document.createElement('span');
    var usernameText = document.createTextNode(sender);
    usernameElement.appendChild(usernameText);
    return usernameElement;
    
}

//訊息發送監聽器
messageForm.addEventListener('submit', sendMessage, true)

//頁面完成後先做連線
$(document).ready(function(event){
	
	username = sessionStorage.getItem("username");
	
	getHistory();
	
	connect(event);
	
})

//擷取檔案資訊
$('#file').change(function(e){
	
	file = e.target.files[0];
	fileName = e.target.files[0].name;
	fileType = e.target.files[0].type;
	
})

//上傳檔案
$('#sendFile').click(function(){
	//防呆
	if(!file){
		
		alert("請選擇檔案再上傳");
		
	} else {
		
		let form = new FormData();
		form.append("file", file);
		form.append("username",username);
		
		$.ajax({
			
			  type : "POST",
			  url : "/sendFile",
			  cache: false,
			  processData: false,
			  contentType: false,
			  async: false,
			  data : form,
			  beforeSend : function(){
				  
				$("#sending").removeClass("hidden");
				
			  },
			  success :function(){
				  
			  },
			  complete : function(){
				  
				  sendMessage();
				  
			  },
			  
		});
		
	}
	
})