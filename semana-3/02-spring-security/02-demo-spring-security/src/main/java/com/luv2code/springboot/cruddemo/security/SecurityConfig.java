package com.luv2code.springboot.cruddemo.security;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class SecurityConfig {

    /**
     * Se usa en  /api/auth (login) y /api/basic.
     * La cadena /api/oauth2 no lo usa ya que ahi los usuarios viven en Keycloak.
     */
    @Bean
    public UserDetailsService userDetailsService(DataSource theDataSource) {

        JdbcUserDetailsManager theUserDetailsManager = new JdbcUserDetailsManager(theDataSource);

        theUserDetailsManager.setUsersByUsernameQuery(
                "select user_id, pw, active from members where user_id=?");

        theUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select user_id, role from roles where user_id=?");

        return theUserDetailsManager;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/api/auth/**");
        http.authorizeHttpRequests(configurer -> configurer.anyRequest().authenticated());
        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain basicFilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/api/basic/**");
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers(HttpMethod.GET,    "/api/basic/employees").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.GET,    "/api/basic/employees/**").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.POST,   "/api/basic/employees").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PUT,    "/api/basic/employees").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PATCH,  "/api/basic/employees/**").hasRole("MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/basic/employees/**").hasRole("ADMIN")
                .anyRequest().authenticated());

        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/api/jwt/**");
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers(HttpMethod.GET,    "/api/jwt/employees").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.GET,    "/api/jwt/employees/**").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.POST,   "/api/jwt/employees").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PUT,    "/api/jwt/employees").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PATCH,  "/api/jwt/employees/**").hasRole("MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/jwt/employees/**").hasRole("ADMIN")
                .anyRequest().authenticated());

        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(localJwtDecoder())
                             .jwtAuthenticationConverter(jwtRolesFromClaim())));

        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    @Order(4)
    public SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/api/oauth2/**");
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers(HttpMethod.GET,    "/api/oauth2/employees").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.GET,    "/api/oauth2/employees/**").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.POST,   "/api/oauth2/employees").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PUT,    "/api/oauth2/employees").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PATCH,  "/api/oauth2/employees/**").hasRole("MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/oauth2/employees/**").hasRole("ADMIN")
                .anyRequest().authenticated());


        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakConverter())));

        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    @Order(5)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configurer -> configurer.anyRequest().authenticated());
        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }


    @Value("${rsa.public-key}")
    private RSAPublicKey publicKey;

    @Value("${rsa.private-key}")
    private RSAPrivateKey privateKey;

    /**
     * FIRMAR tokens. Usa la llave PRIVADA: solo este servidor puede emitir tokens validos.
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    private JwtDecoder localJwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    private JwtAuthenticationConverter jwtRolesFromClaim() {

        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return converter;
    }

    private Converter<Jwt, AbstractAuthenticationToken> keycloakConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(SecurityConfig::extraerRoles);

        return converter;
    }

    @SuppressWarnings("unchecked")
    private static Collection<GrantedAuthority> extraerRoles(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null || realmAccess.get("roles") == null) {
            return List.of();
        }

        List<String> roles = (List<String>) realmAccess.get("roles");

        return roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
