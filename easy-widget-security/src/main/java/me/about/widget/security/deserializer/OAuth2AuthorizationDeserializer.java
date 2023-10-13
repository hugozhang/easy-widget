package me.about.widget.security.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link OAuth2Authorization} 反序列化
 *
 * @author: hugo.zxh
 * @date: 2023/10/13 11:38
 */
public class OAuth2AuthorizationDeserializer extends StdDeserializer<OAuth2Authorization> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public OAuth2AuthorizationDeserializer() {
        super(OAuth2Authorization.class);
    }

    @Override
    public OAuth2Authorization deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        RegisteredClient.Builder registeredClientBuilder = null;
        AuthorizationGrantType authorizationGrantType = null;
        String principalName = null;
        OAuth2AccessToken accessToken = null;
        OAuth2RefreshToken refreshToken = null;
        Map<String, Object> attributesMap = null;
        String id = null;

        JsonToken token;
        while ((token = p.nextToken()) != null) {
            if (JsonToken.FIELD_NAME.equals(token)) {
                String fieldName = p.getCurrentName();
                // 下一个值
                p.nextToken();
                switch (fieldName) {
                    case "registeredClientId":
                        registeredClientBuilder = RegisteredClient.withId(p.getText());
                        break;
                    case "authorizationGrantType":
                        authorizationGrantType = objectMapper.convertValue(
                                p.readValueAs(Map.class).values().iterator().next(), AuthorizationGrantType.class);
                        break;
                    case "principalName":
                        principalName = p.getText();
                        break;
                    case "attributes":
                        attributesMap = objectMapper.convertValue(p.readValueAsTree(),
                                new TypeReference<Map<String, Object>>() {
                                });
                        break;
                    case "accessToken":
                        accessToken = readAccessToken(p);
                        break;
                    case "refreshToken":
                        refreshToken = readRefreshToken(p);
                        break;
                    case "id":
                        id = p.getText();
                        break;
                    default:

                }
            }
        }

        Object oauth2AuthorizationRequestObj = attributesMap.get(OAuth2AuthorizationRequest.class.getName());
        @SuppressWarnings("unchecked")
        Map<String, Object> oauth2AuthorizationRequestMap = objectMapper.convertValue(oauth2AuthorizationRequestObj,
                Map.class);

        String clientId = oauth2AuthorizationRequestMap.get("clientId").toString();
        registeredClientBuilder.clientId(clientId);

        String redirectUri = oauth2AuthorizationRequestMap.get("redirectUri").toString();
        registeredClientBuilder.redirectUri(redirectUri);

        final AuthorizationGrantType finalAuthorizationGrantType = authorizationGrantType;
        registeredClientBuilder.authorizationGrantTypes(authorizationGrantTypes -> {
            authorizationGrantTypes.add(finalAuthorizationGrantType);
        });

        RegisteredClient registeredClient = registeredClientBuilder.build();

        Object principalObj = attributesMap.get(Principal.class.getName());

        OAuth2Authorization.Builder oauth2AuthorizationBuilder = OAuth2Authorization
                .withRegisteredClient(registeredClient)
                .id(id)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .principalName(principalName)
                .authorizationGrantType(authorizationGrantType)
                .attribute(OAuth2AuthorizationRequest.class.getName(), oauth2AuthorizationRequestObj)
                .attribute(Principal.class.getName(), principalObj);

        return oauth2AuthorizationBuilder.build();
    }

    private OAuth2AccessToken readAccessToken(JsonParser p) throws IOException {
        if (p.currentToken() == JsonToken.START_OBJECT) {
            Map<String, Object> accessTokenMap = objectMapper.convertValue(p.readValueAsTree(),
                    new TypeReference<Map<String, Object>>() {
                    });
            if (accessTokenMap.containsKey("token")) {
                Map<String, Object> tokenMap = objectMapper.convertValue(accessTokenMap.get("token"),
                        new TypeReference<Map<String, Object>>() {
                        });

                String tokenValue = tokenMap.get("tokenValue").toString();

                OAuth2AccessToken.TokenType tokenType = null;

                Object tokenTypeObj = tokenMap.get("tokenType");
                if (tokenTypeObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> tokenTypeMap = (Map<String, String>) tokenTypeObj;
                    String value = tokenTypeMap.get("value");
                    if (OAuth2AccessToken.TokenType.BEARER.getValue().equals(value)) {
                        tokenType = OAuth2AccessToken.TokenType.BEARER;
                    }
                }

                String issuedAtStr = tokenMap.get("issuedAt").toString();
                String expiresAtStr = tokenMap.get("expiresAt").toString();
                Object scopesObj = tokenMap.get("scopes");

                Set<String> scopes = new HashSet<>();
                if (scopesObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> scopesList = (List<String>) scopesObj;
                    scopes.addAll(scopesList);
                }

                Instant issuedAt = Instant.parse(issuedAtStr);
                Instant expiresAt = Instant.parse(expiresAtStr);

                assert tokenType != null;
                return new OAuth2AccessToken(tokenType, tokenValue, issuedAt, expiresAt, scopes);
            }
        }
        return null;
    }

    private OAuth2RefreshToken readRefreshToken(JsonParser p) throws IOException {
        if (p.currentToken() == JsonToken.START_OBJECT) {
            Map<String, Object> refreshTokenMap = objectMapper.convertValue(p.readValueAsTree(),
                    new TypeReference<Map<String, Object>>() {
                    });
            if (refreshTokenMap.containsKey("token")) {
                Map<String, String> tokenMap = objectMapper.convertValue(refreshTokenMap.get("token"),
                        new TypeReference<Map<String, String>>() {
                        });

                String tokenValue = tokenMap.get("tokenValue");

                String issuedAtStr = tokenMap.get("issuedAt");
                String expiresAtStr = tokenMap.get("expiresAt");

                Instant issuedAt = Instant.parse(issuedAtStr);
                Instant expiresAt = null;
                if (expiresAtStr != null) {
                    expiresAt = Instant.parse(expiresAtStr);
                }
                return new OAuth2RefreshToken(tokenValue, issuedAt, expiresAt);
            }
        }
        return null;
    }

}
