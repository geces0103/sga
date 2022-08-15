package br.com.sgi.config;


import br.com.sgi.handlers.CustomKeycloakAuthenticationHandler;
import br.com.sgi.handlers.RestAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@KeycloakConfiguration
@Import({KeycloakSpringBootConfigResolver.class, SecurityProblemSupport.class})
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    public static final String USERS_DELETE = "/v1/users/";
    public static final String USERS_GET_USERNAME = "/v1/users/username";
    public static final String USERS_POST = "/v1/users/";
    public static final String USERS_GET_ALL = "/v1/users/all";
    public static final String USERS_UPDATE = "/v1/users/";
    public static final String USERS_GET_ORDERED = "/v1/users/ordered";
    public static final String USERS_GET_ID = "/v1/users/all";

    private final RestAccessDeniedHandler restAccessDeniedHandler;
    private final CustomKeycloakAuthenticationHandler customKeycloakAuthenticationHandler;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        SimpleAuthorityMapper grantedAuthorityMapper = new SimpleAuthorityMapper();
        grantedAuthorityMapper.setPrefix("ROLE_");

        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(grantedAuthorityMapper);
        auth.authenticationProvider(keycloakAuthenticationProvider);

    }

    //Keycloak auth exception handler
    @Bean
    @Override
    protected KeycloakAuthenticationProcessingFilter keycloakAuthenticationProcessingFilter() throws Exception {
        KeycloakAuthenticationProcessingFilter filter = new KeycloakAuthenticationProcessingFilter(
            this.authenticationManagerBean()
        );
        filter.setSessionAuthenticationStrategy(this.sessionAuthenticationStrategy());
        filter.setAuthenticationFailureHandler(customKeycloakAuthenticationHandler);
        return filter;
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(HttpSessionManager.class)
    protected HttpSessionManager httpSessionManager() {
        return new HttpSessionManager();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/swagger-ui.html")
            .antMatchers("/swagger-ui/**")
            .antMatchers("/v3/api-docs/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http
            .csrf()
            .disable()
            .exceptionHandling().accessDeniedHandler(restAccessDeniedHandler)
            .and()
            .headers()
            .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            .and()
            .frameOptions()
            .deny()
            .and()

            // Garantindo Sessão Stateless
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers().authenticated()
            .antMatchers().authenticated()
            .antMatchers(USERS_GET_ALL).hasAnyRole("user_GET_ALL")
            .antMatchers(USERS_GET_ID).hasAnyRole("user_GET_ID")
            .antMatchers(USERS_GET_ORDERED).hasAnyRole("user_GET_ORDERED")
            .antMatchers(USERS_UPDATE).hasAnyRole("user_UPDATE")
            .antMatchers(USERS_POST).hasAnyRole("user_CREATE")
            .antMatchers(USERS_DELETE).hasAnyRole("user_DELETE")
            .antMatchers(USERS_GET_USERNAME).hasAnyRole("user_GET_USERNAME")
            .and().oauth2ResourceServer().jwt()
            .jwtAuthenticationConverter(new CustomJwtAuthenticationConverter("sgi-client"));
    }
}
