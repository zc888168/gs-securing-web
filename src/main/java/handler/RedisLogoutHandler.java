/*

*/
/*
 * <p>文件名称: RedisLogoutHandler</p>
 * <p>项目描述: KOCA 金证云原生平台 V1.0.0</p>
 * <p>公司名称: 深圳市金证科技股份有限公司</p>
 * <p>版权所有: 版权所有(C)2019-2020</p>
 *//*


package handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

*/
/**
 * <p>一句话功能简述.</p>
 * <p>功能详细描述</p>
 *
 * @author zhangchao2
 * @version 1.0.0
 * @since 1.0.0, 2019/09/25
 *//*

public class RedisLogoutHandler implements LogoutHandler {
    private static final Logger logger = LoggerFactory.getLogger(RedisLogoutHandler.class);
    private RedisTemplate redisTemplate;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private static final String HASH_KEY = "spring:session:sessions:";

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String sessionId = request.getRequestedSessionId();
        //todo delete keys *
        boolean result = removeHashKey(sessionId);
        logger.info("delete reust [{}]" ,result);

        //todo del set
//        String redisKey = getExpirationKey(roundUpToNextMinute(expiresInMillis(
//                request.getSession().getMaxInactiveInterval(), request.getSession().getLastAccessedTime().)));
        try {
            redirectStrategy.sendRedirect(request,response, "/");
        } catch (IOException e) {
            logger.error("RedisLogoutHandler error ", e);
        }
    }


    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

    }

    private boolean removeHashKey(String key){
       long num =  redisTemplate.boundHashOps(HASH_KEY).delete( key);
       return num > 0 ? true : false;

    }
    private boolean removeStringKey(String key) {
       return  redisTemplate.delete(key);
    }

    private long roundUpToNextMinute(long timeInMs) {

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timeInMs);
        date.add(Calendar.MINUTE, 1);
        date.clear(Calendar.SECOND);
        date.clear(Calendar.MILLISECOND);
        return date.getTimeInMillis();
    }
    private  String getExpirationKey(long expires) {
        return "expires:" + expires;
    }
}
*/
