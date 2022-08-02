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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@KeycloakConfiguration
@Import({KeycloakSpringBootConfigResolver.class, SecurityProblemSupport.class})
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    public static final String SITUACAO_CONTA_DIGITAL_PARCEIRO = "/api/v1/condominio/listagem"
        + "-situacao-conta-digital-parceiro-filtro";

    public static final String CONSULTA_DADOS_CONTA_DIGITAL = "/api/v1/consulta-conta-digital/**";
    public static final String SOLICITA_CONTA_DIGITAL_V1 = "/api/v1/conta-digital/solicita-conta-digital";

    public static final String SOLICITA_CONTA_DIGITAL_V2 = "/api/v2/erp/conta-digital/solicita-conta-digital";
    public static final String SITUACAO_CONTA_DIGITAL_V2 = "/api/v2/erp/conta-digital/situacao-conta-digital";
    public static final String WEBHOOK_CONTA_DIGITAL_V2 = "/api/v2/erp/conta-digital/webhook/conta-digital";
    public static final String WEBHOOK_DESPESA_V2 = "/api/v2/erp/conta-digital/webhook/consulta-situacao-despesa";
    public static final String DESPESA_V2 = "/api/v2/erp/despesa";
    public static final String SITUACAO_DESPESA_V2 = "/consulta-situacao-despesa";
    public static final String EXTRATO_V2 = "/api/v2/erp/extrato";
    public static final String EXTRATO_LISTAGEM_V2 = "/listagem";
    public static final String EXTRATO_DIVERGENCIA_V2 = "/divergencia";

    public static final String CONFIRMA_SOLICITACAO = "/api/v1/confirma-solicitacao";
    public static final String SUBMETER_CONFIRMACAO = "/api/v1/precadastro/**";
    public static final String FINALIZA_SOLICITACAO = "/api/v1/finaliza-solicitacao";
    public static final String PROCURADORES = "/api/v1/procurador/inclui-email-procuradores";
    public static final String PROCURADORES_CONFIRMAR = "/api/v1/procurador/confirmar/**";
    public static final String EXTRATO_APP = "/api/v1/extrato";
    public static final String DOCUMENTACAO = "/api/v1/documento/inserir-documento/**";
    public static final String DESPESA = "/api/v1/despesa";

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
            .antMatchers(getUrlForbidden()).authenticated()
            .antMatchers(SITUACAO_CONTA_DIGITAL_PARCEIRO).hasAuthority("situacao_conta_digital")

            .antMatchers(CONSULTA_DADOS_CONTA_DIGITAL).hasAuthority("conta_digital")
            .antMatchers(SOLICITA_CONTA_DIGITAL_V1).hasAuthority("solicita_conta_digital")
            .antMatchers(SOLICITA_CONTA_DIGITAL_V2).hasAuthority("solicita_conta_digital")
            .antMatchers(SITUACAO_CONTA_DIGITAL_V2).hasAuthority("situacao_conta_digital")
            .antMatchers(WEBHOOK_CONTA_DIGITAL_V2).hasAuthority("situacao_conta_digital")
            .antMatchers(WEBHOOK_DESPESA_V2).hasAuthority("situacao_conta_digital")
            .antMatchers(CONFIRMA_SOLICITACAO).hasAuthority("conta_digital")
            .antMatchers(SUBMETER_CONFIRMACAO).hasAuthority("conta_digital")
            .antMatchers(PROCURADORES).hasAuthority("conta_digital")
            .antMatchers(DOCUMENTACAO).hasAuthority("conta_digital")
            .antMatchers(FINALIZA_SOLICITACAO).hasAuthority("conta_digital")
            .antMatchers(PROCURADORES_CONFIRMAR).hasAuthority("conta_digital")

            .antMatchers(DESPESA + "/consulta-situacao-despesa").hasAuthority("despesa")
            .antMatchers(DESPESA_V2 + SITUACAO_DESPESA_V2).hasAuthority("despesa")

            .antMatchers(DESPESA).hasAuthority("despesa")
            .antMatchers(DESPESA_V2).hasAuthority("despesa")

            .antMatchers(EXTRATO_APP + "/divergencia").hasAuthority("extrato")
            .antMatchers(EXTRATO_APP + "/paginacao").hasAuthority("extrato")
            .antMatchers(EXTRATO_APP).hasAuthority("extrato")

            .antMatchers(EXTRATO_V2 + EXTRATO_LISTAGEM_V2).hasAuthority("extrato")
            .antMatchers(EXTRATO_V2 + EXTRATO_DIVERGENCIA_V2).hasAuthority("extrato")

            .and().oauth2ResourceServer().jwt()
            .jwtAuthenticationConverter(new CustomJwtAuthenticationConverter("onboarding-sgi"));

    }

    private String[] getUrlForbidden() {
        List<String> paths = new ArrayList<>();
        paths.add(SITUACAO_CONTA_DIGITAL_PARCEIRO);

        paths.add(CONSULTA_DADOS_CONTA_DIGITAL);
        paths.add(SOLICITA_CONTA_DIGITAL_V1);
        paths.add(SOLICITA_CONTA_DIGITAL_V2);
        paths.add(SITUACAO_CONTA_DIGITAL_V2);
        paths.add(CONFIRMA_SOLICITACAO);
        paths.add(SUBMETER_CONFIRMACAO);
        paths.add(PROCURADORES);
        paths.add(DOCUMENTACAO);
        paths.add(PROCURADORES_CONFIRMAR);
        paths.add(FINALIZA_SOLICITACAO);
        paths.add(DESPESA + "/consulta-situacao-despesa");
        paths.add(DESPESA_V2);
        paths.add(DESPESA_V2 + SITUACAO_DESPESA_V2);

        //paths.add(DESPESA);
        paths.add(EXTRATO_APP + "/divergencia");
        paths.add(EXTRATO_APP + "/paginacao");
        paths.add(EXTRATO_APP);

        paths.add(EXTRATO_V2);
        paths.add(EXTRATO_V2 + EXTRATO_LISTAGEM_V2);
        paths.add(EXTRATO_V2 + EXTRATO_DIVERGENCIA_V2);

        return paths.toArray(String[]::new);

    }
}
