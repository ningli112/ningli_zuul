package com.gateway.ninglizuul.util.encryption;

import java.security.MessageDigest;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.gateway.ninglizuul.enums.exception.UtilExceptionCodeEnum;
import com.gateway.ninglizuul.exception.encryption.SignException;

public class MD5Util {
	private static final Logger LOGGER = LoggerFactory.getLogger(MD5Util.class);

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
			"E", "F" };

	/**
	 * 
	 * @Title: byteArrayToHexString
	 * @Description: 转换字节数组为16进制字串
	 * @param b
	 * @return
	 * 		String
	 * @author: 宁黎
	 * @date:2019年5月9日上午11:39:39
	 *
	 */
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/**
	 * 
	 * @Title: byteToHexString
	 * @Description: 转换byte到16进制
	 * @param b
	 * @return
	 * 		String
	 * @author: 宁黎
	 * @date:2019年5月9日上午11:40:21
	 *
	 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * 
	 * @Title: MD5Encode
	 * @Description: 对数据进行MD5计算
	 * @param origin
	 * @return
	 * @throws SignException
	 *                       String
	 * @author: 宁黎
	 * @date:2019年5月9日上午11:38:34
	 *
	 */
	public static String MD5Encode(String origin) throws SignException {
		if (origin == null)
			return null;
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {
			LOGGER.error(MD5Util.class + " --MD5加密异常：" + ex.getMessage());
		}
		if (StringUtils.isEmpty(resultString)) {
			throw new SignException(UtilExceptionCodeEnum.SIGN_EXCEPTION.getErrorCode(),
					UtilExceptionCodeEnum.SIGN_EXCEPTION.getErrorMsg());
		}
		return resultString;
	}

	/**
	 * 
	 * @Title: getUUID
	 * @Description: 获取UUID
	 * @return
	 * 		String
	 * @author: 宁黎
	 * @date:2019年5月10日下午3:12:05
	 *
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
		return uuid;
	}
}
