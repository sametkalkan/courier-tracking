package com.migros.couriertracking.service;

import com.migros.couriertracking.controller.CourierInfoDTO;
import com.migros.couriertracking.domain.Courier;
import com.migros.couriertracking.domain.Store;
import com.migros.couriertracking.exception.RecordNotFoundException;
import com.migros.couriertracking.repository.CourierRepository;
import com.migros.couriertracking.repository.StoreRepository;
import com.migros.couriertracking.utils.MathUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourierTrackingService {
	private final StoreRepository storeRepository;
	private final CourierRepository courierRepository;

	private static final double RADIUS = 100;
	private static final long REENTRANCE_TIME = TimeUnit.MINUTES.toMillis(1);

	public Optional<Store> trackCourier(CourierInfoDTO courierInfoDTO) {
		Double lat = courierInfoDTO.getLat();
		Double lng = courierInfoDTO.getLng();
		Optional<Store> storeOptional = findStore(lat, lng);  // find a store that the courier is close to.
		if (storeOptional.isPresent()) {  // if a store is found
			boolean validEntrance = isValidEntrance(courierInfoDTO);
			if (validEntrance) {
				return storeOptional;
			}
		}
		return Optional.empty();
	}

	public double getTotalTravelDistance(Long courierId) {
		Optional<Courier> courierById = courierRepository.getCourierById(courierId);
		if (courierById.isPresent()) {
			return courierById.get().getTotalTravel();
		}
		throw new RecordNotFoundException("COURIER_NOT_FOUND");
	}

	public void updateCourierTravelDistance(CourierInfoDTO courierInfoDTO) {
		Long courierId = courierInfoDTO.getCourierId();
		Double currentLat = courierInfoDTO.getLat();
		Double currentLng = courierInfoDTO.getLng();

		Optional<Courier> optionalPersistentCourier = courierRepository.getCourierById(courierId);
		if (optionalPersistentCourier.isPresent()) {
			Courier persistentCourier = optionalPersistentCourier.get();
			Point2D.Double lastPoint = persistentCourier.getLastPoint();
			persistentCourier.setLastPoint(new Point2D.Double(currentLat, currentLng));
			if (lastPoint == null) {
				return;
			}
			double distance = MathUtils.calculateDistance(currentLat, currentLng, lastPoint.getX(), lastPoint.getY());
			persistentCourier.setTotalTravel(persistentCourier.getTotalTravel() + distance);
		}
	}

	/**
	 * if a courier enters a store area, this method checks if the courier is in that area before 1 minute <br>
	 * in order to trace it, a {@link Date} object is stored in {@link Courier } object <br>
	 * if difference between last seen and current time is greater than 1 minute, the entrance is counted.
	 */
	private boolean isValidEntrance(CourierInfoDTO courierInfoDTO) {
		Long courierId = courierInfoDTO.getCourierId();
		Date courierTime = courierInfoDTO.getTime();
		Optional<Courier> courierOptional = courierRepository.getCourierById(courierId);
		if (courierOptional.isPresent()) {
			Courier courier = courierOptional.get();
			if (courier.getEntranceDate() == null) {
				courier.setEntranceDate(courierTime);
				return true;
			} else {
				Date lastEntranceTime = courier.getEntranceDate();
				long diff = Math.abs(courierTime.getTime() - lastEntranceTime.getTime());
				return diff > REENTRANCE_TIME;  // return true if over 1 minute
			}
		} else {
			throw new RecordNotFoundException("COURIER_NOT_FOUND");
		}
	}

	/**
	 * finds a store that the courier is close to <br>
	 * measures distance between the courier and each store one by one
	 */
	private Optional<Store> findStore(Double courierLat, Double courierLng) {
		List<Store> all = storeRepository.getAll();
		for (Store store : all) {
			Double storeLat = store.getLat();
			Double storeLng = store.getLng();

			double distance = MathUtils.calculateDistance(courierLat, courierLng, storeLat, storeLng);
			if (distance < RADIUS) {
				return Optional.of(store);
			}
		}
		return Optional.empty();
	}
}
