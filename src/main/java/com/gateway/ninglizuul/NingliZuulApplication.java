package com.gateway.ninglizuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.gateway.ninglizuul.filter.AESEncryptRequestFilter;
import com.gateway.ninglizuul.filter.AESEncryptResponseFilter;
import com.gateway.ninglizuul.filter.RSAEncryptRequestFilter;
import com.gateway.ninglizuul.filter.RSAEncryptResponseFilter;

@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
@EnableFeignClients
public class NingliZuulApplication {
	public static void main(String[] args) {
		SpringApplication.run(NingliZuulApplication.class, args);

	}

	@Bean
	public RSAEncryptRequestFilter rsaEncryptRequestFilter() {
		return new RSAEncryptRequestFilter();
	}

	@Bean
	public RSAEncryptResponseFilter rsaEcryptResponseFilter() {
		return new RSAEncryptResponseFilter();
	}

	@Bean
	public AESEncryptRequestFilter aesEncryptRequestFilter() {
		return new AESEncryptRequestFilter();
	}

	@Bean
	public AESEncryptResponseFilter aesEncrypteResponseFilter() {
		return new AESEncryptResponseFilter();
	}

}
