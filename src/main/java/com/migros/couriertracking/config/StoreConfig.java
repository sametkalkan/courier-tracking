package com.migros.couriertracking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migros.couriertracking.domain.Store;
import com.migros.couriertracking.repository.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.net.URL;
import java.util.*;

@Configuration
@Slf4j
public class StoreConfig {
	@Value("${storelist.path}")
	private String storeListPath;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Bean
	public StoreRepository getStore() {
		List<Store> storeList = createStoreList();
		return new StoreRepository(storeList);
	}

	/**
	 * reads stores from given json file. The path of json file is specified in "application.properties" file in resources.
	 * @return list of store
	 */
	private List<Store> createStoreList() {
		List<Store> storeList = new ArrayList<>();
		URL storeUrl = Thread.currentThread().getContextClassLoader().getResource(storeListPath);
		try {
			assert storeUrl != null;
			List list = objectMapper.readValue(new File(storeUrl.toURI()), List.class);
			for (Object item : list) {
				LinkedHashMap<String, ?> map = (LinkedHashMap<String, ?>) item;
				String name = (String) map.get("name");
				Double lat = (Double) map.get("lat");
				Double lng = (Double) map.get("lng");
				Store build = Store.builder().name(name).lat(lat).lng(lng).build();
				storeList.add(build);
			}
		} catch (Exception e) {
			log.error("Exception occurred while reading the stores");
		}
		return storeList;
	}
}
