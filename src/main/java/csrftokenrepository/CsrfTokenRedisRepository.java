
/*
 * <p>文件名称: CsrfTokenRedisRepository</p>
 * <p>项目描述: KOCA 金证云原生平台 V1.0.0</p>
 * <p>公司名称: 深圳市金证科技股份有限公司</p>
 * <p>版权所有: 版权所有(C)2019-2020</p>
 */

package csrftokenrepository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * <p>一句话功能简述.</p>
 * <p>功能详细描述</p>
 *
 * @author zhangchao2
 * @version 1.0.0
 * @since 1.0.0, 2019/09/24
 */
public class CsrfTokenRedisRepository implements CsrfTokenRepository {
    private static final Logger logger = LoggerFactory.getLogger(CsrfTokenRedisRepository.class);
    private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

    private static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";

    private static final String CSRF_TOKEN_PREFIX = "spring:csrf:token";


    /**
     * 同session时间
     */
    private Long EXPIRE_SECOND = 1800L;


    private LettuceConnectionFactory lettuceConnectionFactory;

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        DefaultCsrfToken defaultCsrfToken = new DefaultCsrfToken(DEFAULT_CSRF_HEADER_NAME, DEFAULT_CSRF_PARAMETER_NAME,
                createNewToken());
        logger.info("CsrfTokenRedisRepository generateToken: [{}]", JSONObject.toJSONString(defaultCsrfToken));
        return defaultCsrfToken;
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        String sessionId = request.getRequestedSessionId();
        if (token == null) {
            if (!StringUtils.isEmpty(sessionId)) {
                delString(sessionId);
                logger.info("CsrfTokenRedisRepository saveToken but  token is null so del token sessionId:[{}]", sessionId);

            } else {
                logger.info("CsrfTokenRedisRepository saveToken but token is null and sessonId is null, do nothing");
            }
        } else {
            saveStringWithExpire(sessionId, JSON.toJSONString(token));
            logger.info("CsrfTokenRedisRepository 保存session到redis里面  并且设置有效期");
        }

    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        logger.info("CsrfTokenRedisRepository 从redis里面获取token");
        String sessionId = request.getRequestedSessionId();
        if (StringUtils.isEmpty(sessionId)) {
            return null;
        }
        String csrfTokenStr = getString(sessionId);
        if (StringUtils.isEmpty(csrfTokenStr)) {
            return null;
        }
        CsrfToken csrfToken = JSON.parseObject(csrfTokenStr, DefaultCsrfToken.class);
        return csrfToken;
    }
    public void setLettuceConnectionFactory(LettuceConnectionFactory lettuceConnectionFactory) {
        this.lettuceConnectionFactory = lettuceConnectionFactory;
    }

    private String createNewToken() {
        return UUID.randomUUID().toString();
    }



    private void saveStringWithExpire(String key, String val) {
        StringRedisConnection stringRedisConnection = new DefaultStringRedisConnection(lettuceConnectionFactory.getConnection());
        try {
            stringRedisConnection.set(decorateKey(key), val);
            stringRedisConnection.expire(decorateKey(key), EXPIRE_SECOND);
        } finally {
            stringRedisConnection.close();
        }
    }

    private String getString(String key) {
        String val = "";
        if (StringUtils.isEmpty(key)) {
            return val;
        }
        StringRedisConnection stringRedisConnection = new DefaultStringRedisConnection(lettuceConnectionFactory.getConnection());

        try {
            val = stringRedisConnection.get(decorateKey(key));
        } finally {
            stringRedisConnection.close();
        }
        return val;
    }

    private boolean delString(String key) {
        if (StringUtils.isEmpty(key)) {
            return true;
        }
        StringRedisConnection stringRedisConnection = new DefaultStringRedisConnection(lettuceConnectionFactory.getConnection());
        Long delCount = 0L;
        try {
            delCount = stringRedisConnection.del(decorateKey(key));
        } finally {
            stringRedisConnection.close();
        }
        return delCount > 0 ? true : false;
    }
    private String decorateKey(String key){
       return CSRF_TOKEN_PREFIX + ":" + key;
    }

}
