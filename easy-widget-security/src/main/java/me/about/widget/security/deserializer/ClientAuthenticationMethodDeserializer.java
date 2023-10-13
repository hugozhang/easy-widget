package me.about.widget.security.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.io.IOException;
import java.util.Map;

/**
 * {@link ClientAuthenticationMethod} 反序列化
 *
 * @author: hugo.zxh
 * @date: 2023/10/13 11:34
 */
public class ClientAuthenticationMethodDeserializer extends StdDeserializer<ClientAuthenticationMethod> {

    public final ObjectMapper objectMapper = new ObjectMapper();

    public ClientAuthenticationMethodDeserializer() {
        super(ClientAuthenticationMethod.class);
    }

    @Override
    public ClientAuthenticationMethod deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JacksonException {
        Map<String, String> map = objectMapper.readValue(p, new TypeReference<Map<String, String>>() {
        });
        return new ClientAuthenticationMethod(map.values().stream().findFirst().orElse(null));
    }

}
