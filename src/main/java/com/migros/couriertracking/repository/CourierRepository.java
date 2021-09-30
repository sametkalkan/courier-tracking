package com.migros.couriertracking.repository;

import com.migros.couriertracking.domain.Courier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CourierRepository {

	private Map<Long, Courier> dummyCourierList = new HashMap<>();

	public CourierRepository() {
		dummyCourierList.put(1L, Courier.builder().id(1L).build());
		dummyCourierList.put(2L, Courier.builder().id(2L).build());
		dummyCourierList.put(3L, Courier.builder().id(3L).build());
		dummyCourierList.put(4L, Courier.builder().id(4L).build());
		dummyCourierList.put(5L, Courier.builder().id(5L).build());
	}

	public Optional<Courier> getCourierById(Long id) {
		Courier courier = dummyCourierList.get(id);
		if (courier != null) {
			return Optional.of(courier);
		}
		return Optional.empty();
	}
}
