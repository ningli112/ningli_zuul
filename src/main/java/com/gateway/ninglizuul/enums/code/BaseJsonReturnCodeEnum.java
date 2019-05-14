package com.gateway.ninglizuul.enums.code;

/**
 * @ClassName: BaseJsonReturnCodeEnum
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 宁黎
 * @date: 2019年5月13日 下午4:51:53
 */
public enum BaseJsonReturnCodeEnum {
	SUCCESS(0, "SUCCESS"), APPID_NO_EXIST(1001, "APPID不存在"), APP_SECRET_ERROR(1002, "APPI Secret错误"),
	ACCESS_TOKEN_NO_EXIST(1003, "accessToken 不存在"), CIPHERTEXT_DAMAGE(1004, "密文不正确或密文损坏");
	/**
	 * 状态值
	 */
	private int code;
	/**
	 * 状态消息
	 */
	private String msg;

	private BaseJsonReturnCodeEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
