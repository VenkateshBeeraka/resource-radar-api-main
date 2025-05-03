package com.resourceradar.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    @Autowired
    TokenService tokenService;

    // read the clientId from @value annotation from properties file

    @Bean
    public AuthenticationEntryPoint myAuthenticationEntryPoint() {
        return new EntryPoint();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				auth -> auth
						.requestMatchers(new AntPathRequestMatcher("/api/v1/social"),
								new AntPathRequestMatcher("/api/v1/employee/create"),
								new AntPathRequestMatcher("/api/v1/employee/reportingmanagers"),
								new AntPathRequestMatcher("/api/v1/departments"),
								new AntPathRequestMatcher("/api/v1/skills"),
								new AntPathRequestMatcher("/api/v1/applicationrole"),
								new AntPathRequestMatcher("/api/v1/designations"),
								new AntPathRequestMatcher("/api/v1/practices"))
						.permitAll().anyRequest().authenticated())
				.exceptionHandling();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authenticationProvider(new GoogleAuthProvider()).exceptionHandling()
				.authenticationEntryPoint(myAuthenticationEntryPoint());
//     ((request, response, e) -> {
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setContentType("application/json");
//            response.getWriter().write("{ \r\n"
//                 + "  \"Error message\": \"You are not authenticated/Token Expired\",\r\n"
//                 + "  \"Status\": \"False\",\r\n"
//                 + "  \"Status code\": \"401\"\r\n"
//                 + "}" );
//        });


        http.apply(MyCustomDsl.customDsl(tokenService));
        http.csrf().disable();
        return http.build();
    }

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/api/v1/employee/create", "/api/v1/employee/reportingmanagers",
				"/api/v1/departments", "/api/v1/skills", "/api/v1/applicationrole", "/api/v1/designations",
				"/api/v1/practices", "/v2/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs/**",
				"/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/swagger-resources", "/v3/api-docs/**",
				"/proxy/**");
	}
}
