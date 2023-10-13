package me.about.widget.security.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.security.oauth2.server.authorization.settings.ConfigurationSettingNames.Token.*;

/**
 * {@link TokenSettings} 反序列化
 *
 * @author: hugo.zxh
 * @date: 2023/10/13 11:39
 */
public class TokenSettingsDeserializer extends StdDeserializer<TokenSettings> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public TokenSettingsDeserializer() {
        super(TokenSettings.class);
    }

    @Override
    public TokenSettings deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        TreeNode treeNode = p.getCodec().readTree(p);
        Map<String, Object> settings = settingsTreeNodeToMap(treeNode);
        accessTokenFormat(settings);
        idTokenSignatureAlgorithm(settings);
        duration(settings);

        return TokenSettings.withSettings(settings).build();
    }

    public static Map<String, Object> settingsTreeNodeToMap(TreeNode treeNode) throws JsonProcessingException {
        TreeNode settingsTreeNode = treeNode.get("settings");
        String string = settingsTreeNode.toString();
        return objectMapper.readValue(string, new TypeReference<Map<String, Object>>() {
        });
    }

    public static void accessTokenFormat(Map<String, Object> settings) {
        Object accessTokenFormatObj = settings.get(ACCESS_TOKEN_FORMAT);
        if (accessTokenFormatObj instanceof Map) {
            settings.remove(ACCESS_TOKEN_FORMAT);
            @SuppressWarnings("unchecked")
            Map<String, String> accessTokenFormatMap = (Map<String, String>) accessTokenFormatObj;
            for (String value : accessTokenFormatMap.values()) {
                settings.put(ACCESS_TOKEN_FORMAT, new OAuth2TokenFormat(value));
            }
        }
    }

    public static void idTokenSignatureAlgorithm(Map<String, Object> settings) {
        Object idTokenSignatureAlgorithmObj = settings.get(ID_TOKEN_SIGNATURE_ALGORITHM);
        if (idTokenSignatureAlgorithmObj instanceof String) {
            settings.remove(ID_TOKEN_SIGNATURE_ALGORITHM);
            String idTokenSignatureAlgorithmStr = (String) idTokenSignatureAlgorithmObj;
            settings.put(ID_TOKEN_SIGNATURE_ALGORITHM, SignatureAlgorithm.from(idTokenSignatureAlgorithmStr));
        }
    }

    public static void duration(Map<String, Object> settings) {
        List<String> list = Arrays.asList(AUTHORIZATION_CODE_TIME_TO_LIVE, ACCESS_TOKEN_TIME_TO_LIVE,
                REFRESH_TOKEN_TIME_TO_LIVE);
        for (String name : list) {
            Object object = settings.get(name);
            if (object != null) {
                if (object instanceof List) {
                    List d = (List) object;
                    long l = ((Double)d.get(1)).longValue();
                    Duration duration = Duration.ofSeconds(l);
                    settings.put(name, duration);
                }
            }
        }
    }
}
