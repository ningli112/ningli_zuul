package com.gateway.ninglizuul.exception.encryption;

/**
 * 
 * @ClassName: SecretKeyGenerationException
 * @Description:秘钥生成异常自定义异常类,秘钥字符还原成秘钥对象失败会抛出此异常
 * @author: 宁黎
 * @date: 2019年4月30日 下午1:12:34
 */
public class SecretKeyGenerationException extends Exception {
	private static final long serialVersionUID = -851120521535051186L;
	/**
	 * 错误码
	 */
	private String errorCode;

	public SecretKeyGenerationException() {

	}

	/**
	 * 
	 * @Title: SecretKeyGenerationException
	 * @Description: 构造一个有详细错误信息的异常,秘钥生成失败会抛出该异常
	 * @param: errorMsg
	 * @author: 宁黎
	 *          2019下午1:35:11
	 * 
	 */
	public SecretKeyGenerationException(String errorMsg) {
		super(errorMsg);
	}

	/**
	 * 
	 * @Title: SecretKeyGenerationException
	 * @Description: 构造一个有详细信息和错误码的异常,秘钥生成失败会抛出该异常
	 * @param: errorCode
	 * @param: errorMsg
	 * @author: 宁黎
	 *          2019下午1:35:36
	 * 
	 */
	public SecretKeyGenerationException(String errorCode, String errorMsg) {
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
