package com.universidad.seguridad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    @Bean
    public AuthenticationProvider authProvider(
        UserDetailsService userDetailsService, 
        PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
    
    return provider;
}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider authProvider) throws Exception {
        http.authenticationProvider(authProvider)
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/", "/login", "/registro", "/css/**",
                        "/js/**").permitAll().requestMatchers("/admin/**").hasRole("ADMIN").anyRequest()
                        .authenticated())
                .formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true").permitAll())
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));

        return http.build();
    }
}
