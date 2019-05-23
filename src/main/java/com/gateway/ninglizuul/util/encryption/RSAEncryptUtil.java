package com.gateway.ninglizuul.util.encryption;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import com.gateway.ninglizuul.enums.exception.UtilExceptionCodeEnum;
import com.gateway.ninglizuul.exception.encryption.DecrypteException;
import com.gateway.ninglizuul.exception.encryption.EncryptException;
import com.gateway.ninglizuul.exception.encryption.SecretKeyGenerationException;
import com.gateway.ninglizuul.exception.encryption.SignException;

/**
 * 
 * @ClassName: RSAEncryptUtil
 * @Description:RAS 加密解密工具类
 * @author: 宁黎
 * @date: 2019年4月30日 上午9:25:57
 */
public class RSAEncryptUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(RSAEncryptUtil.class);
	/**
	 * 加密算法
	 */
	public static final String KEY_ALGORITHM = "RSA";
	/**
	 * 填充设置
	 */
	public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
	/**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;

	/**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;
	/**
	 * 字符集设置
	 */
	public static final String CHARSET_UTF8 = "UTF-8";
	/**
	 * 签名算法
	 */
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	/**
	 * 私钥字符
	 */
	public static String privateKeyString = null;

	/**
	 * 
	 * @Title: sign
	 * @Description: 对数据用私钥进行签名
	 * @param data       已加密要签名的数据
	 * @param privateKey 私钥对象说
	 * @return
	 * @throws SignException
	 *                       String
	 * @author: 宁黎
	 * @date:2019年5月6日下午1:45:56
	 *
	 */
	public static String sign(String data, PrivateKey privateKey) throws SignException {
		String sign = null;
		try {
			byte[] dataByte = data.getBytes(CHARSET_UTF8);
			Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
			signature.initSign(privateKey);
			signature.update(dataByte);
			sign = Base64.getEncoder().encodeToString(signature.sign());
		} catch (SignatureException e) {
			LOGGER.error("签名发生异常");
		} catch (InvalidKeyException e) {
			LOGGER.error("无效的秘钥（编码无效、长度错误、未初始化）");
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("加密算法不存在");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("不支持该字符编码");
		}
		if (sign == null) {
			throw new SignException(UtilExceptionCodeEnum.SIGN_EXCEPTION.getErrorCode(),
					UtilExceptionCodeEnum.SIGN_EXCEPTION.getErrorMsg());
		}
		return sign;
	}

	/**
	 * 
	 * @Title: loadPrivateKeyByFile
	 * @Description: 从文件中读取私钥字符，私钥命名为privateKey.keystore
	 * @param path
	 * @return String
	 * @throws SecretKeyGenerationException
	 * 
	 * @author: 宁黎
	 * @date:2019年5月9日上午10:35:30
	 *
	 */
	public static String loadPrivateKeyByFile(String path) throws SecretKeyGenerationException {
		if (privateKeyString != null) {
			return privateKeyString;
		}

		try {
			File file = ResourceUtils.getFile(path + "/privateKey.keystore");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				sb.append(readLine);
			}
			br.close();
			privateKeyString = sb.toString();
		} catch (IOException e) {
			LOGGER.error("私钥读取异常");
		} catch (NullPointerException e) {
			LOGGER.error("私钥文件privateKey.keystore不存在");
		}
		if (StringUtils.isEmpty(privateKeyString)) {
			throw new SecretKeyGenerationException(UtilExceptionCodeEnum.SECRET_KEY_GENERATION_EXCEPTION.getErrorCode(),
					UtilExceptionCodeEnum.SECRET_KEY_GENERATION_EXCEPTION.getErrorMsg());
		}
		return privateKeyString;
	}

	/**
	 * 
	 * @Title: verify
	 * @Description: 公钥校验数据签名
	 * @param data
	 * @param sign
	 * @param publicKey
	 * @return
	 * 		boolean
	 * @author: 宁黎
	 * @date:2019年5月9日上午10:28:36
	 *
	 */
	public static boolean verify(String data, String sign, PublicKey publicKey) {
		boolean flag = false;
		try {
			byte[] dataByte = data.getBytes(CHARSET_UTF8);
			Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
			signature.initVerify(publicKey);
			signature.update(dataByte);
			flag = signature.verify(Base64.getDecoder().decode(sign));
		} catch (SignatureException e) {
			LOGGER.error("签名发生异常");
		} catch (InvalidKeyException e) {
			LOGGER.error("无效的秘钥（编码无效、长度错误、未初始化）");
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("加密算法不存在");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("不支持该字符编码");
		}
		return flag;
	}

	/**
	 * 
	 * @Title: loadPublicKeyStr
	 * @Description: 通过字符获还原公钥，公钥字符为base64字符
	 * @param publicKeyStr 公钥base64字符
	 * @return RSAPublicKey
	 * @author: 宁黎
	 * @throws SecretKeyGenerationException
	 * @date: 2019年4月30日上午10:04:54
	 * 
	 */
	public static PublicKey loadPublicKeyStr(String publicKeyStr) throws SecretKeyGenerationException {
		byte[] buffer = Base64.getDecoder().decode(publicKeyStr);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
		KeyFactory keyFactory = null;
		PublicKey publicKey = null;
		try {
			keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			publicKey = keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("加密算法不存在");
		} catch (InvalidKeySpecException e) {
			LOGGER.error("公钥非法");
		}
		if (publicKey == null) {
			throw new SecretKeyGenerationException(UtilExceptionCodeEnum.SECRET_KEY_GENERATION_EXCEPTION.getErrorCode(),
					UtilExceptionCodeEnum.SECRET_KEY_GENERATION_EXCEPTION.getErrorMsg());
		}
		return publicKey;
	}

	/**
	 * 
	 * @Title: loadPrivateKeyStr
	 * @Description: 通过私钥还原私钥对象，私钥字符为BASE64字符
	 * @param privateKeyStr 私钥BASE64字符
	 * @return
	 * 		PrivateKey
	 * @author: 宁黎
	 * @throws SecretKeyGenerationException
	 * @date:2019年4月30日下午1:53:13
	 *
	 */
	public static PrivateKey loadPrivateKeyStr(String privateKeyStr) throws SecretKeyGenerationException {
		byte[] buffer = Base64.getDecoder().decode(privateKeyStr);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
		KeyFactory keyFactory = null;
		PrivateKey privateKey = null;
		try {
			keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			privateKey = keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("加密算法不存在");
		} catch (InvalidKeySpecException e) {
			LOGGER.error("私钥非法");
		}
		if (privateKey == null) {
			throw new SecretKeyGenerationException(UtilExceptionCodeEnum.SECRET_KEY_GENERATION_EXCEPTION.getErrorCode(),
					UtilExceptionCodeEnum.SECRET_KEY_GENERATION_EXCEPTION.getErrorMsg());
		}
		return privateKey;
	}

	/**
	 * 
	 * @Title: encrypt
	 * @Description: 对明文进行加密，明文字符集编码为UTF-8,传入公钥就是公钥加密，传入私钥就是私钥加密
	 * @param key       秘钥钥对象
	 * @param plainText 明文字符
	 * @return
	 * 		String
	 * @author: 宁黎
	 * @throws EncryptException
	 * @date:2019年5月6日上午11:03:03
	 *
	 */
	public static String encrypt(Key key, String plainText) throws EncryptException {
		String encryptedData = null;
		try {
			byte[] data = plainText.getBytes(CHARSET_UTF8);
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offset = 0;
			byte[] cache;
			int i = 0;
			while (inputLen - offset > 0) {
				if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(data, offset, MAX_ENCRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(data, offset, inputLen - offset);
				}
				out.write(cache, 0, cache.length);
				i++;
				offset = i * MAX_ENCRYPT_BLOCK;
			}
			byte[] encryptedByteData = out.toByteArray();
			out.close();
			encryptedData = Base64.getEncoder().encodeToString(encryptedByteData);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("加密算法不存在");
		} catch (NoSuchPaddingException e) {
			LOGGER.error("填充机制不可用");
		} catch (InvalidKeyException e) {
			LOGGER.error("无效的秘钥（编码无效、长度错误、未初始化）");
		} catch (IllegalBlockSizeException e) {
			LOGGER.error("加密数据过长，或加密数据错误");
		} catch (BadPaddingException e) {
			LOGGER.error("数据没有正确被填充");
		} catch (IOException e) {
			LOGGER.error("字节流关闭错误");
		}
		if (encryptedData == null) {
			throw new EncryptException(UtilExceptionCodeEnum.ENCRYPT_EXCEPTION.getErrorCode(),
					UtilExceptionCodeEnum.ENCRYPT_EXCEPTION.getErrorMsg());
		}
		return encryptedData;
	}

	/**
	 * 
	 * @Title: decrypt
	 * @Description: 解密数据，并返回原来的明文,传入私钥就是私钥解密，传入公钥就是公钥解密，密文为BASE64编码字符
	 * @param key           秘钥对象
	 * @param encryptedData 密文数据
	 * @return String
	 * @throws DecrypteException
	 * 
	 * @author: 宁黎
	 * @date:2019年4月30日下午6:28:00
	 *
	 */
	public static String decrypt(Key key, String encryptedData) throws DecrypteException {
		int offset = 0;
		byte[] cache;
		int i = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String decryptedStr = null;
		try {
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			byte[] data = Base64.getDecoder().decode(encryptedData);
			int inputLen = data.length;
			cipher.init(Cipher.DECRYPT_MODE, key);
			while (inputLen - offset > 0) {
				if (inputLen - offset > MAX_DECRYPT_BLOCK) {
					cache = cipher.doFinal(data, offset, MAX_DECRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(data, offset, inputLen - offset);
				}
				out.write(cache, 0, cache.length);
				i++;
				offset = i * MAX_DECRYPT_BLOCK;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();
			decryptedStr = new String(decryptedData);

		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("加密算法不存在");
		} catch (NoSuchPaddingException e) {
			LOGGER.error("填充机制不可用");
		} catch (InvalidKeyException e) {
			LOGGER.error("无效的秘钥（编码无效、长度错误、未初始化）");
		} catch (IOException e) {
			LOGGER.error("字节流关闭错误");
		} catch (IllegalBlockSizeException e) {
			LOGGER.error("解密数据过长，解密数据错误");
		} catch (BadPaddingException e) {
			LOGGER.error("数据没有正确被填充");
		}
		if (decryptedStr == null) {
			throw new DecrypteException(UtilExceptionCodeEnum.DECRYPT_EXCEPTION.getErrorCode(),
					UtilExceptionCodeEnum.DECRYPT_EXCEPTION.getErrorMsg());
		}
		return decryptedStr;
	}

	public static void main(String[] args) {
		String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCOKQCAuF5vdA/Wos6mGAGaRRJ+h4dulG2nS6lANFYqSFowbBiERP1Xv4YoHk/jb6F2L8L/+PguZPyFNdSfQPjXWVR7Pr5gYmZSOOMGpnGSSNRGPrvvRMJEkCAZRhDosp0My75nvYL8C1zGnMcX1Mi3fCTYu6JuOcH8f6/uGHRprQIDAQAB";
		String privateKeyStr = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI4pAIC4Xm90D9aizqYYAZpFEn6Hh26UbadLqUA0VipIWjBsGIRE/Ve/higeT+NvoXYvwv/4+C5k/IU11J9A+NdZVHs+vmBiZlI44wamcZJI1EY+u+9EwkSQIBlGEOiynQzLvme9gvwLXMacxxfUyLd8JNi7om45wfx/r+4YdGmtAgMBAAECgYAkw02x+/vP7r+5zdiK82JWts4Vko7fddr1jId0ZxRxUsygARRGAGFTs9/JuC0Ir/GdUrSm5Yeo18zF1inqFS9Efpb81eyWv0U3JI+otZVUzJNEsZYn1ItRdXRYgxkmRdIZI+gYHUJkcm2a02274WQGsoOUnvwwI/qVK/qgFkp9sQJBAMzFCMMlL79A3Yp0OQg3RzinDcbc+1qdM94rKzfTOUgtJdYk48vGO0uxVh1thMCHxKxZIVSI43X5mehg+Rk5y8MCQQCxuf3btIzXKyjEUrBLqbt56SsaPLQR+XjZBXSPJwHIr9ekjsKj0WQNR03r6pfdky3NxHqkSwoRQrftDDEZ5U3PAkEAlqJ/rqbleAkssFSNO/kGQKvLm7YmheNNK6uQEHInGmx0ABOFK/t41VbwZZeSZ9u4l5y9wyUsRzZFvUfRF3iH5wJAOIXpibVrUYdFaOLPdNDfcg9JCQgjUNAhsgu9HUYPmC7si0Zn+se+ZUa3Ln+mmu6TE60YQCYpZoHskvRcrVqzGwJAO1W//s1jHdRs8gVHUwFJpJdB7LvyEvsnoir1r4tvIvRMIRXOkMixavxjJdRVaB2tU1BQsI0Rx/6NTJ8hi9y9dA==";
		String plainText = "{\"secretKey\":\"PABLLDIKHAHOENKJJPELMNFDDMGEFNPL\",\"randomNumber\":\"586487\",\"business\":{\"systemPlateformId\":1,\"companyName\":\"腾讯\"}}";
		try {
			PublicKey publicKey = RSAEncryptUtil.loadPublicKeyStr(publicKeyStr);

			PrivateKey privateKey = RSAEncryptUtil.loadPrivateKeyStr(privateKeyStr);
			String encryptStr = RSAEncryptUtil.encrypt(publicKey, plainText);
			String decryptStr = RSAEncryptUtil.decrypt(privateKey, encryptStr);
			System.out.println(encryptStr);
			System.out.println(decryptStr);
			String encryptStr1 = RSAEncryptUtil.encrypt(privateKey, plainText);
			String decryptStr1 = RSAEncryptUtil.decrypt(publicKey, "TgmbS4GGZi/rYaTqZDQqaxRJ9XRiYSx4yEOH1SALo/PobEgwGASxSAzaS5M4jxBt4P1RIYoTesCpMGgU7C3VT05nC6mlGyANrB7wiJo599+kk2ry6+UoLpwwTnOLpM9n79Jes4wXWPZL5rQ2CMZgkapW7F0fagDK4JPUOFe05Fw=");
			System.out.println(decryptStr1);
		} catch (SecretKeyGenerationException e) {
			e.printStackTrace();
		} catch (EncryptException e) {
			e.printStackTrace();
		} catch (DecrypteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
