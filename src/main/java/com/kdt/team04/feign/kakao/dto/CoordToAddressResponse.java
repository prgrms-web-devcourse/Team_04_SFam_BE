package com.kdt.team04.feign.kakao.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

public record CoordToAddressResponse(Meta meta, List<Documents> documents) {
	public record Meta(
		@JsonProperty("total_count")
		Integer totalCount
	) {
	}

	public record Documents(
		@JsonProperty("road_address")
		RoadAddress roadAddress,
		Address address) {
	}

	public record RoadAddress(
		@JsonProperty("address_name")
		String address_name,

		@JsonProperty("region_1depth_name")
		String region_1depth_name,

		@JsonProperty("region_2depth_name")
		String region_2depth_name,

		@JsonProperty("region_3depth_name")
		String region_3depth_name,

		@JsonProperty("road_name")
		String road_name,

		@JsonProperty("main_building_no")
		String main_building_no,
		@JsonProperty("sub_building_no")
		String sub_building_no,

		@JsonProperty("building_name")
		String building_name,

		@JsonProperty("zip_no")
		String zip_no
	) {
	}

	@Builder
	public record Address(
		@JsonProperty("address_name")
		String addressName,

		@JsonProperty("region_1depth_name")
		String region1DepthName,

		@JsonProperty("region_2depth_name")
		String region2DepthName,

		@JsonProperty("region_3depth_name")
		String region3DepthName,

		@JsonProperty("mountain_yn")
		String mountainYn,

		@JsonProperty("main_address_no")
		String mainAddressNo,

		@JsonProperty("sub_address_no")
		String subAddressNo,

		@JsonProperty("zip_code")
		String zip_code
	) {
	}
}
