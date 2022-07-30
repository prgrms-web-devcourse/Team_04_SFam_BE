package com.kdt.team04.feign.division.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public record DivisionApiResponse(Response response) {

	public record Response(Result result) {

	}

	public record Result(FeatureCollection featureCollection) {
	}

	public record FeatureCollection(String type, List<Feature> features) {

	}

	public record Feature(Properties properties) {

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record Properties(
		String sig_cd,
		String full_nm,
		String sig_kor_nm,
		String ctprvn_cd,
		String ctp_kor_nm
	) {
	}
}
