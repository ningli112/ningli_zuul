package com.gateway.ninglizuul.enums.code;

/**
 * 
 * @ClassName: ClientErrorEnum
 * @Description:客户端错误响应code和msg
 * @author: 宁黎
 * @date: 2019年5月8日 下午5:12:00
 */
public enum ClientErrorRequestEnum {
	ERROR_REQUEST(400, "错误请求"), UNAUTHORIZED_REQUEST(401, "未授权请求"), SERVER_REFUSED_REQUEST(403, "服务器拒绝请求"),
	METHOD_DISABLED_REQUEST(405, "方法禁用"), NOT_ACCEPTED_REQUEST(406, "不接受"),
	PREREQUISITES_NOT_MET_REQUEST(412, "未满足前提条件"), UNMET_EXPECTATION(417, "未满足期望值");
	/**
	 * 错误码
	 */
	private int errorCode;
	/**
	 * 错误信息
	 */
	private String errorMsg;

	private ClientErrorRequestEnum(int errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;

	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
