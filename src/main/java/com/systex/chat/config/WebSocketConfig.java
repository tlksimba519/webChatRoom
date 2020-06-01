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
        
        endpointRegistry.addEndpoint("/chatroom").withSockJS(); 
    
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry brokerRegister) {
        
        brokerRegister.enableSimpleBroker("/topic");
        
        brokerRegister.setApplicationDestinationPrefixes("/app");
    
    }

}