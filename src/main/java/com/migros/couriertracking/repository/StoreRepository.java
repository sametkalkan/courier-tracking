package com.migros.couriertracking.repository;

import com.migros.couriertracking.domain.Store;

import java.util.List;

public class StoreRepository {

	private List<Store> storeList;

	public StoreRepository(List<Store> storeList) {
		this.storeList = storeList;
	}

	public List<Store> getAll() {
		return storeList;
	}
}
