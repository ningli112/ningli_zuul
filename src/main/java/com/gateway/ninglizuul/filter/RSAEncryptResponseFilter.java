package com.gateway.ninglizuul.filter;

import java.security.PrivateKey;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSON;
import com.gateway.ninglizuul.config.filter.EncryptFilterConfig;
import com.gateway.ninglizuul.dto.BaseParamDTO;
import com.gateway.ninglizuul.dto.CommonParamDTO;
import com.gateway.ninglizuul.enums.filter.FilterEnum;
import com.gateway.ninglizuul.exception.encryption.EncryptException;
import com.gateway.ninglizuul.exception.encryption.SecretKeyGenerationException;
import com.gateway.ninglizuul.exception.encryption.SignException;
import com.gateway.ninglizuul.util.encryption.MD5Util;
import com.gateway.ninglizuul.util.encryption.RSAEncryptUtil;
import com.gateway.ninglizuul.wrapper.BodyReaderHttpServletRequestWrapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 
 * @ClassName: RSAEncryptResponseFilter
 * @Description:RSA响应加密过滤器
 * @author: 宁黎
 * @date: 2019年5月10日 上午11:04:57
 */
public class RSAEncryptResponseFilter extends ZuulFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RSAEncryptResponseFilter.class);
	@Autowired
	private EncryptFilterConfig encryptFilterConfig;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 
	 * <p>
	 * Title: shouldFilter
	 * </p>
	 * <p>
	 * Description: 这里可以写逻辑判断，是否要过滤，true为过滤
	 * </p>
	 * 
	 * @author 宁黎
	 * 
	 * @return
	 * @see com.netflix.zuul.IZuulFilter#shouldFilter()
	 */
	@Override
	public boolean shouldFilter() {
		// 获取请求上下文
		RequestContext requestContext = RequestContext.getCurrentContext();
		if (requestContext.getBoolean(FilterEnum.RSA_RESPONSE_FILTER.getKey())) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public Object run() {
		RequestContext requestContext = RequestContext.getCurrentContext();
		// 获取响应体
		String responseBody = BodyReaderHttpServletRequestWrapper.getResponseBody(requestContext);
		// 私钥存放的文件路径
		String path = encryptFilterConfig.getPrivateKey();
		// 从配置中获取私钥
		String privateKeyStr = null;
		// 用私钥字符生成私钥对象
		PrivateKey privateKey = null;
		// 存放密文
		String cipherText = null;
		// 用来存放RSA签名
		String sign = null;
		// 使用UUID生成accesToken
		String accessToken = MD5Util.getUUID();
		// 对称加密秘钥
		String password = MD5Util.getUUID();
		// 设置工厂传输数据,此对象里面包含业务传输数据
		CommonParamDTO commonParamDTO = new CommonParamDTO(password, responseBody);
		String plainText = JSON.toJSONString(commonParamDTO);
		try {
			privateKeyStr = RSAEncryptUtil.loadPrivateKeyByFile(path);
			privateKey = RSAEncryptUtil.loadPrivateKeyStr(privateKeyStr);
			// 对返回的业务参数进行签名
			sign = RSAEncryptUtil.sign(new StringBuffer(plainText).append(accessToken).toString(), privateKey);
			cipherText = RSAEncryptUtil.encrypt(privateKey, plainText);
		} catch (SecretKeyGenerationException e) {
			LOGGER.error(e.getMessage());
		} catch (EncryptException e) {
			LOGGER.error(e.getMessage());
		} catch (SignException e) {
			LOGGER.error(e.getMessage());
		}
		BaseParamDTO baseParamDTO = new BaseParamDTO(cipherText, accessToken, sign);
		String responseStr = JSON.toJSONString(baseParamDTO);
		HttpServletResponse response = requestContext.getResponse();
		response.setContentType(FilterEnum.JSON_CONTENT_TYPE.getKey());
		response.setCharacterEncoding(RSAEncryptUtil.CHARSET_UTF8);
		requestContext.setResponseBody(responseStr);
		// 缓存对称秘钥
		redisTemplate.opsForValue().set(accessToken, password);
		return null;
	}

	/**
	 * 
	 * <p>
	 * Title: filterType
	 * </p>
	 * <p>
	 * Description: pre：路由之前
	 * routing：路由之时
	 * post： 路由之后
	 * error：发送错误调用
	 * </p>
	 * 
	 * @author 宁黎
	 * @return
	 * @see com.netflix.zuul.ZuulFilter#filterType()
	 */
	@Override
	public String filterType() {
		return "post";
	}

	/**
	 * 
	 * <p>
	 * Title: filterOrder
	 * </p>
	 * <p>
	 * Description:数值越大优先级越小
	 * </p>
	 * 
	 * @author 宁黎
	 * @return
	 * @see com.netflix.zuul.ZuulFilter#filterOrder()
	 */
	@Override
	public int filterOrder() {
		return 0;
	}

}
