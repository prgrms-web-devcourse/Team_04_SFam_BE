package com.kdt.team04.feign.division.service;

import org.springframework.stereotype.Service;

import com.kdt.team04.common.config.DivisionApiProperties;
import com.kdt.team04.feign.division.client.DivisionApiClient;
import com.kdt.team04.feign.division.dto.DivisionApiResponse;
import com.kdt.team04.feign.division.dto.DivisionRequest;

@Service
public class DivisionService {

	private final DivisionApiClient divisionApiClient;
	private final DivisionApiProperties divisionApiProperties;

	public DivisionService(DivisionApiClient divisionApiClient, DivisionApiProperties divisionApiProperties) {
		this.divisionApiClient = divisionApiClient;
		this.divisionApiProperties = divisionApiProperties;
	}

	public DivisionApiResponse getDivisions(DivisionRequest request) {
		return divisionApiClient.getDivisions(divisionApiProperties.domain(), divisionApiProperties.key(), request.data(), request.attrfilter(), request.geomfilter());
	}
}
