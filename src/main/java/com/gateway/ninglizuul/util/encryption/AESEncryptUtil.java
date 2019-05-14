package com.gateway.ninglizuul.util.encryption;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gateway.ninglizuul.enums.exception.UtilExceptionCodeEnum;
import com.gateway.ninglizuul.exception.encryption.DecrypteException;
import com.gateway.ninglizuul.exception.encryption.EncryptException;
import com.gateway.ninglizuul.exception.encryption.SecretKeyGenerationException;

public class AESEncryptUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(AESEncryptUtil.class);
	/**
	 * 加密算法
	 */
	private static final String KEY_ALGORITHM = "AES";
	/**
	 * 秘钥长度
	 */
	private static final Integer KEY_LENGTH = 128;
	/**
	 * 字符集设置
	 */
	public static final String CHARSET_UTF8 = "UTF-8";
	/**
	 * 填充设置
	 */
	private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	/**
	 * 
	 * @Title: getSecretKey
	 * @Description: 生成AES秘钥对象
	 * @param password 加密密码
	 * @return SecretKeySpec
	 * @throws SecretKeyGenerationException
	 * 
	 * @author: 宁黎
	 * @date:2019年5月7日上午9:55:41
	 *
	 */
	private static SecretKeySpec getSecretKey(String password) throws SecretKeyGenerationException {
		byte[] passwordByte = null;
		SecretKeySpec secretKeySpec = null;
		try {
			passwordByte = password.getBytes(CHARSET_UTF8);
			KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
			keyGenerator.init(KEY_LENGTH, new SecureRandom(passwordByte));
			SecretKey secretKey = keyGenerator.generateKey();
			secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("不支持该字符编码");
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("加密算法不存在");
		}
		if (secretKeySpec == null) {
			throw new SecretKeyGenerationException(UtilExceptionCodeEnum.SECRET_KEY_GENERATION_EXCEPTION.getErrorCode(),
					UtilExceptionCodeEnum.SECRET_KEY_GENERATION_EXCEPTION.getErrorMsg());
		}
		return secretKeySpec;
	}

	/**
	 * 
	 * @Title: encrypt
	 * @Description: AES 对数据进行加密
	 * @param plainText 明文
	 * @param password  加密密码
	 * @return String
	 * @throws EncryptException
	 * 
	 * @author: 宁黎
	 * @date:2019年5月7日上午10:23:00
	 *
	 */
	public static String encrypt(String plainText, String password) throws EncryptException {
		Cipher cipher = null;
		byte[] plainTextByte = null;
		byte[] result = null;
		try {
			cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
			plainTextByte = plainText.getBytes(CHARSET_UTF8);
			cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));
			result = cipher.doFinal(plainTextByte);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("加密算法不存在");
		} catch (NoSuchPaddingException e) {
			LOGGER.error("填充机制不可用");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("不支持字符编码");
		} catch (InvalidKeyException e) {
			LOGGER.error("无效的秘钥（编码无效、长度错误、未初始化）");
		} catch (SecretKeyGenerationException e) {
			LOGGER.error("秘钥生成异常");
		} catch (IllegalBlockSizeException e) {
			LOGGER.error("加密数据过长，加密数据错误");
		} catch (BadPaddingException e) {
			LOGGER.error("数据没有正确被填充");
		}
		if (result == null) {
			throw new EncryptException(UtilExceptionCodeEnum.ENCRYPT_EXCEPTION.getErrorCode(),
					UtilExceptionCodeEnum.ENCRYPT_EXCEPTION.getErrorMsg());
		}
		return Base64.getEncoder().encodeToString(result);
	}

	/**
	 * 
	 * @Title: decrypt
	 * @Description: AES 解密
	 * @param cipherText 密文BASE64编码
	 * @param password   密码
	 * @return
	 * 		String
	 * @author: 宁黎
	 * @throws DecrypteException
	 * @date:2019年5月7日上午10:52:20
	 *
	 */
	public static String decrypt(String cipherText, String password) throws DecrypteException {
		String result = null;
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));
			byte[] cipherTextByte = Base64.getDecoder().decode(cipherText);
			byte[] plainTextByte = cipher.doFinal(cipherTextByte);
			result = new String(plainTextByte);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("加密算法不存在");
		} catch (NoSuchPaddingException e) {
			LOGGER.error("填充机制不可用");
		} catch (IllegalBlockSizeException e) {
			LOGGER.error("解密数据过长，解密数据错误");
		} catch (BadPaddingException e) {
			LOGGER.error("数据没有正确被填充");
		} catch (InvalidKeyException e) {
			LOGGER.error("无效的秘钥（编码无效、长度错误、未初始化）");
		} catch (SecretKeyGenerationException e) {
			LOGGER.error("秘钥产生异常");
		}
		if (result == null) {
			throw new DecrypteException(UtilExceptionCodeEnum.DECRYPT_EXCEPTION.getErrorCode(),
					UtilExceptionCodeEnum.DECRYPT_EXCEPTION.getErrorMsg());
		}
		return result;

	}

	public static void main(String[] args) {
		try {
			System.out.println(encrypt("ningli", "446A615164F846AE8C343E2E4634D2CC"));
		} catch (EncryptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
