package fr.polytech.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO (GLOBAL): remove logger

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    public JwtAuthConverter() {
        this.jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    }

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthConverter.class);

    private final String principleAttribute = "preferred_username"; // TODO: Env variable
    private final String resourceId = "seasonsforce-client"; // TODO: Env variable

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        logger.info("JwtAuthConverter.convert");
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                getPrincipleClaimName(jwt)
        );
    }

    private String getPrincipleClaimName(Jwt jwt) {
        return jwt.getClaim(principleAttribute);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRoles;
        logger.info("JwtAuthConverter.extractResourceRoles");
        if (jwt.getClaim("resource_access") == null) {
            logger.info("resource_access is null");
            return Set.of();
        }
        resourceAccess = jwt.getClaim("resource_access");

        logger.info("resourceAccess: " + resourceAccess);
        if (resourceAccess.get(resourceId) == null) {
            logger.info("resourceId is null");
            return Set.of();
        }
        resource = (Map<String, Object>) resourceAccess.get(resourceId);

        logger.info("resource: " + resource);

        resourceRoles = (Collection<String>) resource.get("roles");
        Collection<? extends GrantedAuthority> roles = resourceRoles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
        logger.info("roles: " + roles);
        return roles;
    }
}