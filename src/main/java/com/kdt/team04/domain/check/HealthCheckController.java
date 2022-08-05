package com.kdt.team04.domain.check;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Health 체크 API")
@RestController
public class HealthCheckController {

	@Operation(summary = "서버 상태 판단", description = "실행이 잘 되는지 서버 상태를 체크합니다.")
	@GetMapping("/health")
	public String check() {
		return LocalDateTime.now().toString();
	}

}
