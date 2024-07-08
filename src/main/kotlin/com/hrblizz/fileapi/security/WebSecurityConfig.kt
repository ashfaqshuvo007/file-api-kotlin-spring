package com.hrblizz.fileapi.security

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
internal class WebSecurityConfig(
    private val apiAuthenticationEntryPoint: ApiAuthenticationEntryPoint,
    private val apiAuthenticationProvider: ApiAuthenticationProvider
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(apiAuthenticationProvider)
    }

    override fun configure(http: HttpSecurity) {
        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .httpBasic().authenticationEntryPoint(apiAuthenticationEntryPoint)
            .and()
            .csrf().disable()

        val authorizationConfigurer = http.authorizeRequests()
        authorizationConfigurer.antMatchers("/docs", "/docs/*").permitAll()

        authorizationConfigurer
            .antMatchers(HttpMethod.GET,
                "/status", "/webjars/**", "/favicon.ico").permitAll()
            .antMatchers(HttpMethod.POST,
                "/files", "/webjars/**", "/favicon.ico").authenticated()
            .antMatchers(HttpMethod.GET,
                "/file/{token}", "/webjars/**", "/favicon.ico").authenticated()
            .antMatchers(HttpMethod.POST,
                "/files/metas", "/webjars/**", "/favicon.ico").authenticated()
            .antMatchers(HttpMethod.DELETE,
                "/file/{token}", "/webjars/**", "/favicon.ico").authenticated()
            .anyRequest().fullyAuthenticated()
    }
}
