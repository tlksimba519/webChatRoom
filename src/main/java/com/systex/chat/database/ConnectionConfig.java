package com.systex.chat.database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/*
 * connection bean 初始化設定
 */
@Configuration
@ComponentScan("com.systex.chat.database")
public class ConnectionConfig {
	
	@Bean(initMethod = "init")
	ConnectionObject conn() {
		
        return new ConnectionObject();
        
    }
	
}
