package com.gateway.ninglizuul.dto.param;

/**
 * 
 * @ClassName: UserOpenApiParamDTO
 * @Description:关于开放API用户请求参数实体类
 * @author: 宁黎
 * @date: 2019年5月22日 下午1:49:38
 */
public class UserOpenApiParamDTO {
	/**
	 * appId
	 */
	private String appId;
	/**
	 * appId对应的APPKEY
	 */
	private String secretKey;

	public UserOpenApiParamDTO() {
	}

	public UserOpenApiParamDTO(String appId, String secretKey) {
		this.appId = appId;
		this.secretKey = secretKey;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

}
