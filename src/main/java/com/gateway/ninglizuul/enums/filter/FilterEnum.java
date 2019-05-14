package com.gateway.ninglizuul.enums.filter;

/**
 * 
 * @ClassName: FilterEnum
 * @Description:
 * @author: 宁黎
 * @date: 2019年5月10日 上午11:32:45
 */
public enum FilterEnum {
	RSA_RESPONSE_FILTER("RSA_RESPONSE_FILTER"), AES_RESPONSE_FILTER("AES_RESPONSE_FILTER"),
	RSA_REQUEST_FILTER("RSA_REQUEST_FILTER"), AES_REQUEST_FILTER("AES_REQUEST_FILTER"),
	JSON_CONTENT_TYPE("application/json"), POST_METHOD("POST"), AES_ACCESS_TOKEN("AES_ACCESS_TOKEN");
	private String key;

	private FilterEnum(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
