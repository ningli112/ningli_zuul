package com.gateway.ninglizuul.exception.encryption;

/**
 * 
 * @ClassName: EncryptException
 * @Description:RSA加密异常类，加密失败会抛出此异常
 * @author: 宁黎
 * @date: 2019年4月30日 下午5:33:49
 */
public class EncryptException extends Exception {

	private static final long serialVersionUID = 6063426202810248346L;
	/**
	 * 错误码
	 */
	private String errorCode;

	public EncryptException() {

	}

	/**
	 * 
	 * @Title: EncryptException
	 * @Description: 构造一个有详细错误信息的异常
	 * @param: errorMsg
	 * @author: 宁黎
	 *          2019下午1:35:11
	 * 
	 */
	public EncryptException(String errorMsg) {
		super(errorMsg);
	}

	/**
	 * 
	 * @Title: EncryptException
	 * @Description: 构造一个有详细信息和错误码的异常,加密解密失败会抛出此异常
	 * @param: errorCode
	 * @param: errorMsg
	 * @author: 宁黎
	 *          2019下午1:35:36
	 * 
	 */
	public EncryptException(String errorCode, String errorMsg) {
		super(errorMsg);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
