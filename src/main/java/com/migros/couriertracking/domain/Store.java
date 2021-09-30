package com.migros.couriertracking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
public class Store {
	private String name;
	private Double lat;
	private Double lng;

	public Store(Builder builder) {
		this.name = builder.name;
		this.lat = builder.lat;
		this.lng = builder.lng;
	}

	public Store() {
	}

	public static Builder builder() {
		return new Builder();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public static class Builder {
		private String name;
		private Double lat;
		private Double lng;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder lat(Double lat) {
			this.lat = lat;
			return this;
		}

		public Builder lng(Double lng) {
			this.lng = lng;
			return this;
		}

		public Store build() {
			return new Store(this);
		}
	}
}
