package com.serpentia.config;
import com.serpentia.websocket.GameEventSubscriber;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuración de Redis para el sistema.
 * Esta clase configura el template de Redis que se utiliza para almacenar
 * las salas de juego en tiempo real. La configuración incluye serialización
 * personalizada para manejar objetos complejos como RoomDTO.
 *
 */
@Configuration
public class RedisConfig {

    /**
     * Configura el template de Redis para operaciones de cache.
     * Este bean configura cómo se serializan y deserializan los objetos
     * almacenados en Redis. La configuración está optimizada para el almacenamiento
     * de salas de juego con sus propiedades complejas.<
     *
     * @param connectionFactory Factory de conexión a Redis configurada automáticamente
     * @return RedisTemplate configurado para operaciones de cache
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configurar serialización para claves (String)
        template.setKeySerializer(new StringRedisSerializer());
        
        // Configurar serialización para valores (JSON para objetos complejos)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
    /**
     * Configura ShedLock para que use Redis como mecanismo de bloqueo distribuido.
     */
    @Bean
    public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory);
    }
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory factory, GameEventSubscriber subscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(subscriber, new ChannelTopic("game-events"));
        return container;
    }
}