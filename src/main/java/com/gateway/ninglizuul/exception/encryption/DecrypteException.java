package com.gateway.ninglizuul.exception.encryption;

/**
 * 
 * @ClassName: DecrypteException
 * @Description:解密失败抛出此异常
 * @author: 宁黎
 * @date: 2019年4月30日 下午6:24:42
 */
public class DecrypteException extends Exception {

	private static final long serialVersionUID = 7127390289010097195L;
	/**
	 * 错误码
	 */
	private String errorCode;

	public DecrypteException() {

	}

	/**
	 * 
	 * @Title: DecrypteException
	 * @Description: 构造一个有详细错误信息的异常，解密失败抛出此异常
	 * @param: errorMsg
	 * @author: 宁黎
	 *          2019下午1:35:11
	 * 
	 */
	public DecrypteException(String errorMsg) {
		super(errorMsg);
	}

	/**
	 * 
	 * @Title: DecrypteException
	 * @Description: 构造一个有详细信息和错误码的异常，解密失败抛出此异常
	 * @param: errorCode
	 * @param: errorMsg
	 * @author: 宁黎
	 *          2019下午1:35:36
	 * 
	 */
	public DecrypteException(String errorCode, String errorMsg) {
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
