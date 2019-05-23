package com.gateway.ninglizuul.fegin;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gateway.ninglizuul.dto.BaseJsonReturnDTO;
import com.gateway.ninglizuul.dto.param.UserOpenApiParamDTO;
import com.gateway.ninglizuul.hystric.UserOpenApiHystric;

@FeignClient(value = "user-center", fallback = UserOpenApiHystric.class, path = "/api/user/open")
public interface UserOpenApiFegin {

	@PostMapping("/v1/check/appid")
	BaseJsonReturnDTO<String> checkAppId(@RequestBody UserOpenApiParamDTO openApiParamDTO);
}
