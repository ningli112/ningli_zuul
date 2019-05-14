package com.gateway.ninglizuul.enums.exception;

/**
 * 
 * @ClassName: UtilExceptionCodeEnum
 * @Description:工具类错误码和信息枚举
 * @author: 宁黎
 * @date: 2019年4月30日 上午11:32:14
 */
public enum UtilExceptionCodeEnum {
	SECRET_KEY_GENERATION_EXCEPTION("E100001", "秘钥数据为空或秘钥数据错误"), ENCRYPT_EXCEPTION("E100002", "数据加密异常"),
	DECRYPT_EXCEPTION("E100003", "数据解密异常"), SIGN_EXCEPTION("E100004", "对数据进行签名时发生异常");
	/**
	 * 错误码
	 */
	private String errorCode;
	/**
	 * 错误信息
	 */
	private String errorMsg;

	private UtilExceptionCodeEnum(String errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;

	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
