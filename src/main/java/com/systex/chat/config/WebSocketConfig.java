package com.systex.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
   
    @Override
    public void registerStompEndpoints(StompEndpointRegistry endpointRegistry) {
        
    	//註冊一個Client to Server的路由節點
        endpointRegistry.addEndpoint("/chatroom").withSockJS(); 
    
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry brokerRegister) {
        
    	//啟用一個訊息代理並設定路由前綴
        brokerRegister.enableSimpleBroker("/topic");
        
        //設定訊息發送給Controller的路由前綴
        brokerRegister.setApplicationDestinationPrefixes("/app");
    
    }

}