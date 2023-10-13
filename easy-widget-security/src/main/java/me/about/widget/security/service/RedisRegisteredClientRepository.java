package me.about.widget.security.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.concurrent.TimeUnit;

/**
 * redis 注册 client
 *
 * @author: hugo.zxh
 * @date: 2023/10/13 11:53
 */
public class RedisRegisteredClientRepository implements RegisteredClientRepository {

    private RedisTemplate<String, RegisteredClient> redisTemplate;

    private JdbcRegisteredClientRepository jdbcRegisteredClientRepository;

    /**
     * 根据 id 查询时放入Redis中的部分 key
     */
    public static final String REGISTERED_CLIENT_ID = "registered_client:id:";

    /**
     * 根据 clientId 查询时放入Redis中的部分 key
     */
    public static final String REGISTERED_CLIENT_CLIENT_ID = "registered_client:clientId:";


    public RedisRegisteredClientRepository(RedisTemplate<String, RegisteredClient> redisTemplate, JdbcRegisteredClientRepository jdbcRegisteredClientRepository) {
        this.redisTemplate = redisTemplate;
        this.jdbcRegisteredClientRepository = jdbcRegisteredClientRepository;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        registeredClientByIdToRedis(registeredClient);
        registeredClientByClientIdToRedis(registeredClient);
    }

    private void registeredClientByIdToRedis(RegisteredClient registeredClient) {
        redisTemplate.opsForValue().set(REGISTERED_CLIENT_ID + registeredClient.getId(), registeredClient, 5, TimeUnit.HOURS);
    }

    private void registeredClientByClientIdToRedis(RegisteredClient registeredClient) {
        redisTemplate.opsForValue().set(REGISTERED_CLIENT_CLIENT_ID + registeredClient.getId(), registeredClient, 5, TimeUnit.HOURS);
    }

    @Override
    public RegisteredClient findById(String id) {
        RegisteredClient registeredClient = redisTemplate.opsForValue().get(REGISTERED_CLIENT_ID + id);
        if (registeredClient == null) {
            RegisteredClient registeredClientDb = jdbcRegisteredClientRepository.findById(id);
            if (registeredClientDb != null) {
                registeredClientByIdToRedis(registeredClientDb);
            }
            return registeredClientDb;
        }
        return registeredClient;
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        RegisteredClient registeredClient = redisTemplate.opsForValue().get(REGISTERED_CLIENT_CLIENT_ID + clientId);
        if (registeredClient == null) {
            RegisteredClient registeredClientDb = jdbcRegisteredClientRepository.findByClientId(clientId);
            if (registeredClientDb != null) {
                registeredClientByClientIdToRedis(registeredClientDb);
            }
            return registeredClientDb;
        }
        return registeredClient;
    }
}
