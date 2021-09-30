package com.migros.couriertracking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migros.couriertracking.controller.CourierInfoDTO;
import com.migros.couriertracking.domain.Courier;
import com.migros.couriertracking.domain.Store;
import com.migros.couriertracking.exception.RecordNotFoundException;
import com.migros.couriertracking.repository.CourierRepository;
import com.migros.couriertracking.repository.StoreRepository;
import com.migros.couriertracking.service.CourierTrackingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Slf4j
public class CourierTrackingServiceTest {

	@InjectMocks
	private CourierTrackingService courierTrackingService;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private CourierRepository courierRepository;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setUp() {
		List<Store> storeList = createStoreList();
		Mockito.when(storeRepository.getAll()).thenReturn(storeList);
	}

	@Test
	public void trackCourier_ValidEntranceTest() {
		Courier dummyCourier = Courier.builder().id(1L).build();
		Mockito.when(courierRepository.getCourierById(1L)).thenReturn(Optional.of(dummyCourier));

		Date date = new Date(1632572010867L);
		CourierInfoDTO courierInfoDTO = CourierInfoDTO.builder().courierId(1L).lat(40.9923306).lng(29.1244228).time(date).build();

		Optional<Store> storeOptional = courierTrackingService.trackCourier(courierInfoDTO);
		assertThat(storeOptional.isPresent()).isTrue();
		Store store = storeOptional.get();
		assertThat(store.getName()).isEqualTo("Ataşehir MMM Migros");
	}

	@Test
	public void trackCourier_InvalidEntranceTest() {
		Courier dummyCourier = Courier.builder().id(1L).build();
		Mockito.when(courierRepository.getCourierById(1L)).thenReturn(Optional.of(dummyCourier));

		Date date = new Date(1632572010867L);  // 15:13:30
		Date date2 = new Date(1632572030867L);  // 15:13:50
		CourierInfoDTO courierInfoDTO = CourierInfoDTO.builder().courierId(1L).lat(40.9923306).lng(29.1244228).time(date).build();
		CourierInfoDTO courierInfoDTO2 = CourierInfoDTO.builder().courierId(1L).lat(40.9923305).lng(29.1244229).time(date2).build();

		courierTrackingService.trackCourier(courierInfoDTO);
		Optional<Store> storeOptional = courierTrackingService.trackCourier(courierInfoDTO2);
		assertThat(storeOptional.isPresent()).isFalse();
	}

	@Test
	public void trackCourier_InvalidEntrance2Test() {
		Courier dummyCourier = Courier.builder().id(1L).build();
		Mockito.when(courierRepository.getCourierById(1L)).thenReturn(Optional.of(dummyCourier));

		Date date = new Date(1632572010867L);  // 15:13:30
		CourierInfoDTO courierInfoDTO = CourierInfoDTO.builder().courierId(1L).lat(41.9923306).lng(26.1244228).time(date).build();

		Optional<Store> storeOptional = courierTrackingService.trackCourier(courierInfoDTO);
		assertThat(storeOptional.isPresent()).isFalse();
	}

	@Test
	public void updateCourierTravelDistanceTest() {
		Courier dummyCourier = Courier.builder().id(1L).build();
		Mockito.when(courierRepository.getCourierById(1L)).thenReturn(Optional.of(dummyCourier));

		Date date = new Date(1632572010867L);  // 15:13:30
		CourierInfoDTO courierInfoDTO = CourierInfoDTO.builder().courierId(1L).lat(41.9923306).lng(26.1244228).time(date).build();
		Date date2 = new Date(1632572011867L);  // 15:13:31
		CourierInfoDTO courierInfoDTO2 = CourierInfoDTO.builder().courierId(1L).lat(42.9923306).lng(27.1244228).time(date2).build();

		courierTrackingService.updateCourierTravelDistance(courierInfoDTO);
		courierTrackingService.updateCourierTravelDistance(courierInfoDTO2);
		double totalTravelDistance = courierTrackingService.getTotalTravelDistance(1L);
		assertThat(dummyCourier.getTotalTravel()).isEqualTo(138.15298949150167);
		assertThat(dummyCourier.getTotalTravel()).isEqualTo(totalTravelDistance);

		Assertions.assertThrows(RecordNotFoundException.class, () -> courierTrackingService.getTotalTravelDistance(22L));
	}

	private List<Store> createStoreList() {
		List<Store> storeList = new ArrayList<>();
		URL storeUrl = Thread.currentThread().getContextClassLoader().getResource("stores.json");
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
