package com.migros.couriertracking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migros.couriertracking.controller.CourierController;
import com.migros.couriertracking.controller.CourierInfoDTO;
import com.migros.couriertracking.domain.Courier;
import com.migros.couriertracking.domain.Store;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourierApplicationTests {

	@Autowired
	private CourierController courierController;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void contextLoads() {
		assertThat(courierController).isNotNull();
	}

	@Test
	public void trackCourier_ValidEntranceTest() throws Exception {
		Date date = new Date(1632592010867L);
		CourierInfoDTO courierInfoDTO = CourierInfoDTO.builder().courierId(1L).lat(40.9923306).lng(29.1244228).time(date).build();

		ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
				.post("/api/courier/track-courier")
				.content(asJsonString(courierInfoDTO))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		perform.andExpect(status().isOk());
		byte[] contentAsByteArray = perform.andReturn().getResponse().getContentAsByteArray();
		Store store = new ObjectMapper().readValue(contentAsByteArray, Store.class);
		assertThat(store).isNotNull();
		assertThat(store.getName()).isEqualTo("Ataşehir MMM Migros");
		assertThat(store.getLat()).isEqualTo(40.9923307);
		assertThat(store.getLng()).isEqualTo(29.1244229);
	}

	/**
	 * test for reentry if courier is in the store's area multiple times in one minute.
	 */
	@Test
	public void trackCourier_InvalidEntranceTest() throws Exception {
		Date date = new Date(1632572010867L);  // 15:13:30
		Date date2 = new Date(1632572030867L);  // 15:13:50
		CourierInfoDTO courierInfoDTO = CourierInfoDTO.builder().courierId(1L).lat(40.9923306).lng(29.1244228).time(date).build();
		CourierInfoDTO courierInfoDTO2 = CourierInfoDTO.builder().courierId(1L).lat(40.9923305).lng(29.1244229).time(date2).build();

		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/courier/track-courier")
				.content(asJsonString(courierInfoDTO))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
				.post("/api/courier/track-courier")
				.content(asJsonString(courierInfoDTO2))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		perform.andExpect(status().isNotFound());
		String contentAsString = perform.andReturn().getResponse().getContentAsString();
		assertThat(contentAsString).isEqualTo("NO_ENTRANCE");
	}

	/**
	 * test for reentry if courier far away from the store
	 */
	@Test
	public void trackCourier_InvalidEntrance2Test() throws Exception {
		Date date = new Date(1632572010867L);  // 15:13:30
		CourierInfoDTO courierInfoDTO = CourierInfoDTO.builder().courierId(1L).lat(41.9923306).lng(26.1244228).time(date).build();

		ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
				.post("/api/courier/track-courier")
				.content(asJsonString(courierInfoDTO))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		perform.andExpect(status().isNotFound());
		String contentAsString = perform.andReturn().getResponse().getContentAsString();
		assertThat(contentAsString).isEqualTo("NO_ENTRANCE");
	}

	@Test
	public void getTotalTravelDistanceTest() throws Exception {
		Date date = new Date(1632572010867L);  // 15:13:30
		CourierInfoDTO courierInfoDTO = CourierInfoDTO.builder().courierId(1L).lat(41.9923306).lng(26.1244228).time(date).build();
		Date date2 = new Date(1632572011867L);  // 15:13:31
		CourierInfoDTO courierInfoDTO2 = CourierInfoDTO.builder().courierId(1L).lat(42.9923306).lng(27.1244228).time(date2).build();

		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/courier/track-courier")
				.content(asJsonString(courierInfoDTO))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/courier/track-courier")
				.content(asJsonString(courierInfoDTO2))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
				.get("/api/courier/total-travel-distance")
				.param("courierId", "1")
				.accept(MediaType.APPLICATION_JSON));


		perform.andExpect(status().isOk());
		String contentAsString = perform.andReturn().getResponse().getContentAsString();
		assertThat(contentAsString).isEqualTo("138.15298949150167");

		ResultActions courierException = mockMvc.perform(MockMvcRequestBuilders
				.get("/api/courier/total-travel-distance")
				.param("courierId", "22")
				.contentType(MediaType.APPLICATION_JSON));

		Exception resolvedException = courierException.andReturn().getResolvedException();
		assert resolvedException != null;
		assertThat(resolvedException.getMessage()).isEqualTo("COURIER_NOT_FOUND");
	}

	public String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
