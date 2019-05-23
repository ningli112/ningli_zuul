package com.gateway.ninglizuul.enums.code;

/**
 * @ClassName: BaseJsonReturnCodeEnum
 * @Description:基础返回码枚举类
 * @author: 宁黎
 * @date: 2019年5月13日 下午4:51:53
 */
public enum BaseJsonReturnCodeEnum {
	/**
	 * 请求成功
	 */
	SUCCESS(0, "SUCCESS"),
	/**
	 * APPID不存在
	 */
	APPID_NO_EXIST(1001, "APPID不存在"),
	/**
	 * appkey错误
	 */
	APP_SECRET_ERROR(1002, "APPI Secret错误"),
	/**
	 * 访问令牌不存在
	 */
	ACCESS_TOKEN_NO_EXIST(1003, "accessToken 不存在"),
	/**
	 * 密文数据错误或损坏
	 */
	CIPHERTEXT_DAMAGE(1004, "密文不正确或密文损坏"),
	/**
	 * 服务调用失败
	 */
	SERVICE_ERROR(1005, "服务调用出错");
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
