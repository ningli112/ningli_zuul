package com.gateway.ninglizuul.filter;

import java.security.PrivateKey;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.gateway.ninglizuul.config.filter.EncryptFilterConfig;
import com.gateway.ninglizuul.dto.BaseJsonReturnDTO;
import com.gateway.ninglizuul.dto.BaseParamDTO;
import com.gateway.ninglizuul.dto.CommonParamDTO;
import com.gateway.ninglizuul.dto.param.UserOpenApiParamDTO;
import com.gateway.ninglizuul.enums.code.BaseJsonReturnCodeEnum;
import com.gateway.ninglizuul.enums.code.ClientErrorRequestEnum;
import com.gateway.ninglizuul.enums.filter.FilterEnum;
import com.gateway.ninglizuul.exception.encryption.DecrypteException;
import com.gateway.ninglizuul.exception.encryption.SecretKeyGenerationException;
import com.gateway.ninglizuul.fegin.UserOpenApiFegin;
import com.gateway.ninglizuul.util.encryption.RSAEncryptUtil;
import com.gateway.ninglizuul.wrapper.BodyReaderHttpServletRequestWrapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 
 * @ClassName: RSAEncryptRequestFilter
 * @Description:RSA解密过滤器，如果请求参数没有访问令牌,则启用该过滤器，解密RSA公钥加密的业务参数
 * @author: 宁黎
 * @date: 2019年5月9日 上午10:04:42
 */
public class RSAEncryptRequestFilter extends ZuulFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RSAEncryptRequestFilter.class);
	@Autowired
	private EncryptFilterConfig encryptFilterConfig;
	@Autowired
	private UserOpenApiFegin userOpenApiFegin;

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
		if (!encryptFilterConfig.getRsaEncrypt()) {
			return false;
		}
		// 获取请求上下文
		RequestContext requestContext = RequestContext.getCurrentContext();
		// 如果AES过滤器重写了请求体 则不在经过RSA过滤器
		if (requestContext.getBoolean(FilterEnum.RSA_REQUEST_FILTER.getKey())) {
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
		// 如果参数为空则不使用该过滤器
		if (baseParamDTO == null) {
			return false;
		}
		String appId = baseParamDTO.getAppId();
		// 如果不带appId参数则不启用该过滤器
		if (StringUtils.isEmpty(appId)) {
			return false;
		}

		// 获取访问令牌
		String accessToken = baseParamDTO.getAccessToken();
		// 如果访问令牌为空则启用该过滤器
		if (StringUtils.isEmpty(accessToken)) {
			return true;
		}
		return false;
	}

	@Override
	public Object run() {
		// 获取请求上下文
		RequestContext requestContext = RequestContext.getCurrentContext();
		HttpServletRequest request = requestContext.getRequest();
		// 请求体的转换为基本传输参数
		BaseParamDTO baseParamDTO = BodyReaderHttpServletRequestWrapper.getBaseParamDTO(request);
		// 重写请求体后不能在通过AES加解密过滤器 参数转换会失败
		requestContext.set(FilterEnum.AES_REQUEST_FILTER.getKey(), true);
		// 获取加密后的密文数据
		String cipherText = baseParamDTO.getCipher();
		// 私钥存放的文件路径
		String path = encryptFilterConfig.getPrivateKey();
		// 从配置中获取私钥
		String privateKeyStr = null;
		// 用私钥字符生成私钥对象
		PrivateKey privateKey = null;
		// 用于存放明文
		String plainText = null;
		try {
			privateKeyStr = RSAEncryptUtil.loadPrivateKeyByFile(path);
			privateKey = RSAEncryptUtil.loadPrivateKeyStr(privateKeyStr);
			plainText = RSAEncryptUtil.decrypt(privateKey, cipherText);
		} catch (SecretKeyGenerationException e) {
			LOGGER.error(e.getMessage());
		} catch (DecrypteException e) {
			LOGGER.error(e.getMessage());
		}
		// 如果解密失败则服务器拒绝请求
		if (StringUtils.isEmpty(plainText)) {
			requestContext.setSendZuulResponse(false);
			HttpServletResponse response = requestContext.getResponse();
			response.setStatus(ClientErrorRequestEnum.SERVER_REFUSED_REQUEST.getErrorCode());
			response.setCharacterEncoding(RSAEncryptUtil.CHARSET_UTF8);
			requestContext.setResponseBody(ClientErrorRequestEnum.SERVER_REFUSED_REQUEST.getErrorMsg());
			return null;
		}
		// 校验APPID APPKEY
		CommonParamDTO commonParamDTO = JSON.parseObject(plainText, CommonParamDTO.class);
		String secretKey = commonParamDTO.getSecretKey();
		String appId = baseParamDTO.getAppId();
		UserOpenApiParamDTO openApiDTO = new UserOpenApiParamDTO(appId, secretKey);
		BaseJsonReturnDTO<String> jsonReturnDTO = userOpenApiFegin.checkAppId(openApiDTO);
		if (jsonReturnDTO.getCode() != BaseJsonReturnCodeEnum.SUCCESS.getCode()) {
			requestContext.setSendZuulResponse(false);
			HttpServletResponse response = requestContext.getResponse();
			response.setStatus(ClientErrorRequestEnum.SERVER_REFUSED_REQUEST.getErrorCode());
			response.setCharacterEncoding(RSAEncryptUtil.CHARSET_UTF8);
			requestContext.setResponseBody(JSON.toJSONString(jsonReturnDTO));
			return null;
		}
		// 把解密后的业务明文重新写入request中
		requestContext.setRequest(new BodyReaderHttpServletRequestWrapper(request, commonParamDTO.getBusiness()));
		// 请求解密过滤器启用了对请求参数解密成功后，响应加密过滤器也要启用
		requestContext.set(FilterEnum.RSA_RESPONSE_FILTER.getKey(), true);
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
		return "pre";
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
