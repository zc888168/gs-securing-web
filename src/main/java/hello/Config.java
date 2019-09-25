package hello;

import handler.RedisLogoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
public class Config {

	@Bean
	public LettuceConnectionFactory connectionFactory() {
		return new LettuceConnectionFactory("127.0.0.1", 6379);
	}

	@Bean
	public RedisLogoutHandler getRedisLogoutHandler(LettuceConnectionFactory lettuceConnectionFactory){
		RedisLogoutHandler redisLogoutHandler = new RedisLogoutHandler();
		redisLogoutHandler.setLettuceConnectionFactory(lettuceConnectionFactory);
		return redisLogoutHandler;
	}
}