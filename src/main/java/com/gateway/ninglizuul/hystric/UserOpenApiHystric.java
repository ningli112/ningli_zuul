package com.gateway.ninglizuul.hystric;

import org.springframework.stereotype.Component;

import com.gateway.ninglizuul.dto.BaseJsonReturnDTO;
import com.gateway.ninglizuul.dto.param.UserOpenApiParamDTO;
import com.gateway.ninglizuul.enums.code.BaseJsonReturnCodeEnum;
import com.gateway.ninglizuul.fegin.UserOpenApiFegin;

@Component
public class UserOpenApiHystric implements UserOpenApiFegin {
	/**
	 * 
	 * <p>
	 * Title: checkAppId
	 * </p>
	 * <p>
	 * Description:调用fegin服务检查APPID 和APPKEY失败时候返回
	 * </p>
	 * 
	 * @author 宁黎
	 * @param openApiParamDTO
	 * @return
	 * @see com.gateway.ninglizuul.fegin.UserOpenApiFegin#checkAppId(com.gateway.ninglizuul.dto.param.UserOpenApiParamDTO)
	 */
	@Override
	public BaseJsonReturnDTO<String> checkAppId(UserOpenApiParamDTO openApiParamDTO) {
		return new BaseJsonReturnDTO<String>(BaseJsonReturnCodeEnum.SERVICE_ERROR.getCode(),
				BaseJsonReturnCodeEnum.SERVICE_ERROR.getMsg());
	}

}
