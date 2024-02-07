package pro.akosarev.sandbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.stream.Stream;

@Configuration
public class SecurityBeans {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(configurer -> configurer.anyExchange().authenticated())
                .oauth2ResourceServer(customizer -> customizer.jwt(jwt -> {
                    ReactiveJwtAuthenticationConverter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
                    jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");

                    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

                    JwtGrantedAuthoritiesConverter customJwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                    customJwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("groups");
                    customJwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

                    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                            new ReactiveJwtGrantedAuthoritiesConverterAdapter(token ->
                                    Stream.concat(jwtGrantedAuthoritiesConverter.convert(token).stream(),
                                                    customJwtGrantedAuthoritiesConverter.convert(token).stream())
                                            .toList()));

                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter);
                }))
                .build();
    }
}
