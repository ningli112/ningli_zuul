package com.gateway.ninglizuul.dto;

/**
 * @ClassName: BaseJsonReturnDTO
 * @Description:返回JSON数据的基本模型
 * @author: 宁黎
 * @date: 2019年5月13日 下午4:05:01
 */
public class BaseJsonReturnDTO<T> {
	/**
	 * 状态码
	 */
	private int code;
	/**
	 * 消息
	 */
	private String msg;
	/**
	 * 数据条数
	 */
	private Long count;
	/**
	 * 业务数据
	 */
	private T data;

	public BaseJsonReturnDTO() {

	}

	/**
	 * 
	 * @Title: BaseJsonReturnDTO
	 * @Description: 初始化带有状态码和消息的返回实例
	 * @param: code
	 * @param: msg
	 * @author: 宁黎 2019下午4:05:47
	 * 
	 */
	public BaseJsonReturnDTO(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	/**
	 * 
	 * @Title: BaseJsonReturnDTO
	 * @Description: 初始化带有桩体吗和消息、数据条数、业务数据的返回实例
	 * @param: code
	 * @param: msg
	 * @param: count
	 * @param: data
	 * @author: 宁黎 2019下午4:06:59
	 * 
	 */
	public BaseJsonReturnDTO(int code, String msg, Long count, T data) {
		this.code = code;
		this.msg = msg;
		this.count = count;
		this.data = data;
	}

	/**
	 * 
	 * @Title: BaseJsonReturnDTO
	 * @Description: 初始化带有状态码和消、业务数据的状态值
	 * @param: code
	 * @param: msg
	 * @param: data
	 * @author: 宁黎 2019下午4:08:00
	 * 
	 */
	public BaseJsonReturnDTO(int code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
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

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
