package com.gateway.ninglizuul.config.filter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 
 * @ClassName: EncryptFilterConfig
 * @Description:加解密过滤器配置
 * @author: 宁黎
 * @date: 2019年5月9日 下午2:50:35
 */
@Component
@ConfigurationProperties(prefix = "encrypt.filter")
public class EncryptFilterConfig {
	/**
	 * RSA 私钥
	 */
	private String privateKey;
	/**
	 * RSA加解密过滤器配置
	 */
	private Boolean rsaEncrypt = true;
	/**
	 * AES加解密过滤器配置
	 */
	private Boolean aesEncrypt = true;

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public Boolean getRsaEncrypt() {
		return rsaEncrypt;
	}

	public void setRsaEncrypt(Boolean rsaEncrypt) {
		this.rsaEncrypt = rsaEncrypt;
	}

	public Boolean getAesEncrypt() {
		return aesEncrypt;
	}

	public void setAesEncrypt(Boolean aesEncrypt) {
		this.aesEncrypt = aesEncrypt;
	}

}
