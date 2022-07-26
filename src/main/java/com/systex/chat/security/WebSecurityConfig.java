package com.systex.chat.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public PasswordEncoder passwordEncoder() {
		// 使用BCrypt加密
		return new BCryptPasswordEncoder();
	}

	@Autowired
	CookieFilter cookieFilter;

	@Autowired
	CustomSuccessHandler successHandler;

	@Autowired
	private AuthenticationProvider provider;

	// Spring Security 登入驗證流程及參數設定
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/loginpage", "/loginFail", "/signup", "/signup.html", "/css", "/error.html").permitAll()
				.anyRequest().authenticated()
				.and()
				.csrf().disable()
				.formLogin()
				.usernameParameter("UserName").passwordParameter("Password")
				.loginPage("/loginpage")
				.loginProcessingUrl("/login")
				.defaultSuccessUrl("/main.html", true)
				.failureUrl("/loginFail")
				.successHandler(successHandler)
				.and()
				.addFilterBefore(cookieFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(provider);
	}

	// 靜態資源存取加入白名單
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/**/*.js", "/lang/*.json", "/**/*.css", "/**/*.js", "/**/*.map", "/**/*.html",
				"/**/*.png", "/**/*.jpg");

	}

}
