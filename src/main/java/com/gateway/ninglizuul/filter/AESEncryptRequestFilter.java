package com.gateway.ninglizuul.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import com.gateway.ninglizuul.config.filter.EncryptFilterConfig;
import com.gateway.ninglizuul.dto.BaseParamDTO;
import com.gateway.ninglizuul.enums.code.ClientErrorRequestEnum;
import com.gateway.ninglizuul.enums.filter.FilterEnum;
import com.gateway.ninglizuul.exception.encryption.DecrypteException;
import com.gateway.ninglizuul.exception.encryption.SignException;
import com.gateway.ninglizuul.util.encryption.AESEncryptUtil;
import com.gateway.ninglizuul.util.encryption.MD5Util;
import com.gateway.ninglizuul.wrapper.BodyReaderHttpServletRequestWrapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 
 * @ClassName: AESEncryptRequestFilter
 * @Description:AES加解密过滤器,如果存在密文令牌则使用该过滤器对业务数据进行解密
 * @author: 宁黎
 * @date: 2019年5月7日 下午2:39:14
 */
public class AESEncryptRequestFilter extends ZuulFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(AESEncryptRequestFilter.class);
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private EncryptFilterConfig encryptFilterConfig;

	@Override
	public Object run() {
		// 获取请求上下文
		RequestContext requestContext = RequestContext.getCurrentContext();
		HttpServletRequest request = requestContext.getRequest();
		BaseParamDTO baseParamDTO = BodyReaderHttpServletRequestWrapper.getBaseParamDTO(request);
		// 获取访问令牌
		String accessToken = baseParamDTO.getAccessToken();
		String paramSign = baseParamDTO.getSign();
		// 获取加密后的密文数据
		String cipherText = baseParamDTO.getCipher();
		// 通过访问令牌 从redis中获取AES加密密码
		String password = redisTemplate.opsForValue().get(accessToken);
		// 密文解密后的明文
		String plainText = null;
		// 用密码解密AES算法加密后的业务数据
		try {
			plainText = AESEncryptUtil.decrypt(cipherText, password);
		} catch (DecrypteException e) {
			LOGGER.error(e.getMessage());
		}
		// 拼接签名参数，签名为加密参数解密后拼接accessToken 然后进行MD5计算签名
		String signText = new StringBuffer(plainText).append(accessToken).toString();
		// 计算参数签名
		String sign = null;
		try {
			sign = MD5Util.MD5Encode(signText);
		} catch (SignException e) {
			LOGGER.error(e.getMessage());
		}
		LOGGER.info(sign);
		// 如果签名不存在 解密失败 签名不一致 则服务器拒绝请求
		if (StringUtils.isEmpty(sign) || StringUtils.isEmpty(paramSign) || StringUtils.isEmpty(plainText)
				|| !paramSign.equals(sign)) {
			requestContext.setSendZuulResponse(false);
			HttpServletResponse response = requestContext.getResponse();
			response.setStatus(ClientErrorRequestEnum.SERVER_REFUSED_REQUEST.getErrorCode());
			response.setCharacterEncoding(AESEncryptUtil.CHARSET_UTF8);
			requestContext.setResponseBody(ClientErrorRequestEnum.SERVER_REFUSED_REQUEST.getErrorMsg());
		} else {
			// 把解密后的明文重新写入request中
			requestContext.setRequest(new BodyReaderHttpServletRequestWrapper(request, plainText));
			// 请求参数解密成功后 启用响应加密过滤器
			requestContext.set(FilterEnum.AES_RESPONSE_FILTER.getKey(), true);
			// 将AccessToken 传入到下一个过滤器中，主要是响应过滤器中使用
			requestContext.set(FilterEnum.AES_ACCESS_TOKEN.getKey(), accessToken);
			// 如果使用AES过滤器重写请求体就不能再通过RSA过滤器，否则参数转换会失败
			requestContext.set(FilterEnum.RSA_REQUEST_FILTER.getKey(), true);
		}

		return null;
	}

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
	 * @return
	 * @see com.netflix.zuul.IZuulFilter#shouldFilter()
	 */
	@Override
	public boolean shouldFilter() {
		// 读取过滤器启用配置，如果配置为false则直接不执行后面
		if (!encryptFilterConfig.getAesEncrypt()) {
			return false;
		}
		// 获取请求上下文
		RequestContext requestContext = RequestContext.getCurrentContext();
		// 如果RSA过滤器重写了请求体 则不在经过AES过滤器
		if (requestContext.getBoolean(FilterEnum.AES_REQUEST_FILTER.getKey())) {
			return false;
		}
		HttpServletRequest request = requestContext.getRequest();
		String method = request.getMethod();
		String contentType = request.getContentType();
		// 请求方式必须是POST JSON形式,如果不是则执行该过滤器
		if (!FilterEnum.POST_METHOD.getKey().equals(method.toUpperCase())
				&& FilterEnum.JSON_CONTENT_TYPE.getKey().equals(contentType)) {
			return false;
		}
		BaseParamDTO baseParamDTO = BodyReaderHttpServletRequestWrapper.getBaseParamDTO(request);
		// 获取访问令牌
		String accessToken = baseParamDTO.getAccessToken();
		// 如果基本参数为空则不使用该过滤器 访问令牌为空则不启用该过滤器
		if (baseParamDTO == null || StringUtils.isEmpty(accessToken)) {
			return false;
		}
		// 通过访问令牌 从redis中获取AES加密密码
		String password = redisTemplate.opsForValue().get(accessToken);
		// AES加密密码为空则不启用该过滤器
		if (StringUtils.isEmpty(password)) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * <p>
	 * Title: filterOrder
	 * </p>
	 * <p>
	 * Description:数值越大优先级越低
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
		return "pre";
	}

}
