package br.com.sgi.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String ROLES = "roles";
    private static final String RESOURCE_ACCESS = "resource_access";

    private final JwtGrantedAuthoritiesConverter defaultAuthorities = new JwtGrantedAuthoritiesConverter();
    private final String resourceId;

    public CustomJwtAuthenticationConverter(String resourceId) {
        this.resourceId = resourceId;
    }

    @SuppressWarnings("unchecked")
    private static Collection<? extends GrantedAuthority> extractResourceRoles(final Jwt jwt, final String resourceId) {
        Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
        Map<String, Object> resource;
        Collection<String> resourceRoles;
        if (resourceAccess != null && (resource = (Map<String, Object>) resourceAccess.get(resourceId)) != null
            && (resourceRoles = (Collection<String>) resource.get(ROLES)) != null) {
            return resourceRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    @Override
    public AbstractAuthenticationToken convert(final Jwt source) {
        Collection<GrantedAuthority> authorities = Stream
            .concat(defaultAuthorities.convert(source).stream(),
                extractResourceRoles(source, resourceId).stream())
            .collect(Collectors.toSet());
        return new JwtAuthenticationToken(source, authorities);
    }
}
