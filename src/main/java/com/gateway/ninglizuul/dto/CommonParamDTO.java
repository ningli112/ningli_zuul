package com.gateway.ninglizuul.dto;

public class CommonParamDTO {
	/**
	 * AES对称加密密码
	 */
	private String password;
	/**
	 * appKey APPID 对应的密码
	 */
	private String secretKey;
	/**
	 * 随机数，加入随机数让签名不可预判
	 */
	private int randomNumber;
	/**
	 * 存放业务密文
	 */
	private String business;

	public CommonParamDTO(String password, String business) {
		this.password = password;
		this.business = business;
	}

	public CommonParamDTO(String password, String secretKey, int randomNumber, String business) {
		this.password = password;
		this.secretKey = secretKey;
		this.randomNumber = randomNumber;
		this.business = business;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public int getRandomNumber() {
		return randomNumber;
	}

	public void setRandomNumber(int randomNumber) {
		this.randomNumber = randomNumber;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

}
