package com.migros.couriertracking.controller;

import com.migros.couriertracking.domain.Store;
import com.migros.couriertracking.service.CourierTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/courier")
@RequiredArgsConstructor
@Slf4j
public class CourierController {
	private final CourierTrackingService courierTrackingService;

	@PostMapping("/track-courier")
	public ResponseEntity<?> trackCourier(@RequestBody CourierInfoDTO courierInfoDTO) {
		courierTrackingService.updateCourierTravelDistance(courierInfoDTO);

		Optional<Store> storeOptional = courierTrackingService.trackCourier(courierInfoDTO);
		if (storeOptional.isPresent()) {
			logEntrance(courierInfoDTO, storeOptional.get());
			return ResponseEntity.ok(storeOptional.get());
		}
		return new ResponseEntity<>("NO_ENTRANCE", HttpStatus.NOT_FOUND);
	}

	@GetMapping("/total-travel-distance")
	public ResponseEntity<Double> getTotalTravelDistance(@RequestParam Long courierId) {
		double totalTravelDistance = courierTrackingService.getTotalTravelDistance(courierId);
		return ResponseEntity.ok(totalTravelDistance);
	}


	private void logEntrance(CourierInfoDTO courierInfoDTO, Store store) {
		log.info("\nCourier Entered Store Area\n" +
				"CourierId: {}\n" +
				"Time: {}\n" +
				"Store: {}\n", courierInfoDTO.getCourierId(), courierInfoDTO.getTime(), store.getName());
	}

}
