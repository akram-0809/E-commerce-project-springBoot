package com.jtspringproject.JtSpringProject.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.repository.UserRepository;

@Configuration
public class SecurityConfiguration {

	@Bean
	@Order(1)
	SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/admin/**")
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(new AntPathRequestMatcher("/admin/login")).permitAll()
				.requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
			)
			.formLogin(form -> form
				.loginPage("/admin/login")
				.loginProcessingUrl("/admin/loginvalidate")
				.successHandler((request, response, authentication) -> {
					response.sendRedirect("/admin/");
				})
				.failureHandler((request, response, exception) -> {
					response.sendRedirect("/admin/login?error=true");
				})
			)
			.logout(logout -> logout
				.logoutUrl("/admin/logout")
				.logoutSuccessUrl("/admin/login")
				.deleteCookies("JSESSIONID")
			)
			.exceptionHandling(ex -> ex
				.accessDeniedPage("/403")
			)
			.csrf(csrf -> csrf.disable());

		return http.build();
	}

	@Bean
	@Order(2)
	SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
			.requestMatchers("/login", "/register", "/newuserregister", "/test", "/test2").permitAll()
			.anyRequest().hasRole("USER")
		)
		.formLogin(form -> form
			.loginPage("/login")
			.loginProcessingUrl("/userloginvalidate")
			.successHandler((request, response, authentication) -> {
				response.sendRedirect("/");
			})
			.failureHandler((request, response, exception) -> {
				response.sendRedirect("/login?error=true");
			})
		)
		.logout(logout -> logout
			.logoutUrl("/logout")
			.logoutSuccessUrl("/login")
			.deleteCookies("JSESSIONID")
		)
		.exceptionHandling(ex -> ex
			.accessDeniedPage("/403")
		)
		.csrf(csrf -> csrf.disable());

		return http.build();
	}

	@Bean
	UserDetailsService userDetailsService(UserRepository userRepository) {
		return username -> {
			User user = userRepository.findByUsername(username);
			if (user == null) {
				throw new UsernameNotFoundException("User with username " + username + " not found.");
			}
			String role = user.getRole().equals("ROLE_ADMIN") ? "ADMIN" : "USER";

			return org.springframework.security.core.userdetails.User
				.withUsername(username)
				.password(user.getPassword())
				.roles(role)
				.build();
		};
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}