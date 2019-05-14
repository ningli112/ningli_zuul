package com.gateway.ninglizuul.exception.encryption;

/**
 * 
 * @ClassName: SignException
 * @Description:当对数据进行签名失败时候则抛出此异常
 * @author: 宁黎
 * @date: 2019年5月6日 下午1:40:22
 */
public class SignException extends Exception {
	private static final long serialVersionUID = -1927578507707904736L;
	/**
	 * 错误码
	 */
	private String errorCode;

	public SignException() {

	}

	/**
	 * 
	 * @Title: SecretKeyGenerationException
	 * @Description: 构造一个有详细错误信息的异常，当对数据进行签名失败时候则抛出此异常
	 * @param: errorMsg
	 * @author: 宁黎
	 *          2019下午1:35:11
	 * 
	 */
	public SignException(String errorMsg) {
		super(errorMsg);
	}

	/**
	 * 
	 * @Title: SecretKeyGenerationException
	 * @Description: 构造一个有详细信息和错误码的异常，当对数据进行签名失败时候则抛出此异常
	 * @param: errorCode
	 * @param: errorMsg
	 * @author: 宁黎
	 *          2019下午1:35:36
	 * 
	 */
	public SignException(String errorCode, String errorMsg) {
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
