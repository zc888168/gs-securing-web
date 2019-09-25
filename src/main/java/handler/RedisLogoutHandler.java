
/*
 * <p>文件名称: RedisLogoutHandler</p>
 * <p>项目描述: KOCA 金证云原生平台 V1.0.0</p>
 * <p>公司名称: 深圳市金证科技股份有限公司</p>
 * <p>版权所有: 版权所有(C)2019-2020</p>
 */

package handler;

import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>一句话功能简述.</p>
 * <p>功能详细描述</p>
 *
 * @author zhangchao2
 * @version 1.0.0
 * @since 1.0.0, 2019/09/25
 */
public class RedisLogoutHandler implements LogoutHandler {
    private LettuceConnectionFactory lettuceConnectionFactory;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

    }


    public void setLettuceConnectionFactory(LettuceConnectionFactory lettuceConnectionFactory) {
        this.lettuceConnectionFactory = lettuceConnectionFactory;
    }

    private boolean removeStringKey(String key) {
        RedisConnection redisConnection = lettuceConnectionFactory.getConnection();
        try {
            redisConnection.del(key.getBytes());
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            redisConnection.close();
        }
    }
}
