package me.about.widget.security.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import me.about.widget.security.deserializer.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * {@link ObjectMapper} 工具类
 *
 * @author: hugo.zxh
 * @date: 2023/10/13 11:32
 */
public class ObjectMapperUtils {

    public static ObjectMapper redis() {
        // ObjectMapper 提供了从基本 POJO（普通旧Java对象）或从通用 JSON 树模型（{@link JsonNode}）读取和写入 JSON的功能以及执行转换的相关功能。
        ObjectMapper objectMapper = new ObjectMapper();
        // {@link https://github.com/FasterXML/jackson-databind/issues/2838}
        objectMapper.activateDefaultTyping(new DefaultBaseTypeLimitingValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        // 枚举，定义影响Java对象序列化方式的简单开/关功能。
        // 默认情况下启用功能，因此默认情况下日期/时间序列化为时间戳。
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 如果启用，上下文<code> TimeZone </
        // code>将基本上覆盖任何其他TimeZone信息;如果禁用，则仅在值本身不包含任何TimeZone信息时使用。
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        // 注册使用Jackson核心序列化{@code java.time}对象的功能的类。
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 添加序列化
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));

        // 添加反序列化
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));

        // 添加 OAuth 2.1 的反序列化
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(ClientAuthenticationMethod.class, new ClientAuthenticationMethodDeserializer());
        simpleModule.addDeserializer(AuthorizationGrantType.class, new AuthorizationGrantTypeDeserializer());
        simpleModule.addDeserializer(ClientSettings.class, new ClientSettingsDeserializer());
        simpleModule.addDeserializer(TokenSettings.class, new TokenSettingsDeserializer());
        simpleModule.addDeserializer(OAuth2Authorization.class, new OAuth2AuthorizationDeserializer());
        simpleModule.addDeserializer(OAuth2AuthorizationConsent.class, new OAuth2AuthorizationConsentDeserializer());

        // 用于注册可以扩展该映射器提供的功能的模块的方法; 例如，通过添加自定义序列化程序和反序列化程序的提供程序。
        objectMapper.registerModules(javaTimeModule, simpleModule);

        return objectMapper;
    }

}
