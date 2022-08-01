package com.kdt.team04.feign.division.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kdt.team04.feign.division.dto.DivisionApiResponse;

@FeignClient(name = "division", url = "https://api.vworld.kr/req/data")
public interface DivisionApiClient {

	@GetMapping("?request=getfeature&size=1000&page=1&geometry=false&attribute=true&crs=EPSG:3857")
	DivisionApiResponse getDivisions(
		@RequestParam(name = "domain") String domain,
		@RequestParam(name = "key") String key,
		@RequestParam(name = "data") String data,
		@RequestParam(name = "attrfilter", required = false) String attrfilter,
		@RequestParam(name = "geomfilter") String geomfilter);

}
