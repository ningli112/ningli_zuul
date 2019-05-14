package com.gateway.ninglizuul.wrapper;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import com.alibaba.fastjson.JSON;
import com.gateway.ninglizuul.dto.BaseParamDTO;
import com.gateway.ninglizuul.util.encryption.RSAEncryptUtil;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.ServletInputStreamWrapper;

public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(BodyReaderHttpServletRequestWrapper.class);
	private final byte[] body;

	/**
	 * 
	 * @Title: BodyReaderHttpServletRequestWrapper
	 * @Description: 初始化BodyReaderHttpServletRequestWrapper，并重新吧参数写入请求体
	 * @param: request HttpServletRequest对象
	 * @param: plainText 明文重写入request
	 * @author: 宁黎
	 *          2019下午2:16:39
	 * 
	 */
	public BodyReaderHttpServletRequestWrapper(HttpServletRequest request, String plainText) {
		super(request);
		this.body = plainText.getBytes(Charset.forName("UTF-8"));
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return new ServletInputStreamWrapper(body);
	}

	@Override
	public int getContentLength() {
		return body.length;
	}

	@Override
	public long getContentLengthLong() {
		return body.length;
	}

	/**
	 * 
	 * @Title: getBodyString
	 * @Description: 吧请求体转成字符
	 * @param request
	 * @return
	 * 		String
	 * @author: 宁黎
	 * @date:2019年5月7日下午1:17:17
	 *
	 */
	public static String getBodyString(ServletRequest request) {
		StringBuilder sb = new StringBuilder();
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			inputStream = request.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			LOGGER.error("getBodyString出现问题！");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					LOGGER.error("流关闭出现问题！", e);
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					LOGGER.error("流关闭出现问题！", e);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @Title: getResponseBody
	 * @Description:从请求上下文中获取请求响应体
	 * @param requestContext
	 * @return
	 * 		String
	 * @author: 宁黎
	 * @date:2019年5月10日下午6:15:21
	 *
	 */
	public static String getResponseBody(RequestContext requestContext) {
		InputStream inputStream = requestContext.getResponseDataStream();
		// 获取响应体
		String responseBody = null;
		try {
			responseBody = StreamUtils.copyToString(inputStream, Charset.forName(RSAEncryptUtil.CHARSET_UTF8));
		} catch (IOException e) {
			LOGGER.error("读取响应体异常");
		}
		return responseBody;
	}

	/**
	 * 
	 * @Title: getBaseParamDTO
	 * @Description: 从请求体中读取流并转为基本参数对象
	 * @param request
	 * @return
	 * 		BaseParamDTO
	 * @author: 宁黎
	 * @date:2019年5月9日上午10:01:09
	 *
	 */
	public static BaseParamDTO getBaseParamDTO(HttpServletRequest request) {
		// 请求体转为字符
		String body = BodyReaderHttpServletRequestWrapper.getBodyString(request);
		// 请求体字符转为基本参数对象
		BaseParamDTO baseParamDTO = JSON.parseObject(body, BaseParamDTO.class);
		return baseParamDTO;
	}
}
