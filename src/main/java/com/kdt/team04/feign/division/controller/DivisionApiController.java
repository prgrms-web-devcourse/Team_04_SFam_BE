package com.kdt.team04.feign.division.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.feign.division.dto.DivisionApiResponse;
import com.kdt.team04.feign.division.dto.DivisionRequest;
import com.kdt.team04.feign.division.service.DivisionService;

@RestController
public class DivisionApiController {

	private final DivisionService divisionService;

	public DivisionApiController(DivisionService divisionService) {
		this.divisionService = divisionService;
	}

	@GetMapping("/api/divisions")
	public ApiResponse<List<DivisionApiResponse.Feature>> getDivisions(DivisionRequest divisionRequest) {
		System.out.println(divisionRequest);
		return new ApiResponse<>(divisionService.getDivisions(divisionRequest)
			.response()
			.result()
			.featureCollection()
			.features());
	}
}
