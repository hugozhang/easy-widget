package me.about.widget.security.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2023/10/13 11:53
 */
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    /**
     * 根据 id 查询时放入Redis中的部分 key
     */
    public static final String OAUTH2_AUTHORIZATION_ID = "oauth2_authorization:id:";

    public static final String OAUTH2_AUTHORIZATION_TOKEN_TYPE = "oauth2_authorization:tokenType:";

    private RedisTemplate<String, OAuth2Authorization> redisTemplate;

    private JdbcOAuth2AuthorizationService jdbcOAuth2AuthorizationService;

    public RedisOAuth2AuthorizationService(RedisTemplate<String,OAuth2Authorization> redisTemplate, JdbcOAuth2AuthorizationService jdbcOAuth2AuthorizationService) {
        this.redisTemplate = redisTemplate;
        this.jdbcOAuth2AuthorizationService = jdbcOAuth2AuthorizationService;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        redisTemplate.opsForValue().set(OAUTH2_AUTHORIZATION_ID + authorization.getId(), authorization, 5, TimeUnit.HOURS);
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getAccessToken();
        if (accessToken != null) {
            OAuth2AccessToken token = accessToken.getToken();
            if (token != null) {
                String tokenValue = token.getTokenValue();
                redisTemplate.opsForValue().set(OAUTH2_AUTHORIZATION_TOKEN_TYPE + OAuth2TokenType.ACCESS_TOKEN.getValue() + ":" + tokenValue, authorization, 5, TimeUnit.HOURS);
            }
        }

        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getRefreshToken();
        if (refreshToken != null) {
            OAuth2RefreshToken token = refreshToken.getToken();
            if (token != null) {
                String tokenValue = token.getTokenValue();
                redisTemplate.opsForValue().set(OAUTH2_AUTHORIZATION_TOKEN_TYPE + OAuth2TokenType.REFRESH_TOKEN.getValue() + ":" + tokenValue, authorization, 5, TimeUnit.HOURS);
            }
        }

        jdbcOAuth2AuthorizationService.remove(authorization);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        redisTemplate.delete(OAUTH2_AUTHORIZATION_ID + authorization.getId());
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getAccessToken();
        if (accessToken != null) {
            OAuth2AccessToken token = accessToken.getToken();
            String tokenValue = token.getTokenValue();
            redisTemplate.delete(OAUTH2_AUTHORIZATION_TOKEN_TYPE + OAuth2TokenType.ACCESS_TOKEN.getValue() + ":" + tokenValue);
        }

        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getRefreshToken();
        if (refreshToken != null) {
            OAuth2RefreshToken token = refreshToken.getToken();
            String tokenValue = token.getTokenValue();
            redisTemplate.delete(OAUTH2_AUTHORIZATION_TOKEN_TYPE + OAuth2TokenType.REFRESH_TOKEN.getValue() + ":" + tokenValue);
        }
    }

    @Override
    public OAuth2Authorization findById(String id) {
        return redisTemplate.opsForValue().get(OAUTH2_AUTHORIZATION_ID + id);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        assert tokenType != null;
        String tokenTypeValue = tokenType.getValue();
        OAuth2Authorization oauth2AuthorizationByRedis = redisTemplate.opsForValue().get(OAUTH2_AUTHORIZATION_TOKEN_TYPE + tokenTypeValue + ":" + token);
        return oauth2AuthorizationByRedis;
    }
}
