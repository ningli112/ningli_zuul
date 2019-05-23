package com.gateway.ninglizuul.filter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSON;
import com.gateway.ninglizuul.dto.BaseParamDTO;
import com.gateway.ninglizuul.dto.CommonParamDTO;
import com.gateway.ninglizuul.enums.filter.FilterEnum;
import com.gateway.ninglizuul.exception.encryption.EncryptException;
import com.gateway.ninglizuul.exception.encryption.SignException;
import com.gateway.ninglizuul.util.encryption.AESEncryptUtil;
import com.gateway.ninglizuul.util.encryption.MD5Util;
import com.gateway.ninglizuul.util.encryption.RSAEncryptUtil;
import com.gateway.ninglizuul.wrapper.BodyReaderHttpServletRequestWrapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 
 * @ClassName: AESEncryptResponseFilter
 * @Description:AES 响应加密过滤器
 * @author: 宁黎
 * @date: 2019年5月10日 上午11:14:59
 */
public class AESEncryptResponseFilter extends ZuulFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(AESEncryptResponseFilter.class);
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
	 * @return
	 * @see com.netflix.zuul.IZuulFilter#shouldFilter()
	 */
	@Override
	public boolean shouldFilter() {
		// 获取请求上下文
		RequestContext requestContext = RequestContext.getCurrentContext();
		if (requestContext.getBoolean(FilterEnum.AES_RESPONSE_FILTER.getKey())) {
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
		String accessToken = (String) requestContext.get(FilterEnum.AES_ACCESS_TOKEN.getKey());
		String password = redisTemplate.opsForValue().get(accessToken);
		// 产生新的加密密码
		String newPassword = MD5Util.getUUID();
		CommonParamDTO commonParamDTO = new CommonParamDTO(newPassword, responseBody);
		String plainText = JSON.toJSONString(commonParamDTO);
		// 数据签名
		String sign = null;
		// 密文
		String cipherText = null;
		try {
			// 计算签名
			sign = MD5Util.MD5Encode(new StringBuffer(plainText).append(accessToken).toString());
			// 用新密码对数据进行加密
			cipherText = AESEncryptUtil.encrypt(plainText, newPassword);
		} catch (SignException | EncryptException e) {
			LOGGER.error(e.getMessage());
		}
		BaseParamDTO baseParamDTO = new BaseParamDTO(cipherText, accessToken, sign);
		String responseStr = JSON.toJSONString(baseParamDTO);
		HttpServletResponse response = requestContext.getResponse();
		response.setContentType(FilterEnum.JSON_CONTENT_TYPE.getKey());
		response.setCharacterEncoding(RSAEncryptUtil.CHARSET_UTF8);
		requestContext.setResponseBody(responseStr);
		// 设置新的加密秘钥
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
		return 1;
	}
}
