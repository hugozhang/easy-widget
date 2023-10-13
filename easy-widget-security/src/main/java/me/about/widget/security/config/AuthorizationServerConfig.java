package me.about.widget.security.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import me.about.widget.security.service.JdbcOAuth2AuthorizationService;
import me.about.widget.security.service.RedisOAuth2AuthorizationService;
import me.about.widget.security.service.RedisRegisteredClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 资源授权配置
 *
 * @author: hugo.zxh
 * @date: 2023/10/13 11:25
 */
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    private Logger logger = LoggerFactory.getLogger(AuthorizationServerConfig.class);

    @Resource
    private RedisTemplate<String, RegisteredClient> redisTemplateRegisteredClient;

    @Resource
    private RedisTemplate<String, OAuth2Authorization> redisTemplateOAuth2Authorization;

    @Resource
    private DataSource dataSource;

    @Resource
    private AuthenticationEntryPoint delegatedAuthenticationEntryPoint;

    @Value("${oauth2.open.api.antPatterns}")
    private String oauth2OpenApiAntPatterns;

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        authorizationServerConfigurer
                .tokenEndpoint(endpointConfigurer -> endpointConfigurer
                        .accessTokenResponseHandler((request, response, authentication) -> {
                            if (authentication instanceof OAuth2AccessTokenAuthenticationToken) {
                                OAuth2AccessTokenAuthenticationToken oAuth2AccessTokenAuthenticationToken = (OAuth2AccessTokenAuthenticationToken) authentication;
                                response.setCharacterEncoding("UTF-8");
                                response.setContentType("application/json; charset=utf-8");
                                PrintWriter out = null;

                                OAuth2AccessToken accessToken = oAuth2AccessTokenAuthenticationToken.getAccessToken();
                                Map<String,Object> outPut = new HashMap<>();
                                outPut.put("access_token",accessToken.getTokenValue());
                                outPut.put("token_type",accessToken.getTokenType().getValue());

                                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                String expiresAt = dateFormat.format(ZonedDateTime.ofInstant( accessToken.getExpiresAt(), ZoneId.systemDefault()).toLocalDateTime());
                                outPut.put("expires_at",expiresAt);

                                try {
                                    out = response.getWriter();
//                                    out.append(JSONUtil.toJsonStr(Result.success(outPut)));
                                    out.flush();
                                } catch (IOException e) {
                                    logger.error(e.getMessage(),e);
                                } finally {
                                    if (out != null) {
                                        out.close();
                                    }
                                }
                            } else {
                                logger.info(authentication.getClass().getName());
                            }
                        })
                        .errorResponseHandler((request, response, exception) -> {
                            exceptionHandler(request,response, exception);
                        })
                )
                .clientAuthentication(clientAuthenticationConfigurer ->
                        clientAuthenticationConfigurer.errorResponseHandler((request, response, exception) -> {
                            exceptionHandler(request,response, exception);
                        })
        );

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        http
                .requestMatcher(endpointsMatcher)
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .apply(authorizationServerConfigurer);
        return http.formLogin(Customizer.withDefaults()).build();
    }

    private void exceptionHandler(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        logger.info(request.getMethod());
        logger.error(exception.getMessage(),exception);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();

            out.flush();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {

        SimpleAccessDeniedHandler accessDeniedHandler = new SimpleAccessDeniedHandler();

        http.csrf().disable()
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.antMatchers(oauth2OpenApiAntPatterns).authenticated().anyRequest().permitAll()
                )

                // 异常处理
                .exceptionHandling(exceptionConfigurer -> exceptionConfigurer
                        // 拒绝访问
                        .accessDeniedHandler(accessDeniedHandler)
                        // 认证失败
                        .authenticationEntryPoint(delegatedAuthenticationEntryPoint)
                )
                // 资源服务
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(delegatedAuthenticationEntryPoint)
                        .jwt()
                );
        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        JdbcRegisteredClientRepository jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        RegisteredClientRepository registeredClientRepository = new RedisRegisteredClientRepository(redisTemplateRegisteredClient,jdbcRegisteredClientRepository);
        return registeredClientRepository;
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        JdbcOAuth2AuthorizationService jdbcOAuth2AuthorizationService = new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
        return new RedisOAuth2AuthorizationService(redisTemplateOAuth2Authorization,jdbcOAuth2AuthorizationService);
    }

//    @Bean
//    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
//        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
//    }


    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    private static RSAKey generateRsa() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String jwtIssuerUri;

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(jwtIssuerUri)
                .build();
    }
}
