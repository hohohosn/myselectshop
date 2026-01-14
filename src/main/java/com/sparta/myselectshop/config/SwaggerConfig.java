package com.sparta.myselectshop.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 설정
 * - API 문서 자동 생성
 * - JWT 인증 지원
 * - 접속: http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		String jwtSchemeName = "JWT Token";

		// JWT 보안 스키마 설정
		SecurityRequirement securityRequirement = new SecurityRequirement()
			.addList(jwtSchemeName);

		Components components = new Components()
			.addSecuritySchemes(jwtSchemeName, new SecurityScheme()
				.name(jwtSchemeName)
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT")
				.description("JWT 토큰을 입력하세요. (Bearer 제외)")
			);

		return new OpenAPI()
			.info(apiInfo())
			.addSecurityItem(securityRequirement)
			.components(components);
	}

	private Info apiInfo() {
		return new Info()
			.title("MySelectShop API")
			.description("""
				나만의 선택 쇼핑몰 API 명세서
				
				## 주요 기능
				- 회원가입 및 로그인 (JWT)
				- 상품 검색 및 관심상품 등록
				- 폴더 관리
				- 상품 가격 비교
				- 관리자 기능
				
				## 인증 방법
				1. /api/user/login 으로 로그인
				2. 응답으로 받은 JWT 토큰을 복사
				3. 우측 상단 'Authorize' 버튼 클릭
				4. 토큰 입력 후 'Authorize' 클릭
				""")
			.version("1.0.0")
			.contact(new Contact()
				.name("Sparta Coding Club")
				.email("support@spartacodingclub.kr")
				.url("https://spartacodingclub.kr"))
			.license(new License()
				.name("Apache 2.0")
				.url("https://www.apache.org/licenses/LICENSE-2.0"));
	}
}