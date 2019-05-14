package com.gateway.ninglizuul.dto;

public class BaseParamDTO {
	/**
	 * 应用ID
	 */
	private String appId;

	/**
	 * 密文数据
	 */
	private String cipher;
	/**
	 * 访问令牌
	 */
	private String accesToken;
	/**
	 * 数据签名
	 */
	private String sign;

	/**
	 * 
	 * @Title: BaseParamDTO
	 * @Description: 初始化基本参数
	 * @param: appId 应用ID
	 * @param: cipher 密文
	 * @author: 宁黎
	 *          2019下午5:36:40
	 * 
	 */
	public BaseParamDTO(String appId, String cipher) {
		this.appId = appId;
		this.cipher = cipher;
	}

	/**
	 * 
	 * @Title: BaseParamDTO
	 * @Description: 初始化密文 访问令牌和签名
	 * @param: cipher
	 * @param: accesToken
	 * @param: sign
	 * @author: 宁黎
	 *          2019下午3:02:24
	 * 
	 */
	public BaseParamDTO(String cipher, String accesToken, String sign) {
		this.cipher = cipher;
		this.accesToken = accesToken;
		this.sign = sign;
	}

	/**
	 * 
	 * @Title: BaseParamDTO
	 * @Description: 初始化所有参数
	 * @param: appId
	 * @param: cipher
	 * @param: accesToken
	 * @param: sign
	 * @author: 宁黎
	 *          2019下午3:50:01
	 * 
	 */
	public BaseParamDTO(String appId, String cipher, String accesToken, String sign) {
		this.appId = appId;
		this.cipher = cipher;
		this.accesToken = accesToken;
		this.sign = sign;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCipher() {
		return cipher;
	}

	public void setCipher(String cipher) {
		this.cipher = cipher;
	}

	public String getAccesToken() {
		return accesToken;
	}

	public void setAccesToken(String accesToken) {
		this.accesToken = accesToken;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
